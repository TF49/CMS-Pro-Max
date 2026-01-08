package com.example.cms_android.response.base;

import java.util.List;
import com.example.cms_android.response.data.PropertyData;

public class PropertyResponse {
    private int code;
    private String message;
    private PropertyData data;

    public PropertyResponse() {
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

    public PropertyData getData() {
        return data;
    }

    public void setData(PropertyData data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}