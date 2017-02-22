package com.xzy.cm.common.exception;

/**
 * Created by xiongzhanyuan on 2017/2/16.
 */
public enum ErrorCodeEnum {
    //通用
    SYSTEM_ERROR("100001","系统异常"),
    PARAMETERFORMATERROR("20001", "参数格式错误!"),
    PARAMETERMISSING("20001","参数值缺失！");

    private String resultCode;
    private String resultMsg;

    private ErrorCodeEnum() {
    }

    private ErrorCodeEnum(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }


    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public static String getValues(String resultCode){
        for(ErrorCodeEnum chronicEnum:ErrorCodeEnum.values()){
            if(chronicEnum.resultCode==resultCode){
                return chronicEnum.resultMsg;
            }
        }
        return null;
    }

    public static ErrorCodeEnum getEnumByCode(String resultCode){
        for(ErrorCodeEnum chronicEnum:ErrorCodeEnum.values()){
            if(chronicEnum.getResultCode().equals(resultCode)){
                return chronicEnum;
            }
        }
        return null;
    }

    public static ErrorCodeEnum getEnumByMsg(String resultMsg){
        for(ErrorCodeEnum chronicEnum:ErrorCodeEnum.values()){
            if(chronicEnum.getResultMsg().equals(resultMsg)){
                return chronicEnum;
            }
        }
        return null;
    }
    
}
