package com.ri.artificial.advice;

import cn.hutool.http.HttpStatus;
import com.ri.artificial.context.Result;
import com.ri.artificial.exception.CommonException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Ri
 * @date 2026-10-01 16:03
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(CommonException.class)
    public Object handleCommonException(CommonException e) {
        log.error("自定义异常 ---> {} ， 异常原因：{}", e.getClass().getName(), e.getMessage(), e);
        return processResponse(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e) {
        log.error("其他异常 uri : -->", e);
        return processResponse(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部异常");
    }

    private ResponseEntity<Result<?>> processResponse(Integer code, String msg) {
        int httpStatus = (code != null && code >= 100 && code < 600) ? code : 500;
        Result<?> result = Result.error(code, msg);
        return ResponseEntity.status(httpStatus).body(result);
    }
}
