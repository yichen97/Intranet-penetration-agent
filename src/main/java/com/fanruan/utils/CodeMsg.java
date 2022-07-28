package com.fanruan.utils;


public class CodeMsg {

    private int code;
    private String msg;

    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    //注意  %s ，格式化字符串
    public static CodeMsg SERVER_BIND_ERROR = new CodeMsg(500101, "服务端绑定异常:%s");
    public static CodeMsg CLIENT_ID_ERROR = new CodeMsg(500201, "客户端ID错误");
    public static CodeMsg PARAM_EXIST = new CodeMsg(500301, "参数已存在，将被更新");
    public static CodeMsg DRIVER_NOTFOUND = new CodeMsg(500401, "驱动不存在");

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}