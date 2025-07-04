package com.tmsdemo.exception;



public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    // 可选：添加带原因参数的构造器
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}