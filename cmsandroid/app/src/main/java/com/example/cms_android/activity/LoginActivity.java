package com.example.cms_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.MainActivity;
import com.example.cms_android.R;
import com.example.cms_android.auth.LoginResponse;
import com.example.cms_android.auth.User;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.utils.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private TokenManager tokenManager;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initViews();
        initServices();
        setupListeners();
    }
    
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void initServices() {
        apiService = new ApiService();
        tokenManager = new TokenManager(this);
        executorService = Executors.newSingleThreadExecutor();
    }
    
    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        findViewById(R.id.tv_register).setOnClickListener(v -> navigateToRegister());
        findViewById(R.id.tv_forgot_password).setOnClickListener(v -> navigateToForgotPassword());
    }
    
    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
    
    private void navigateToForgotPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
    
    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 显示进度条
        showProgress(true);
        
        // 在后台线程执行登录请求
        executorService.execute(() -> {
            try {
                LoginResponse response = apiService.login(username, password);
                
                // 切换到主线程更新UI
                runOnUiThread(() -> {
                    showProgress(false);
                    
                    if (response.isSuccess() && response.getData() != null) {
                        // 检查用户角色 - 管理员不能登录Android应用
                        User user = response.getData().getUser();
                        if (user != null && "ADMIN".equals(user.getRole())) {
                            Toast.makeText(LoginActivity.this, "管理员请使用网页端登录", Toast.LENGTH_LONG).show();
                            return;
                        }
                        
                        // 保存Token和用户信息
                        String token = response.getData().getToken();
                        tokenManager.saveToken(token);

                        // 保存完整用户信息
                        if (user != null) {
                            tokenManager.saveUserInfo(user);
                        }
                        
                        // 设置全局Token
                        ApiService.setAuthToken(token);
                        
                        // 跳转到主页面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = response.getMessage() != null ? response.getMessage() : "登录失败";
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}