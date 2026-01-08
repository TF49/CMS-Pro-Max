package com.example.cms.common.exception;

/**
 * 权限不足异常
 * 用于处理用户没有足够权限访问资源的情况
 */
public class PermissionDeniedException extends RuntimeException {
    
    public PermissionDeniedException() {
        super();
    }
    
    public PermissionDeniedException(String message) {
        super(message);
    }
    
    public PermissionDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public PermissionDeniedException(Throwable cause) {
        super(cause);
    }
    
    protected PermissionDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
