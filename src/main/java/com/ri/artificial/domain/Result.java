package com.ri.artificial.domain;

import lombok.Data;
import cn.hutool.http.HttpStatus;

/**
 * @author Ri
 * @date 2026/10/01 16:15
 */
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        return success(HttpStatus.HTTP_OK, "ok", null);
    }

    public static <T> Result<T> success(T data) {
        return success(HttpStatus.HTTP_OK, "ok", data);
    }

    public static <T> Result<T> success(String msg) {
        return success(HttpStatus.HTTP_OK, msg, null);
    }

    public static <T> Result<T> success(Integer code, String msg) {
        return success(code, msg, null);
    }

    public static <T> Result<T> success(String msg, T data) {
        return success(HttpStatus.HTTP_OK, msg, data);
    }

    public static <T> Result<T> success(Integer code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error() {
        return error(HttpStatus.HTTP_BAD_REQUEST, "error", null);
    }

    public static <T> Result<T> error(T data) {
        return error(HttpStatus.HTTP_BAD_REQUEST, "error", data);
    }

    public static <T> Result<T> error(String msg) {
        return error(HttpStatus.HTTP_BAD_REQUEST, msg, null);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return error(code, msg, null);
    }

    public static <T> Result<T> error(String msg, T data) {
        return error(HttpStatus.HTTP_BAD_REQUEST, msg, data);
    }

    public static <T> Result<T> error(Integer code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
