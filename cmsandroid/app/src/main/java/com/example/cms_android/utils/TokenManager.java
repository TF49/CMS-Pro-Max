package com.example.cms_android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.cms_android.auth.User;
import com.google.gson.Gson;

/**
 * Token管理器
 * 负责安全存储和管理用户的认证令牌及用户信息
 *
 * 该类使用Android的SharedPreferences进行持久化存储
 * 存储内容包括：
 * 1. auth_token - JWT认证令牌，用于API请求认证
 * 2. username - 用户名，方便快速获取
 * 3. user_info - 完整的用户信息（序列化为JSON）
 *
 * 使用场景：
 * - 用户登录成功后保存Token
 * - 发送API请求时获取Token
 * - 获取当前用户信息
 * - 检查用户登录状态
 * - 用户退出登录时清理数据
 *
 * 安全说明：
 * - SharedPreferences存储的数据在应用卸载前会保留
 * - 敏感信息（Token）应考虑加密存储
 * - 退出登录时应调用clearToken()清理所有数据
 */
public class TokenManager {

    /** SharedPreferences文件名 */
    private static final String PREF_NAME = "cms_prefs";

    /** Token存储键名 */
    private static final String KEY_TOKEN = "auth_token";

    /** 用户名存储键名 */
    private static final String KEY_USERNAME = "username";

    /** 用户信息存储键名 */
    private static final String KEY_USER_INFO = "user_info";

    /** SharedPreferences实例，用于数据存储 */
    private SharedPreferences sharedPreferences;

    /** SharedPreferences编辑器，用于写入数据 */
    private SharedPreferences.Editor editor;

    /** JSON解析器，用于User对象与JSON字符串的转换 */
    private Gson gson;

    /**
     * 构造函数
     * 初始化SharedPreferences和编辑器
     *
     * @param context 上下文对象，用于获取SharedPreferences
     */
    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    /**
     * 保存认证令牌
     * 将JWT Token持久化存储，用于后续API请求认证
     *
     * 存储方式：使用apply()异步写入，不阻塞主线程
     *
     * @param token JWT认证令牌字符串
     */
    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    /**
     * 保存用户名
     * 存储用户名称，方便快速获取显示
     *
     * 建议与saveUserInfo()同时调用，确保数据一致性
     *
     * @param username 用户名字符串
     */
    public void saveUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    /**
     * 保存用户信息
     * 将User对象序列化为JSON字符串后存储
     *
     * 存储内容：
     * - 用户ID
     * - 用户名
     * - 角色信息
     * - 其他用户相关数据
     *
     * @param user 用户信息对象
     */
    public void saveUserInfo(User user) {
        // 将User对象转换为JSON字符串
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER_INFO, userJson);
        editor.apply();
    }

    /**
     * 获取认证令牌
     * 用于API请求时的身份认证
     *
     * @return JWT Token字符串，如果未存储则返回null
     */
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    /**
     * 获取用户名
     *
     * 获取策略（按优先级顺序）：
     * 1. 优先返回直接存储的用户名
     * 2. 如果没有，尝试从User对象中获取用户名
     * 3. 如果都没有，返回默认用户名"管理员"
     *
     * @return 用户名字符串
     */
    public String getUsername() {
        // 优先从直接存储的用户名获取
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        if (username != null) {
            return username;
        }

        // 如果没有直接存储的用户名，尝试从完整用户信息中获取
        User user = getUserInfo();
        if (user != null && user.getUsername() != null) {
            return user.getUsername();
        }

        // 都没有则返回默认值
        return "管理员";
    }

    /**
     * 获取用户信息
     * 将存储的JSON字符串反序列化为User对象
     *
     * @return User对象，如果未存储则返回null
     */
    public User getUserInfo() {
        String userJson = sharedPreferences.getString(KEY_USER_INFO, null);
        return userJson != null ? gson.fromJson(userJson, User.class) : null;
    }

    /**
     * 清除所有认证信息
     * 用于用户退出登录时清理数据
     *
     * 清理内容：
     * - 认证令牌
     * - 用户名
     * - 用户信息
     *
     * 清理后调用isLoggedIn()将返回false
     */
    public void clearToken() {
        // 逐个移除存储的键值对
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_USER_INFO);
        editor.apply();
    }

    /**
     * 检查用户是否已登录
     *
     * 判断依据：Token是否存在且不为空
     * Token通常在登录成功后保存，退出登录后清除
     *
     * @return true表示已登录，false表示未登录
     */
    public boolean isLoggedIn() {
        return getToken() != null;
    }
}