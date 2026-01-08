package com.example.cms.common;

/**
 * 密码工具类
 * 注意：此工具类已被废弃，现在使用明文密码验证
 * 不再使用加密功能，仅保留接口避免编译错误
 * @deprecated 使用明文密码验证
 */
@Deprecated
public class PasswordUtils {
    
    /**
     * 加密密码 - 已废弃
     * @param rawPassword 原始密码
     * @return 加密后的密码（实际不加密，直接返回原密码）
     * @deprecated 使用明文密码
     */
    @Deprecated
    public static String encrypt(String rawPassword) {
        // 简化版本：不加密，直接返回原密码
        return rawPassword;
    }
    
    /**
     * 验证密码 - 已废弃
     * @param rawPassword 原始密码
     * @param encryptedPassword 加密后的密码（实际是明文）
     * @return 是否匹配（直接字符串比较）
     * @deprecated 使用明文密码比较
     */
    @Deprecated
    public static boolean matches(String rawPassword, String encryptedPassword) {
        // 直接比较明文密码
        return rawPassword != null && encryptedPassword != null && rawPassword.equals(encryptedPassword);
    }
    
    /**
     * 生成随机密码（用于测试）- 已废弃
     * @return 随机密码（实际返回固定字符串）
     * @deprecated 不再使用
     */
    @Deprecated
    public static String generateRandomPassword() {
        // 简化版本：返回固定密码
        return "123456";
    }
}