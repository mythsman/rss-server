package com.mythsman.server.advice;

import com.mythsman.server.domain.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author tusenpo
 * @date 6/5/21
 */
@RestControllerAdvice
public class CommonAdvice implements ResponseBodyAdvice<Object> {
    private static final Logger logger = LoggerFactory.getLogger(CommonAdvice.class);

    @ExceptionHandler(Exception.class)
    public BaseResponse handleException(Exception e) {
        logger.error("error occurs,", e);
        BaseResponse response = new BaseResponse();
        response.setSuccess(false);
        response.setErrorMsg(e.getMessage());
        return response;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (!(body instanceof BaseResponse)) {
            BaseResponse wrappedResult = new BaseResponse();
            wrappedResult.setSuccess(true);
            wrappedResult.setResult(body);
            return wrappedResult;
        }
        return body;
    }
}
