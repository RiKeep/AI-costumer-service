package com.ri.artificial.advice;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.hutool.http.HttpStatus;
import com.ri.artificial.context.Result;
import com.ri.artificial.exception.CommonException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Ri
 * @date 2026-10-01 16:03
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {
    /**
     * Validation 异常消息处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getAllErrors()
                .stream().map(ObjectError::getDefaultMessage).toList().get(0);
        log.error("请求参数校验异常 --> {}", msg);
        return processResponse(HttpStatus.HTTP_BAD_REQUEST, msg);
    }

    @ExceptionHandler(NotLoginException.class)
    public Object handleSaTokenException(NotLoginException e) {
        log.error("用户未登录 ---> {} ， 异常原因：{}", e.getClass().getName(), e.getMessage(), e);
        return processResponse(HttpStatus.HTTP_UNAUTHORIZED, "用户未登录");
    }

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
