package com.example.cms_android.network;

/**
 * API服务类 - 负责与后端服务器进行HTTP通信
 * 
 * 功能概述：
 * 1. 提供用户认证相关接口（登录、注册、密码重置）
 * 2. 提供居民信息管理接口（增删改查）
 * 3. 提供各类信息管理接口（户籍、教育、就业、医疗、社保、车辆、房产）
 * 
 * 技术特点：
 * - 使用OkHttpClient进行网络请求
 * - 使用Gson进行JSON序列化/反序列化
 * - 支持JWT Token认证机制
 * - 支持分页查询
 * - 自定义日期类型适配器
 * 
 * @author CMS Android开发团队
 * @version 1.0
 */

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.cms_android.auth.LoginRequest;
import com.example.cms_android.auth.LoginResponse;
import com.example.cms_android.auth.RegisterResponse;
import com.example.cms_android.model.Resident;
import com.example.cms_android.response.base.ResidentResponse;
import com.example.cms_android.response.base.HouseholdResponse;
import com.example.cms_android.response.base.EducationResponse;
import com.example.cms_android.response.base.EmploymentResponse;
import com.example.cms_android.response.base.MedicalResponse;
import com.example.cms_android.response.base.SocialSecurityResponse;
import com.example.cms_android.response.base.VehicleResponse;
import com.example.cms_android.response.base.PropertyResponse;
import com.example.cms_android.model.Household;
import com.example.cms_android.model.Education;
import com.example.cms_android.model.Employment;
import com.example.cms_android.model.Medical;
import com.example.cms_android.model.SocialSecurity;
import com.example.cms_android.model.Vehicle;
import com.example.cms_android.model.Property;

/**
 * Google JSON处理库 - 用于Java对象与JSON字符串之间的转换
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Java并发工具类 - 用于设置网络请求超时时间
 */
import java.util.concurrent.TimeUnit;

/**
 * OkHttp3网络库 - 发送HTTP请求和处理响应
 */
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Gson自定义序列化/反序列化适配器接口
 */
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Java反射和时间处理相关类
 */
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * API服务类 - 统一管理所有与后端服务器的HTTP通信
 * 
 * 该类是Android客户端与后端API交互的核心枢纽，
 * 封装了所有网络请求的细节，提供简洁的接口供上层调用。
 * 
 * 主要职责：
 * 1. 用户认证管理（登录、注册、密码重置）
 * 2. 居民信息CRUD操作
 * 3. 各类信息管理（户籍、教育、就业、医疗、社保、车辆、房产）
 * 4. JWT Token认证维护
 * 
 * 使用方式：
 * - 创建ApiService实例
 * - 调用相应的方法进行API请求
 * - 方法会返回对应的响应对象或抛出异常
 * 
 * @since 1.0
 */

public class ApiService {
    /**
     * 服务器基础URL地址
     * 10.0.2.2 是Android模拟器访问宿主机localhost的特殊地址
     * 端口9090为后端服务端口
     */
    private static final String BASE_URL = "http://10.0.2.2:9090/";
    
    /**
     * API接口基础路径
     * 所有的API请求都会在此路径下进行
     */
    private static final String API_BASE_URL = BASE_URL + "api/";
    
    /**
     * JSON媒体类型常量
     * 用于指定HTTP请求Content-Type为application/json; charset=utf-8
     */
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * OkHttpClient实例
     * 用于发送HTTP请求和接收响应
     * 在构造函数中配置了30秒超时时间
     */
    private OkHttpClient client;
    
    /**
     * Gson实例
     * 用于Java对象与JSON字符串之间的序列化和反序列化
     * 配置了自定义日期类型适配器
     */
    private Gson gson;
    
    /**
     * JWT认证令牌
     * 用于存储用户登录后获取的认证令牌
     * 采用静态变量以便在整个应用范围内共享
     */
    private static String authToken;

