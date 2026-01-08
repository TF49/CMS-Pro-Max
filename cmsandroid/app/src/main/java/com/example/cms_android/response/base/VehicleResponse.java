package com.example.cms_android.response.base;

import java.util.List;
import com.example.cms_android.response.data.VehicleData;

public class VehicleResponse {
    private int code;
    private String message;
    private VehicleData data;

    public VehicleResponse() {
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

    public VehicleData getData() {
        return data;
    }

    public void setData(VehicleData data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}