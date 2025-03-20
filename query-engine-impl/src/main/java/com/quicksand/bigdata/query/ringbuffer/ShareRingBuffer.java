package com.quicksand.bigdata.query.ringbuffer;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.quicksand.bigdata.query.utils.TraceFuture;
import com.quicksand.bigdata.query.vos.ShareRingBufferEntry;
import com.quicksand.bigdata.query.vos.UniqFlaged;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * RedisRingBuffer
 *
 * @author xupei
 * @date 2022/8/9
 */
public interface ShareRingBuffer<T extends UniqFlaged> {

    /**
     * 发布到环上
     *
     * @param t 实体
     */
    Ctx<T> publish(T t);

    T fetch(String uniqFlag);

    /**
     * 获取RingBuffer名称
     *
     * @return String
     */
    String getName();

    /**
     * 获取本地标识
     *
     * @return String
     */
    String getLocalFlag();


    /**
     * 注册处理器
     *
     * @param handlers instancd of  handler
     */
    void registHandlers(EventHandler<T>... handlers) throws InterruptedException;

    /**
     * 初始化RingBuffer
     */
    ShareRingBuffer<T> init(String name, int size);

    void modifyEntry(T t);

    interface EventHandler<T extends UniqFlaged> {

        void onEvent(T event) throws Exception;

        void finalDeal(Ctx<T> ctx) throws Exception;

    }

    @Slf4j
    class Ctx<T extends UniqFlaged>
            extends Semaphore {

        @Getter
        boolean semaphoreInited = false;

        /**
         * 位点
         */
        @Getter
        @Setter
        long sequence;

        @Getter
        long createMills;

        @Getter
        @Setter
        long shareMills;

        /**
         * 值
         */
        @Getter
        @Setter
        T value;

        /**
         * 是否代理远端
         */
        @Getter
        boolean proxy;

        public Ctx(boolean proxy) {
            super(1);
            createMills = System.currentTimeMillis();
            this.proxy = proxy;
            try {
                acquire();
                semaphoreInited = true;
            } catch (InterruptedException e) {
                log.error("Ctx inited fail !", e);
            }
        }

        public void syncWait(long mills) {
            //noinspection ResultOfMethodCallIgnored
            Try.run(() -> tryAcquire(mills, TimeUnit.MILLISECONDS))
                    .onFailure(ex -> log.warn(String.format("sync query timeout ! flag:%s`", value.getFlag()), ex));
        }

    }

