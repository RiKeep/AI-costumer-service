package com.ri.artificial.exception;

/**
 * 请求参数异常类
 * @author Ri
 * @date 2026-10-01 16:06
 */
public class BadRequestException extends CommonException {

    public BadRequestException(String message, Integer code) {
        super(message, code);
    }

    public BadRequestException(String message, Throwable cause, Integer code) {
        super(message, cause, code);
    }

    public BadRequestException(Throwable cause, Integer code) {
        super(cause, code);
    }
}
