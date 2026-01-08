package com.example.cms_android.auth;

public class RegisterResponse {
    private int code;
    private String message;
    private Object data; // 用于兼容后端的响应结构

    public RegisterResponse() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        // 注册成功时后端返回code为200
        return code == 200;
    }
}