    /**
     * 构造函数 - 初始化API服务实例
     * 
     * 在构造函数中完成以下初始化工作：
     * 1. 创建OkHttpClient网络客户端，配置30秒超时时间
     * 2. 创建Gson实例，配置自定义日期类型适配器
     * 
     * 超时时间说明：
     * - connectTimeout：建立TCP连接的最长时间，超过此时间未连接成功则超时
     * - readTimeout：读取服务器响应的最长时间，超过此时间未收到响应则超时
     * - writeTimeout：向服务器发送数据的最长时间，超过此时间未发送完成则超时
     * 
     * 日期适配器说明：
     * - DateTypeAdapter：处理java.util.Date类型，支持多种日期格式
     * - LocalDateTypeAdapter：处理java.time.LocalDate类型，格式为yyyy-MM-dd
     * - LocalDateTimeTypeAdapter：处理java.time.LocalDateTime类型，格式为yyyy-MM-dd HH:mm:ss
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ApiService() {
        // 创建OkHttpClient实例，设置超时时间
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时：30秒
                .readTimeout(30, TimeUnit.SECONDS)     // 读取超时：30秒
                .writeTimeout(30, TimeUnit.SECONDS)    // 写入超时：30秒
                .build();

        // 创建Gson实例，用于JSON序列化和反序列化
        gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    /**
     * 设置用户认证令牌
     * 
     * 该方法用于在用户成功登录后，将服务器返回的JWT认证令牌存储起来，
     * 后续的所有需要认证的API请求都会自动携带此令牌。
     * 
     * 使用场景：
     * - 用户登录成功后调用此方法保存令牌
     * - 用户退出登录时调用此方法清除令牌（传入null或空字符串）
     * 
     * 认证机制说明：
     * - 采用Bearer Token认证方式
     * - 令牌格式：Authorization: Bearer <token>
     * - 令牌通常包含用户ID、角色、过期时间等信息
     * 
     * @param token JWT认证令牌字符串，登录成功后从服务器获取
     */
    public static void setAuthToken(String token) {
        authToken = token;
    }

    /**
     * 获取当前存储的认证令牌
     * 
     * 该方法用于获取之前存储的JWT认证令牌。
     * 主要在发送API请求时，由内部逻辑自动添加到请求头中，
     * 上层调用者一般不需要直接调用此方法。
     * 
     * @return String 当前存储的JWT认证令牌，如果没有则返回null
     */
    public static String getAuthToken() {
        return authToken;
    }

    /**
     * 用户登录接口
     * 
     * 该方法用于验证用户身份并获取认证令牌。
     * 登录成功后会将令牌保存供后续请求使用。
     * 
     * API详情：
     * - 请求方式：POST
     * - 请求路径：/api/auth/login
     * - 请求参数：username（用户名）、password（密码）
     * - 请求体格式：JSON，如：{"username": "test", "password": "123456"}
     * 
     * 响应说明：
     * - 成功：返回包含JWT令牌的LoginResponse对象
     * - 失败：抛出异常，包含HTTP状态码
     * 
     * 错误码说明：
     * - 400：请求参数错误
     * - 401：用户名或密码错误
     * - 500：服务器内部错误
     * 
     * @param username 用户名，必填
     * @param password 用户密码，必填
     * @return LoginResponse 包含登录结果、JWT令牌、用户信息等
     * @throws Exception 登录失败时抛出异常，包含HTTP状态码和错误信息
     */
    public LoginResponse login(String username, String password) throws Exception {
        // 创建登录请求对象
        LoginRequest loginRequest = new LoginRequest(username, password);
        // 将请求对象转换为JSON字符串
        String json = gson.toJson(loginRequest);
        // 创建HTTP请求体，指定Content-Type为application/json; charset=utf-8
        RequestBody body = RequestBody.create(json, JSON);

        // 构建HTTP请求
        Request request = new Request.Builder()
                .url(API_BASE_URL + "auth/login")  // 登录接口地址
                .post(body)                         // POST方法提交请求体
                .build();

        // 发送请求并处理响应（使用try-with-resources自动关闭响应）
        try (Response response = client.newCall(request).execute()) {
            // 检查HTTP响应状态码是否为成功（200-299）
            if (!response.isSuccessful()) {
                // 登录失败，抛出异常
                throw new Exception("登录失败: " + response.code());
            }

            // 读取响应体内容
            String responseData = response.body().string();
            // 将JSON响应转换为LoginResponse对象
            return gson.fromJson(responseData, LoginResponse.class);
        }
    }

