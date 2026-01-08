package com.example.cms.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 用于处理系统中的各种异常，返回统一的响应格式
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(PermissionDeniedException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handlePermissionDeniedException(PermissionDeniedException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.FORBIDDEN.value());
        response.put("message", e.getMessage());
        response.put("success", false);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * 处理Spring Security的AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.FORBIDDEN.value());
        response.put("message", "权限不足，无法访问该资源");
        response.put("success", false);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("message", "系统内部错误：" + e.getMessage());
        response.put("success", false);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
