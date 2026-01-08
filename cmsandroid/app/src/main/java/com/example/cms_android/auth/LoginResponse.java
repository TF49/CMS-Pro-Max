package com.example.cms_android.auth;

public class LoginResponse {
    private int code;
    private String message;
    private LoginResult data;

    public LoginResponse() {
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

    public LoginResult getData() {
        return data;
    }

    public void setData(LoginResult data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}