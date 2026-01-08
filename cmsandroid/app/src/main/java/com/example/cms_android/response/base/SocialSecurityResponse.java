package com.example.cms_android.response.base;

import java.util.List;
import com.example.cms_android.response.data.SocialSecurityData;

public class SocialSecurityResponse {
    private int code;
    private String message;
    private SocialSecurityData data;

    public SocialSecurityResponse() {
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

    public SocialSecurityData getData() {
        return data;
    }

    public void setData(SocialSecurityData data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}