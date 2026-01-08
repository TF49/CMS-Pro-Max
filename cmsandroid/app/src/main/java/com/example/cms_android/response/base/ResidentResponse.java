package com.example.cms_android.response.base;


import java.util.List;
import com.example.cms_android.response.data.ResidentData;

public class ResidentResponse {
    private int code;
    private String message;
    private ResidentData data;

    public ResidentResponse() {
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

    public ResidentData getData() {
        return data;
    }

    public void setData(ResidentData data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}