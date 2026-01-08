package com.example.cms_android.response.base;

import java.util.List;
import com.example.cms_android.response.data.EducationData;

public class EducationResponse {
    private int code;
    private String message;
    private EducationData data;

    public EducationResponse() {
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

    public EducationData getData() {
        return data;
    }

    public void setData(EducationData data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}