    @Slf4j
    @SuppressWarnings("unchecked")
    @NoArgsConstructor
    class RedisRingBuffer<T extends UniqFlaged>
            implements ShareRingBuffer<T> {

        /**
         * 实体数据
         */
        public static final String RDS_ENTRY = "BDT:SRB:ERY:%s:%s";
        /**
         * 全局性的LCK
         */
        private static final String RDS_LCK = "BDT:SRB:%s:LCK";
        /**
         * 实体数据List/Queue
         */
        private static final String RDS_NAME = "BDT:SRB:%s";
        /**
         * Queue的元数据
         */
        private static final String RDS_META = "BDT:SRB:META:%s";
        static String bufferMeta;
        static String curReadIndexKey;
        @Getter
        boolean inited = false;
        @Getter
        String name;
        @Getter
        int size;
        @Getter
        String localFlag = UUID.randomUUID().toString().replace("-", "");
        @Setter
        @Getter
        boolean shutdown = false;
        @Setter
        RedisTemplate<String, T> redisTemplate;
        @Setter
        RedisTemplate<String, String> stringRedisTemplate;
        @Setter
        RedisTemplate<String, Long> longRedisTemplate;
        RingBuffer<Ctx<T>> ringBuffer;
        @Setter
        EventHandler<T>[] handlers;

        @Override
        public Ctx<T> publish(T t) {
            publish2Remote(t);
            return publish2Local(t);
        }

        @Override
        public T fetch(String uniqFlag) {
            String entryKey = String.format(RDS_ENTRY, name, uniqFlag);
            if (Objects.equals(true, redisTemplate.hasKey(entryKey))) {
                return (T) redisTemplate.opsForHash().get(entryKey, "OBJ");
            }
            return null;
        }

        private void publish2Remote(T t) {
            String hitFlag = t.getFlag();
            String entryKey = String.format(RDS_ENTRY, name, hitFlag);
            stringRedisTemplate.opsForHash().put(entryKey, "SLOC", localFlag);
            redisTemplate.opsForHash().put(entryKey, "OBJ", t);
            stringRedisTemplate.opsForZSet().add(String.format(RDS_NAME, name), hitFlag, System.currentTimeMillis() + 100L);
            stringRedisTemplate.expire(entryKey, 25, TimeUnit.HOURS);
        }

        private Ctx<T> publish2Local(T t) {
            long sequence = ringBuffer.next();
            Ctx<T> ctx = ringBuffer.get(sequence);
            ctx.setSequence(sequence);
            ctx.setValue(t);
            ringBuffer.publish(sequence);
            return ctx;
        }

        @Override
        public void registHandlers(EventHandler<T>... handlers) throws InterruptedException {
            if (inited) {
                throw new InterruptedException("ShareRingBuffer inited done ! ");
            }
            if (null == handlers || 0 == handlers.length) {
                throw new InterruptedException("handlers at least one ! ");
            }
            this.handlers = handlers;
        }

        private void createOrLoadRingBuffer() {
            bufferMeta = String.format(RDS_META, name);
            curReadIndexKey = String.format("%s:readIndex", localFlag);
            boolean bufferExist = Objects.equals(true, redisTemplate.hasKey(bufferMeta));
            long createMills = System.currentTimeMillis();
            if (!bufferExist) {
                boolean interrupted = false;
                while (!interrupted) {
                    synchronized (this) {
                        boolean setSuccess = Objects.equals(true, stringRedisTemplate.opsForValue().setIfAbsent(String.format(RDS_LCK, name), String.valueOf(createMills), 1, TimeUnit.SECONDS));
                        if (setSuccess) {
                            bufferExist = Objects.equals(true, redisTemplate.hasKey(bufferMeta));
                            if (bufferExist) {
                                interrupted = true;
                            } else {
                                //尝试放置数据
                                Map<String, String> values = new HashMap<>(8);
                                values.put("size", String.valueOf(size));
                                values.put("name", name);
                                values.put("createMills", JsonUtils.toJsonString(new Date()));
                                values.put("writeIndex", "0");
                                values.put("around", "0");
                                stringRedisTemplate.opsForHash().putAll(bufferMeta, values);
                            }
                        } else {
                            //创建最多尝试5s
                            interrupted = 5000L <= System.currentTimeMillis() - createMills;
                        }
                    }
                }
            }
            //放置自身的读取索引
            Long currentReadIndex = stringRedisTemplate.opsForHash().increment(bufferMeta, curReadIndexKey, 0);
            if (Objects.equals(0L, currentReadIndex)) {
                //回溯3min
                stringRedisTemplate.opsForHash().put(bufferMeta, curReadIndexKey, String.valueOf(createMills - 3 * 60000L));
            }
            //启动守护线程
            TraceFuture.run(() -> {
                while (!shutdown) {
                    Try.run(() -> {
                        String setKey = String.format(RDS_NAME, name);
                        long sIndex = stringRedisTemplate.opsForHash().increment(bufferMeta, curReadIndexKey, 0);
                        long gap = System.currentTimeMillis() - sIndex;
                        Long eIndex = stringRedisTemplate.opsForHash().increment(bufferMeta, curReadIndexKey, gap);
                        Set<String> hits = stringRedisTemplate.opsForZSet().rangeByScore(setKey, sIndex, eIndex);
                        if (!CollectionUtils.isEmpty(hits)) {
                            for (String hit : hits) {
                                String entryKey = String.format(RDS_ENTRY, name, hit);
                                String loc = (String) stringRedisTemplate.opsForHash().get(entryKey, "SLOC");
                                if (!Objects.equals(localFlag, loc)) {
                                    T t = (T) redisTemplate.opsForHash().get(entryKey, "OBJ");
                                    if (t instanceof ShareRingBufferEntry) {
                                        //noinspection rawtypes
                                        ((ShareRingBufferEntry) t).setCurBuffer(this);
                                        TraceFuture.run(() -> log.info("remote hit ! hit:{}", hit));
                                    }
                                    if (null != t) {
                                        publish2Local(t);
                                    } else {
                                        log.warn("object not exist ! entryKey:{}`", hit);
                                    }
                                }
                            }
                        }
                        redisTemplate.expire(bufferMeta, 30, TimeUnit.DAYS);
                        redisTemplate.expire(setKey, 30, TimeUnit.DAYS);
                        TimeUnit.MILLISECONDS.sleep(200L);
                    }).onFailure(ex -> {
                        log.warn("createOrLoadRingBuffer fail ! ", ex);
                    });
                }
            });
        }

        @SuppressWarnings("unchecked")
        @Override
        public ShareRingBuffer<T> init(String name, int size) {
            Assert.notNull(redisTemplate, "");
            Assert.notNull(handlers, "");
            this.name = name;
            this.size = size;
            Disruptor<Ctx<T>> disruptor = new Disruptor<>(() -> new Ctx<>(false), size, DaemonThreadFactory.INSTANCE, ProducerType.MULTI, new BlockingWaitStrategy());
            @SuppressWarnings("rawtypes") WorkHandler[] workHandlers = new WorkHandler[handlers.length];
            for (int i = 0; i < handlers.length; i++) {
                int finalIndex = i;
                workHandlers[i] = (WorkHandler<Ctx<T>>) event -> {
                    handlers[finalIndex].onEvent(event.value);
                    handlers[finalIndex].finalDeal(event);
                };
            }
            disruptor.handleEventsWithWorkerPool(workHandlers);
            disruptor.start();
            ringBuffer = disruptor.getRingBuffer();
            //本地接入与初始化
            createOrLoadRingBuffer();
            inited = true;
            return this;
        }

        @Override
        public void modifyEntry(T t) {
            String entryKey = String.format(RDS_ENTRY, name, t.getFlag());
            redisTemplate.opsForHash().put(entryKey, "OBJ", t);
            //记录附加信息
            Long uc = redisTemplate.opsForHash().increment(entryKey, "UC", 1);
            redisTemplate.opsForHash().put(entryKey, "UC-I" + uc, localFlag);
            redisTemplate.opsForHash().put(entryKey, "UC-T" + uc, System.currentTimeMillis());
        }

    }

}