    /**
     * 用户注册接口
     * 
     * 该方法用于新用户注册，只能注册普通用户角色（USER）。
     * 管理员账户需要通过后台管理系统创建，以保证系统安全性。
     * 
     * API详情：
     * - 请求方式：POST
     * - 请求路径：/api/auth/register
     * - 请求参数：username（用户名）、password（密码）、idCard（身份证号）
     * - 请求体格式：JSON
     * 
     * 注册限制：
     * - 用户名：唯一，不能重复
     * - 密码：建议6位以上，包含字母和数字
     * - 身份证号：用于身份验证，必须有效
     * - 角色：固定为"USER"，普通用户权限
     * 
     * 响应说明：
     * - 成功：返回RegisterResponse，包含注册结果
     * - 失败：抛出异常
     * 
     * @param username 用户名，用于登录系统，必填
     * @param password 用户密码，必填
     * @param idCard 身份证号码，用于身份验证，必填
     * @return RegisterResponse 包含注册结果信息
     * @throws Exception 注册失败时抛出异常，包含HTTP状态码和错误信息
     */
    public RegisterResponse register(String username, String password, String idCard) throws Exception {
        // 构建注册请求体
        // 注意：角色被硬编码为"USER"，只允许普通用户注册
        // 管理员账户需要通过网页端管理界面创建
        RegisterRequest registerRequest = new RegisterRequest(username, password, password, idCard, "USER");
        // 将请求对象转换为JSON字符串
        String json = gson.toJson(registerRequest);
        // 创建HTTP请求体
        RequestBody body = RequestBody.create(json, JSON);

        // 构建HTTP请求
        Request request = new Request.Builder()
                .url(API_BASE_URL + "auth/register")  // 注册接口地址
                .post(body)                           // POST方法提交
                .build();

        // 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("注册失败: " + response.code());
            }

