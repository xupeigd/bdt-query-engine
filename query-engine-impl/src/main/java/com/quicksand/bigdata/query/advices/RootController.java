package com.quicksand.bigdata.query.advices;

import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.Eulogy;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RootController
 *
 * @author xupei
 * @date 2022/8/5
 */
@RestController
public class RootController
        implements ErrorController {

    @Resource
    HttpServletResponse httpResponse;
    @Resource
    HttpServletRequest httpRequest;

    @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> error() {
        if (httpResponse.getStatus() == HttpStatus.NOT_FOUND.value()) {
            Response<Void> response = Eulogy.eulogy();
            response.setCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
            return response;
        }
        return Response.response(String.valueOf(httpResponse.getStatus()), "some thing wrong ! ");
    }

}
