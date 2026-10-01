package com.ri.artificial.exception;

import lombok.Getter;

/**
 * @author Ri
 * @date 2026-10-01 16:04
 */
@Getter
public class CommonException extends RuntimeException{
    // 状态码
    private final Integer code;

    // 创建一个接收错误信息和错误码的构造方法
    public CommonException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    // 创建一个接收错误信、异常原因、错误码的构造方法
    public CommonException(String message, Throwable cause, Integer code) {
        super(message, cause);
        this.code = code;
    }

    // 创建一个接收异常原因、错误码的构造方法
    public CommonException(Throwable cause, Integer code) {
        super(cause);
        this.code = code;
    }
}
