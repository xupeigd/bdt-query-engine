package com.quicksand.bigdata.query.rests;

import com.quicksand.bigdata.query.models.ConnectionInfoModel;
import com.quicksand.bigdata.query.models.QueryReqModel;
import com.quicksand.bigdata.query.models.QueryRespModel;
import com.quicksand.bigdata.query.services.QueryTaskService;
import com.quicksand.bigdata.query.utils.AuthUtil;
import com.quicksand.bigdata.query.utils.TraceFuture;
import com.quicksand.bigdata.query.vos.QueryTaskVO;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * QueryRestController
 *
 * @author xupei
 * @date 2022/8/10
 */
@SuppressWarnings("AlibabaServiceOrDaoClassShouldEndWithImpl")
@Slf4j
@Validated
@RestController
@CrossOrigin
@Api("查询Apis")
public class QueryRestController
        implements QueryRestService {

    @Resource
    QueryTaskService queryTaskService;

    @ApiOperation("创建查询请求")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Operation Success ！"),
    })
    @PreAuthorize("isAuthenticated()")
    @Override
    public Response<QueryRespModel> query(@RequestBody @Validated QueryReqModel model) {
        TraceFuture.run(() -> log.info("query request ! model:{}", JsonUtils.toJsonString(model)));
        return Response.ok(cover2Model(queryTaskService.query(model)));
    }

    @ApiOperation("检索查询作业")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Operation Success ！"),
    })
    @PreAuthorize("isAuthenticated()")
    @Override
    public Response<QueryRespModel> getResp(@PathVariable("id") @Length(min = 4, max = 32, message = "不存在的作业！") String id) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        TraceFuture.run(() -> log.info("getResp request ! user:[id:{},name:{}],taskId:{}", null == userDetail ? 0 : userDetail.getId(),
                null == userDetail ? "" : userDetail.getName(), id));
        QueryTaskVO queryTaskVO = queryTaskService.fetchTask(id);
        if (null == queryTaskVO) {
            return Response.notfound();
        }
        return Response.ok(cover2Model(queryTaskVO));
    }

    QueryRespModel cover2Model(QueryTaskVO taskVo) {
        QueryRespModel respModel = JsonUtils.transfrom(taskVo, QueryRespModel.class);
        //rebuild req
        QueryReqModel req = new QueryReqModel();
        req.setMode(taskVo.getReqControlInfo().getMode());
        req.setAsyncMills(taskVo.getReqControlInfo().getAsyncMills());
        req.setSyncMills(taskVo.getReqControlInfo().getSyncMills());
        req.setTemplateSql(taskVo.getQueryReq().getTemplateSql());
        req.setParamters(taskVo.getQueryReq().getParamters());
        req.setResultMode(taskVo.getReqControlInfo().getResultMode());
        respModel.setReq(req);
        //rebuild connection
        ConnectionInfoModel connectionInfo = new ConnectionInfoModel();
        BeanUtils.copyProperties(taskVo.getConnectionInfo(), connectionInfo);
        //处理掉密码
        connectionInfo.setPassword("******");
        req.setConnectionInfo(connectionInfo);
        return respModel;
    }

}
