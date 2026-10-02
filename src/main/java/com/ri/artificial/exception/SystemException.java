package com.ri.artificial.exception;

import cn.hutool.http.HttpStatus;

/**
 * @author Ri
 * @date 2026-10-02 17:27
 * 系统异常：用于区分「业务规则拒绝(400)」与「系统/依赖故障(500)」
 */
public class SystemException extends CommonException{
    public SystemException(String message) {
        super(message, HttpStatus.HTTP_INTERNAL_ERROR);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause, HttpStatus.HTTP_INTERNAL_ERROR);
    }

    public SystemException(Throwable cause) {
        super(cause, HttpStatus.HTTP_INTERNAL_ERROR);
    }
}
