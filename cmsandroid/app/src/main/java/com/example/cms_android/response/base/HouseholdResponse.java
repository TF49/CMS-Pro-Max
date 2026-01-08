package com.example.cms_android.response.base;

import java.util.List;
import com.example.cms_android.response.data.HouseholdData;

public class HouseholdResponse {
    private int code;
    private String message;
    private HouseholdData data;

    public HouseholdResponse() {
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

    public HouseholdData getData() {
        return data;
    }

    public void setData(HouseholdData data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}