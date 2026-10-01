package com.ri.artificial.exception;

/**
 * 文件上传失败异常类
 * @author Ri
 * @date 2026-10-01 16:07
 */
public class UploadFileException extends CommonException {

    public UploadFileException(String message, Integer code) {
        super(message, code);
    }

    public UploadFileException(String message, Throwable cause, Integer code) {
        super(message, cause, code);
    }

    public UploadFileException(Throwable cause, Integer code) {
        super(cause, code);
    }
}
