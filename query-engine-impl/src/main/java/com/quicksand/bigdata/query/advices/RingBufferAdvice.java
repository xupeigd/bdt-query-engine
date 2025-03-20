package com.quicksand.bigdata.query.advices;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * RingBufferAdvice
 *
 * @author xupei
 * @date 2022/8/9
 */
@Slf4j
@Aspect
public class RingBufferAdvice {


    @Before("execution(public * com.lmax.disruptor.RingBuffer.publish())")
    public void ringBuffer() {

    }

}
