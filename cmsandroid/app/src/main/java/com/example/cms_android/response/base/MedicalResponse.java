package com.example.cms_android.response.base;

import java.util.List;
import com.example.cms_android.response.data.MedicalData;

public class MedicalResponse {
    private int code;
    private String message;
    private MedicalData data;

    public MedicalResponse() {
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

    public MedicalData getData() {
        return data;
    }

    public void setData(MedicalData data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}