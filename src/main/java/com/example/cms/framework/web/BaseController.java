package com.example.cms.framework.web;

import com.example.cms.common.Result;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {
    
    protected <T> Result<T> success(T data) {
        return Result.success(data);
    }
    
    protected <T> Result<T> error(String message) {
        return Result.error(message);
    }
}