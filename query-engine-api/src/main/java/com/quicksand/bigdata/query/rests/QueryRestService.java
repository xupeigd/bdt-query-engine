package com.quicksand.bigdata.query.rests;

import com.quicksand.bigdata.query.models.QueryReqModel;
import com.quicksand.bigdata.query.models.QueryRespModel;
import com.quicksand.bigdata.vars.http.model.Response;
import org.hibernate.validator.constraints.Length;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * QueryRestService
 *
 * @author xupei
 * @date 2022/8/8
 */
@FeignClient(
        name = "${vars.name.ms.QueryRestService:bdt-query-engine}",
        url = "${vars.url.ms.QueryRestService:}",
        contextId = "QueryRestService")
public interface QueryRestService {

    /**
     * 创建查询作业
     *
     * @param model 查询参数
     * @return instance of QueryRespModel
     */
    @PostMapping("/query/queries")
    Response<QueryRespModel> query(@RequestBody @Validated QueryReqModel model);

    /**
     * 查询作业状态
     *
     * @param id 作业标识id
     * @return instance of QueryRespModel
     */
    @GetMapping("/query/queries/{id}")
    Response<QueryRespModel> getResp(@PathVariable("id") @Length(min = 4, max = 32, message = "不存在的作业！") String id);

}