            // 读取响应体并转换为RegisterResponse对象
            String responseData = response.body().string();
            return gson.fromJson(responseData, RegisterResponse.class);
        }
    }

    /**
     * 重置用户密码接口
     * 
     * 该方法用于用户忘记密码时，通过用户名和身份证号验证身份后重置密码。
     * 这是一种自助式的密码找回方式。
     * 
     * API详情：
     * - 请求方式：POST
     * - 请求路径：/api/auth/reset-password
     * - 请求体格式：JSON
     * 
     * 安全验证：
     * - 需要提供正确的用户名
     * - 需要提供与账号绑定的身份证号
     * - 验证通过后才能重置密码
     * 
     * 响应说明：
     * - 成功：返回类似登录的响应，可能需要重新登录
     * - 失败：抛出异常
     * 
     * @param username 用户名，必填
     * @param idCard 身份证号码，必填
     * @param newPassword 新密码，必填
     * @return LoginResponse 包含重置结果
     * @throws Exception 重置失败时抛出异常，包含HTTP状态码和错误信息
     */
    public LoginResponse resetPassword(String username, String idCard, String newPassword) throws Exception {
        // 构建重置密码请求体
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(username, idCard, newPassword, newPassword,
                "");
        // 转换为JSON
        String json = gson.toJson(resetPasswordRequest);
        RequestBody body = RequestBody.create(json, JSON);

        // 构建请求
        Request request = new Request.Builder()
                .url(API_BASE_URL + "auth/reset-password")  // 重置密码接口地址
                .post(body)
                .build();

        // 发送请求
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("重置密码失败: " + response.code());
            }

            String responseData = response.body().string();
            return gson.fromJson(responseData, LoginResponse.class);
        }
    }

    /**
     * 获取居民列表（分页查询）
     * 
     * 该方法用于从服务器获取居民信息列表，支持分页功能。
     * 调用此接口需要用户登录认证。
     * 
     * API详情：
     * - 请求方式：GET
     * - 请求路径：/resident/list
     * - 请求参数：page（页码，从0开始）、size（每页数量）
     * - 认证方式：Bearer Token（JWT）
     * 
     * 分页说明：
     * - page：从0开始的页码索引
     * - size：每页返回的记录数量
     * - 例如：page=0&size=10 表示获取第1页，每页10条记录
     * 
     * 响应说明：
     * - 成功：返回ResidentResponse，包含居民数据列表和分页信息
     * - 失败：抛出异常
     * 
     * @param page 页码索引，从0开始计数
     * @param size 每页显示的记录数量
     * @return ResidentResponse 包含居民数据列表和分页信息的响应对象
     * @throws Exception 请求失败时抛出异常，包含HTTP状态码和错误信息
     */
    public ResidentResponse getResidents(int page, int size) throws Exception {
        // 构建请求构建器，拼接分页参数
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "resident/list?page=" + page + "&size=" + size);

        // 检查是否存在认证令牌，如果存在则添加到请求头
        if (authToken != null && !authToken.isEmpty()) {
            // Authorization请求头格式：Bearer <token>
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        // 构建完整的请求对象
        Request request = builder.build();

        // 发送HTTP请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            // 检查HTTP响应状态码是否为2xx成功状态
            if (!response.isSuccessful()) {
                // 请求失败，抛出异常带状态码
                throw new Exception("获取居民列表失败: " + response.code());
            }

            // 读取响应体内容
            String responseData = response.body().string();
            // 将JSON数据反序列化为ResidentResponse对象
            return gson.fromJson(responseData, ResidentResponse.class);
        }
    }

    /**
     * 注册请求模型
     */
    private static class RegisterRequest {
        private String username;
        private String password;
        private String confirmPassword;
        private String idCard;
        private String role;

        public RegisterRequest(String username, String password, String confirmPassword, String idCard, String role) {
            this.username = username;
            this.password = password;
            this.confirmPassword = confirmPassword;
            this.idCard = idCard;
            this.role = role;
        }

        // Getters and setters (required for Gson serialization)
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    /**
     * 重置密码请求模型
     */
    private static class ResetPasswordRequest {
        private String username;
        private String idCard;
        private String token;
        private String newPassword;
        private String confirmPassword;

        public ResetPasswordRequest(String username, String idCard, String newPassword, String confirmPassword,
                String token) {
            this.username = username;
            this.idCard = idCard;
            this.newPassword = newPassword;
            this.confirmPassword = confirmPassword;
            this.token = token;
        }

        // Getters and setters (required for Gson serialization)
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }

    /**
     * 添加居民
     */
    public boolean addResident(Resident resident) throws Exception {
        String json = gson.toJson(resident);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "resident/add")
                .post(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 更新居民信息
     */
    public boolean updateResident(Resident resident) throws Exception {
        String json = gson.toJson(resident);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "resident/update")
                .put(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 删除居民
     */
    public boolean deleteResident(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "resident/delete/" + id)
                .delete();

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 根据ID获取居民详情
     */
    public Resident getResidentById(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "resident/" + id);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取居民详情失败: " + response.code());
            }

            String responseData = response.body().string();
            // 添加调试日志，打印服务器返回的原始JSON数据

            // 先解析响应为Result对象
            com.google.gson.JsonObject jsonObject = gson.fromJson(responseData, com.google.gson.JsonObject.class);
            // 获取data字段
            com.google.gson.JsonElement dataElement = jsonObject.get("data");
            // 解析data字段为Resident对象
            return gson.fromJson(dataElement, Resident.class);
        }
    }

    /**
     * 获取户籍信息列表
     */
    public HouseholdResponse getHouseholdList(int page, int size) throws Exception {
        return getHouseholds(page, size);
    }

    /**
     * 获取户籍信息列表
     */
    public HouseholdResponse getHouseholds(int page, int size) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "household/list?page=" + page + "&size=" + size);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取户籍列表失败: " + response.code());
            }

            String responseData = response.body().string();
            return gson.fromJson(responseData, HouseholdResponse.class);
        }
    }

    /**
     * 添加户籍信息
     */
    public boolean addHousehold(Household household) throws Exception {
        String json = gson.toJson(household);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "household/add")
                .post(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 获取教育信息列表
     */
    public EducationResponse getEducationList(int page, int size) throws Exception {
        return getEducations(page, size);
    }

    /**
     * 获取教育信息列表
     */
    public EducationResponse getEducations(int page, int size) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "education/list?page=" + page + "&size=" + size);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取教育列表失败: " + response.code());
            }

            String responseData = response.body().string();
            return gson.fromJson(responseData, EducationResponse.class);
        }
    }

    /**
     * 获取就业信息列表
     */
    public EmploymentResponse getEmploymentList(int page, int size) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "employment/list?page=" + page + "&size=" + size);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取就业列表失败: " + response.code());
            }

            String responseData = response.body().string();
            // 添加调试日志，打印服务器返回的原始JSON数据

            return gson.fromJson(responseData, EmploymentResponse.class);
        }
    }

    /**
     * 获取就业信息列表
     */
    public EmploymentResponse getEmployments(int page, int size) throws Exception {
        return getEmploymentList(page, size);
    }

    /**
     * 获取医疗信息列表
     */
    public MedicalResponse getMedicalList(int page, int size) throws Exception {
        return getMedicals(page, size);
    }

    /**
     * 获取医疗信息列表
     */
    public MedicalResponse getMedicals(int page, int size) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "medical/list?page=" + page + "&size=" + size);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取医疗列表失败: " + response.code());
            }

            String responseData = response.body().string();
            // 添加调试日志，打印完整的医疗列表响应

            return gson.fromJson(responseData, MedicalResponse.class);
        }
    }

    /**
     * 获取社保信息列表
     */
    public SocialSecurityResponse getSocialSecurityList(int page, int size) throws Exception {
        return getSocialSecurities(page, size);
    }

    /**
     * 获取社保信息列表
     */
    public SocialSecurityResponse getSocialSecurities(int page, int size) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "social-security/list?page=" + page + "&size=" + size);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取社保列表失败: " + response.code());
            }

            String responseData = response.body().string();
            return gson.fromJson(responseData, SocialSecurityResponse.class);
        }
    }

    /**
     * 获取车辆信息列表
     */
    public VehicleResponse getVehicleList(int page, int size) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "vehicle/list?page=" + page + "&size=" + size);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取车辆列表失败: " + response.code());
            }

            String responseData = response.body().string();
            return gson.fromJson(responseData, VehicleResponse.class);
        }
    }

    /**
     * 获取车辆信息列表
     */
    public VehicleResponse getVehicles(int page, int size) throws Exception {
        return getVehicleList(page, size);
    }

    /**
     * 获取房产信息列表
     */
    public PropertyResponse getPropertyList(int page, int size) throws Exception {
        return getProperties(page, size);
    }

    /**
     * 获取房产信息列表
     */
    public PropertyResponse getProperties(int page, int size) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "property/list?page=" + page + "&size=" + size);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取房产列表失败: " + response.code());
            }

            String responseData = response.body().string();
            return gson.fromJson(responseData, PropertyResponse.class);
        }
    }

    /**
     * 根据ID获取社保详情
     */
    public SocialSecurity getSocialSecurityById(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "social-security/" + id);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取社保详情失败: " + response.code());
            }

            String responseData = response.body().string();
            // 先解析响应为Result对象
            com.google.gson.JsonObject jsonObject = gson.fromJson(responseData, com.google.gson.JsonObject.class);
            // 获取data字段
            com.google.gson.JsonElement dataElement = jsonObject.get("data");
            // 解析data字段为SocialSecurity对象
            return gson.fromJson(dataElement, SocialSecurity.class);
        }
    }

    /**
     * 更新社保信息
     */
    public boolean updateSocialSecurity(SocialSecurity socialSecurity) throws Exception {
        String json = gson.toJson(socialSecurity);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "social-security/update")
                .put(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String responseBody = response.body().string();
                // 如果响应不成功，抛出带有详细信息的异常
                if (!response.isSuccessful()) {
                    throw new Exception("HTTP " + response.code() + ": " + response.message() + " - " + responseBody);
                }
                return response.isSuccessful();
            } else {
                return response.isSuccessful();
            }
        }
    }

    /**
     * 删除社保信息
     */
    public boolean deleteSocialSecurity(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "social-security/delete/" + id)
                .delete();

        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 添加社保信息
     */
    public boolean addSocialSecurity(SocialSecurity socialSecurity) throws Exception {
        String json = gson.toJson(socialSecurity);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "social-security/add")
                .post(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 根据ID获取车辆详情
     */
    public Vehicle getVehicleById(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "vehicle/" + id);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取车辆详情失败: " + response.code());
            }

            String responseData = response.body().string();
            // 先解析响应为Result对象
            com.google.gson.JsonObject jsonObject = gson.fromJson(responseData, com.google.gson.JsonObject.class);
            // 获取data字段
            com.google.gson.JsonElement dataElement = jsonObject.get("data");
            // 解析data字段为Vehicle对象
            return gson.fromJson(dataElement, Vehicle.class);
        }
    }

    /**
     * 更新车辆信息
     */
    public boolean updateVehicle(Vehicle vehicle) throws Exception {
        String json = gson.toJson(vehicle);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "vehicle/update")
                .put(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 添加车辆信息
     */
    public boolean addVehicle(Vehicle vehicle) throws Exception {
        String json = gson.toJson(vehicle);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "vehicle/add")
                .post(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String responseBody = response.body().string();
                // 如果响应不成功，抛出带有详细信息的异常
                if (!response.isSuccessful()) {
                    throw new Exception("HTTP " + response.code() + ": " + response.message() + " - " + responseBody);
                }
                return response.isSuccessful();
            } else {
                return response.isSuccessful();
            }
        }
    }

    /**
     * 删除车辆信息
     */
    public boolean deleteVehicle(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "vehicle/delete/" + id)
                .delete();

        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 根据ID获取房产详情
     */
    public Property getPropertyById(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "property/" + id);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取房产详情失败: " + response.code());
            }

            String responseData = response.body().string();
            // 先解析响应为Result对象
            com.google.gson.JsonObject jsonObject = gson.fromJson(responseData, com.google.gson.JsonObject.class);
            // 获取data字段
            com.google.gson.JsonElement dataElement = jsonObject.get("data");
            // 解析data字段为Property对象
            return gson.fromJson(dataElement, Property.class);
        }
    }

    /**
     * 添加房产信息
     */
    public boolean addProperty(Property property) throws Exception {
        String json = gson.toJson(property);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "property/add")
                .post(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 删除房产信息
     */
    public boolean deleteProperty(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "property/delete/" + id)
                .delete();

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 更新房产信息
     */
    public boolean updateProperty(Property property) throws Exception {
        String json = gson.toJson(property);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "property/update")
                .put(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 根据ID获取教育详情
     */
    public Education getEducationById(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "education/" + id);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取教育详情失败: " + response.code());
            }

            String responseData = response.body().string();
            // 添加调试日志，打印服务器返回的原始JSON数据

            // 先解析响应为Result对象
            com.google.gson.JsonObject jsonObject = gson.fromJson(responseData, com.google.gson.JsonObject.class);
            // 获取data字段
            com.google.gson.JsonElement dataElement = jsonObject.get("data");
            // 解析data字段为Education对象
            return gson.fromJson(dataElement, Education.class);
        }
    }

    /**
     * 添加教育信息
     */
    public boolean addEducation(Education education) throws Exception {
        String json = gson.toJson(education);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "education/add")
                .post(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 更新教育信息
     */
    public boolean updateEducation(Education education) throws Exception {
        String json = gson.toJson(education);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "education/update")
                .put(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return true;
            } else {
                // 记录错误信息用于调试
                String errorMessage = "HTTP " + response.code() + ": " + response.message();
                if (response.body() != null) {
                    errorMessage += " - " + response.body().string();
                }
                throw new Exception(errorMessage);
            }
        }
    }

    /**
     * 删除教育信息
     */
    public boolean deleteEducation(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "education/delete/" + id)
                .delete();

        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 根据ID获取就业详情
     */
    public Employment getEmploymentById(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "employment/" + id);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取就业详情失败: " + response.code());
            }

            String responseData = response.body().string();
            // 先解析响应为Result对象
            com.google.gson.JsonObject jsonObject = gson.fromJson(responseData, com.google.gson.JsonObject.class);
            // 获取data字段
            com.google.gson.JsonElement dataElement = jsonObject.get("data");
            // 解析data字段为Employment对象
            return gson.fromJson(dataElement, Employment.class);
        }
    }

    /**
     * 添加就业信息
     */
    public boolean addEmployment(Employment employment) throws Exception {
        String json = gson.toJson(employment);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "employment/add")
                .post(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 更新就业信息
     */
    public boolean updateEmployment(Employment employment) throws Exception {
        String json = gson.toJson(employment);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "employment/update")
                .put(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 删除就业信息
     */
    public boolean deleteEmployment(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "employment/delete/" + id)
                .delete();

        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 根据ID获取医疗详情
     */
    public Medical getMedicalById(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "medical/" + id);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取医疗详情失败: " + response.code());
            }

            String responseData = response.body().string();
            // 打印完整的服务器响应

            // 先解析响应为Result对象
            com.google.gson.JsonObject jsonObject = gson.fromJson(responseData, com.google.gson.JsonObject.class);
            // 获取data字段
            com.google.gson.JsonElement dataElement = jsonObject.get("data");
            // 打印data字段内容

            // 解析data字段为Medical对象
            return gson.fromJson(dataElement, Medical.class);
        }
    }

    /**
     * 更新医疗信息
     */
    public boolean updateMedical(Medical medical) throws Exception {
        String json = gson.toJson(medical);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "medical/update")
                .put(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 添加医疗信息
     */
    public boolean addMedical(Medical medical) throws Exception {
        String json = gson.toJson(medical);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "medical/add")
                .post(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 删除医疗信息
     */
    public boolean deleteMedical(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "medical/delete/" + id)
                .delete();

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 根据ID获取户籍详情
     */
    public Household getHouseholdById(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "household/" + id);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("获取户籍详情失败: " + response.code());
            }

            String responseData = response.body().string();
            // 先解析响应为Result对象
            com.google.gson.JsonObject jsonObject = gson.fromJson(responseData, com.google.gson.JsonObject.class);
            // 获取data字段
            com.google.gson.JsonElement dataElement = jsonObject.get("data");
            // 解析data字段为Household对象
            return gson.fromJson(dataElement, Household.class);
        }
    }

    /**
     * 更新户籍信息
     */
    public boolean updateHousehold(Household household) throws Exception {
        String json = gson.toJson(household);
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "household/update")
                .put(body);

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * 删除户籍信息
     */
    public boolean deleteHousehold(Long id) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + "household/delete/" + id)
                .delete();

        // 添加认证头
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * LocalDate类型适配器，处理"yyyy-MM-dd"格式
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static class LocalDateTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(formatter));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String dateString = json.getAsString().trim();
            try {
                // 处理 "yyyy-MM-dd" 格式
                if (dateString.length() == 10) {
                    return LocalDate.parse(dateString, formatter);
                }
                // 处理 "yyyy-MM-dd HH:mm:ss" 格式（从服务器来的数据可能包含时间）
                return LocalDate.parse(dateString.substring(0, 10), formatter);
            } catch (Exception e) {
                throw new JsonParseException("Failed parsing '" + dateString + "' as LocalDate", e);
            }
        }
    }

    /**
     * LocalDateTime类型适配器，处理"yyyy-MM-dd HH:mm:ss"格式
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static class LocalDateTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(formatter));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String dateString = json.getAsString().trim();
            try {
                return LocalDateTime.parse(dateString, formatter);
            } catch (Exception e) {
                throw new JsonParseException("Failed parsing '" + dateString + "' as LocalDateTime", e);
            }
        }
    }

    /**
     * 自定义日期类型适配器，处理多种日期格式
     */
    private static class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
        private final String[] dateFormats = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                "yyyy-MM-dd"
        };

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String dateString = json.getAsString().trim();

            for (String format : dateFormats) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat(format);
                    formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    formatter.setLenient(false);
                    return formatter.parse(dateString);
                } catch (ParseException e) {
                    // 尝试下一种格式
                }
            }

            throw new JsonParseException("Failed parsing '" + dateString + "' as Date. Expected formats: yyyy-MM-dd HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss.SSS, yyyy-MM-dd'T'HH:mm:ss.SSSZ, yyyy-MM-dd");
        }
    }
}