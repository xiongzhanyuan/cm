package com.xzy.cm.common.exception;


/**
 * Created by xiongzhanyuan on 2017/2/16.
 */
public class BussinessException extends RuntimeException {

    private static final long serialVersionUID = -6879298763723247455L;

    private ErrorCodeEnum errorCodeEnum;

    private String message;

    public BussinessException() {

    }

    public BussinessException(String message) {
        this.message = message;
    }

    public BussinessException(ErrorCodeEnum errorCodeEnum) {
        this.errorCodeEnum = errorCodeEnum;
        this.message = errorCodeEnum.getResultMsg();
    }

    public BussinessException(ErrorCodeEnum errorCodeEnum, Throwable cause){
        super(cause.getMessage(),cause);
        this.message = errorCodeEnum.getResultMsg();
        this.errorCodeEnum = errorCodeEnum;
    }

    public ErrorCodeEnum getErrorCodeEnum() {
        return errorCodeEnum;
    }

    public void setErrorCodeEnum(ErrorCodeEnum errorCodeEnum) {
        this.errorCodeEnum = errorCodeEnum;
    }

    @Override
    public String getMessage() {
        return message;
    }



}
