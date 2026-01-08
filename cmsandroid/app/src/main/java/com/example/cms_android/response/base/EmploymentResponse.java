package com.example.cms_android.response.base;

import java.util.List;
import com.example.cms_android.response.data.EmploymentData;

public class EmploymentResponse {
    private int code;
    private String message;
    private EmploymentData data;

    public EmploymentResponse() {
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

    public EmploymentData getData() {
        return data;
    }

    public void setData(EmploymentData data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}