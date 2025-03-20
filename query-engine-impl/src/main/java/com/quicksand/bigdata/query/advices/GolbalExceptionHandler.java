package com.quicksand.bigdata.query.advices;

import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.net.BindException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * GolbalExceptionHandler
 *
 * @author xupei
 * @date 2022/7/28
 */
@Slf4j
@ControllerAdvice
public class GolbalExceptionHandler {

    @Resource
    HttpServletResponse httpServletResponse;

    /**
     * 参数绑定异常全局处理器
     *
     * @param ex  异常
     * @param <T> 泛型
     * @return instance of ResponseModel
     */
    private <T> Response<T> handleValidationException(Exception ex) {
        Response<T> response = Response.response(HttpStatus.BAD_REQUEST);
        httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex;
            if (StringUtils.hasText(constraintViolationException.getMessage())) {
                response.setDebugMessage(constraintViolationException.getMessage());
            }
        } else if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) ex;
            BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            if (!CollectionUtils.isEmpty(fieldErrors)) {
                FieldError fieldError = fieldErrors.get(0);
                response.setDebugMessage(fieldError.getDefaultMessage());
            }
        }
        return response;
    }

    @ResponseBody
    @ExceptionHandler(value = {Exception.class})
    public <T> Response<T> handleException(Exception ex) {
        log.warn("some thing wrong !", ex);
        Response<T> response;
        if (ex instanceof AccessDeniedException) {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            response = Response.response(HttpStatus.FORBIDDEN, "未授权的访问！");
        } else if (ex instanceof BindException
                || ex instanceof MethodArgumentNotValidException
                || ex instanceof ValidationException) {
            response = handleValidationException(ex);
        } else if (ex instanceof SQLIntegrityConstraintViolationException) {
            httpServletResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            response = Response.response(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
            response.setDebugMessage(ex.getMessage());
        } else {
            httpServletResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            response = Response.response(HttpStatus.SERVICE_UNAVAILABLE, "错误的请求！");
        }
        response.setDebugMessage(StringUtils.hasText(ex.getMessage())
                ? ex.getMessage()
                : Try.of(() -> JsonUtils.toJsonString(ex)).getOrElse("ex toJson err! "));
        return response;
    }

}
