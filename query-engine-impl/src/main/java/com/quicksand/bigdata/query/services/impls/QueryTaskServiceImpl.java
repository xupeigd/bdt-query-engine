package com.quicksand.bigdata.query.services.impls;

import com.quicksand.bigdata.query.consts.DataStatus;
import com.quicksand.bigdata.query.consts.JobState;
import com.quicksand.bigdata.query.consts.QueryMode;
import com.quicksand.bigdata.query.consts.TriggerType;
import com.quicksand.bigdata.query.dbvos.ConnectionInfoDBVO;
import com.quicksand.bigdata.query.dms.ConnectionInfoDataManager;
import com.quicksand.bigdata.query.models.ConnectionInfoModel;
import com.quicksand.bigdata.query.models.QueryReqModel;
import com.quicksand.bigdata.query.ringbuffer.ShareRingBuffer;
import com.quicksand.bigdata.query.services.HotLoadingConfig;
import com.quicksand.bigdata.query.services.QueryTaskService;
import com.quicksand.bigdata.query.services.TaskManagerService;
import com.quicksand.bigdata.query.utils.AuthUtil;
import com.quicksand.bigdata.query.utils.TraceFuture;
import com.quicksand.bigdata.query.vos.ConnectionInfoVO;
import com.quicksand.bigdata.query.vos.QueryReqInfoVO;
import com.quicksand.bigdata.query.vos.QueryTaskVO;
import com.quicksand.bigdata.query.vos.ReqControlInfoVO;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.validation.ValidationException;
import java.util.Date;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;

/**
 * QueryTaskServiceImpl
 *
 * @author xupei
 * @date 2022/8/9
 */
@Slf4j
@Service
public class QueryTaskServiceImpl
        implements QueryTaskService {

    @Resource
    HotLoadingConfig hotLoadingConfig;
    @Resource
    TaskManagerService taskManagerService;
    @Resource
    ConnectionInfoDataManager connectionInfoDataManager;
    @Resource
    ShareRingBuffer<QueryTaskVO> queryTaskShareRingBuffer;

    @Override
    public QueryTaskVO query(QueryReqModel model) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        QueryTaskVO task = cover2TaskVo(model);
        ShareRingBuffer.Ctx<QueryTaskVO> ctx = queryTaskShareRingBuffer.publish(task);
        TraceFuture.run(() -> log.info("query submit ! user:[id:{},name:{}],id:{},model:{}", null == userDetail ? 0L : userDetail.getId(),
                null == userDetail ? "" : userDetail.getName(), ctx.getValue().getId(), JsonUtils.toJsonString(model)));
        //根据控制类型，返回结果
        if (hotLoadingConfig.syncEable()
                && Objects.equals(QueryMode.Sync, model.getMode())) {
            ctx.syncWait(Math.min(null == model.getSyncMills() ? 5000L : model.getSyncMills(), hotLoadingConfig.maxSyncMills()));
        } else {
            //异步直接释放
            ctx.release();
        }
        return ctx.getValue();
    }

    private QueryTaskVO cover2TaskVo(QueryReqModel model) {
        return QueryTaskVO.builder()
                .id(UUID.randomUUID().toString().replace("-", ""))
                .voucher(calculateFlag(model))
                .connectionInfo(cover2ConnectionVo(model.getConnectionInfo()))
                .submitTime(new Date())
                .state(JobState.Ready)
                .trigger(TriggerType.AdHoc)
                .queryReq(cover2ReqInfo(model))
                .reqControlInfo(cover2ReqControlInfo(model))
                .curBuffer(queryTaskShareRingBuffer)
                .build();
    }

    private ReqControlInfoVO cover2ReqControlInfo(QueryReqModel model) {
        return ReqControlInfoVO.builder()
                .mode(model.getMode())
                .asyncMills(model.getAsyncMills())
                .syncMills(model.getSyncMills())
                .resultMode(model.getResultMode())
                .build();
    }

    private QueryReqInfoVO cover2ReqInfo(QueryReqModel model) {
        TreeMap<String, Object> params = new TreeMap<>();
        if (!CollectionUtils.isEmpty(model.getParamters())) {
            params.putAll(model.getParamters());
        }
        return QueryReqInfoVO.builder()
                .paramters(model.getParamters())
                .templateSql(model.getTemplateSql())
                .sqlMd5(DigestUtils.md5DigestAsHex(model.getTemplateSql().getBytes()))
                .paramsMd5(DigestUtils.md5DigestAsHex(JsonUtils.toJsonString(params).getBytes()))
                .build();
    }

    @Override
    public QueryTaskVO fetchTask(String id) {
        return taskManagerService.findTask(id);
    }

    private String calculateFlag(QueryReqModel model) {
        String connectionFlag = StringUtils.hasText(model.getConnectionInfo().getFlag())
                ? model.getConnectionInfo().getFlag()
                : DigestUtils.md5DigestAsHex((JsonUtils.toJsonString(model.getConnectionInfo())).getBytes());
        // model.getConnectionInfo().setFlag(connectionFlag);
        TreeMap<String, Object> params = new TreeMap<>();
        if (!CollectionUtils.isEmpty(model.getParamters())) {
            params.putAll(model.getParamters());
        }
        String paramsMd5 = DigestUtils.md5DigestAsHex(JsonUtils.toJsonString(params).getBytes());
        String sqlMd5 = DigestUtils.md5DigestAsHex(model.getTemplateSql().getBytes());
        return DigestUtils.md5DigestAsHex(String.format("%s:%s:%s", connectionFlag, sqlMd5, paramsMd5).getBytes());
    }

    private ConnectionInfoVO cover2ConnectionVo(ConnectionInfoModel model) {
        //如果只有flag：本地查询
        if (StringUtils.hasText(model.getFlag())) {
            ConnectionInfoDBVO byFlag = connectionInfoDataManager.findByFlag(model.getFlag());
            if (null != byFlag) {
                ConnectionInfoVO build = ConnectionInfoVO.builder().build();
                BeanUtils.copyProperties(byFlag, build);
                return build;
            } else {
                throw new ValidationException("lost connection info ！");
            }
        }
        //如果全都有，转换，并本地保存
        ConnectionInfoVO build = ConnectionInfoVO.builder().build();
        BeanUtils.copyProperties(model, build);
        //计算flag，并保存
        TraceFuture.run(() -> {
            String flag = DigestUtils.md5DigestAsHex(JsonUtils.toJsonString(build).getBytes());
            if (null == connectionInfoDataManager.findByFlag(flag)) {
                ConnectionInfoDBVO connectionInfoDBVO = ConnectionInfoDBVO.builder().build();
                BeanUtils.copyProperties(build, connectionInfoDBVO);
                connectionInfoDBVO.setFlag(flag);
                connectionInfoDBVO.setStatus(DataStatus.ENABLE);
                connectionInfoDBVO.setCreateTime(new Date());
                connectionInfoDBVO.setUpdateTime(new Date());
                connectionInfoDataManager.saveConnectionInfo(connectionInfoDBVO);
            }
        });
        return build;
    }

}
