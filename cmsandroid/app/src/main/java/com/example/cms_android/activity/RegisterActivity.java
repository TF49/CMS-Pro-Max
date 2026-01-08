package com.example.cms_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.auth.LoginResponse;
import com.example.cms_android.auth.RegisterResponse;
import com.example.cms_android.network.ApiService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etPassword, etConfirmPassword, etIdCard;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // 启用返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        initViews();
        initServices();
        setupListeners();
    }
    
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etIdCard = findViewById(R.id.et_id_card);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void initServices() {
        apiService = new ApiService();
        executorService = Executors.newSingleThreadExecutor();
    }
    
    private void setupListeners() {
        findViewById(R.id.btn_register).setOnClickListener(v -> attemptRegister());
        findViewById(R.id.tv_login).setOnClickListener(v -> navigateToLogin());
    }
    
    private void attemptRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String idCard = etIdCard.getText().toString().trim();
        
        // 输入验证
        if (!validateInput(username, password, confirmPassword, idCard)) {
            return;
        }
        
        // 显示进度条
        showProgress(true);
        
        // 在后台线程执行注册请求
        executorService.execute(() -> {
            try {
                // 调用注册API
                RegisterResponse response = apiService.register(username, password, idCard);
                
                // 切换到主线程更新UI
                runOnUiThread(() -> {
                    showProgress(false);
                    
                    if (response.isSuccess()) {
                        // 注册成功，跳转到登录页面
                        Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                        navigateToLogin();
                    } else {
                        String message = response.getMessage() != null ? response.getMessage() : "注册失败";
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(RegisterActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    /**
     * 输入验证
     */
    private boolean validateInput(String username, String password, String confirmPassword, String idCard) {
        if (username.isEmpty()) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (username.length() < 3 || username.length() > 20) {
            Toast.makeText(this, "用户名长度应在3-20个字符之间", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (password.length() < 6) {
            Toast.makeText(this, "密码长度不能少于6个字符", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (idCard.isEmpty()) {
            Toast.makeText(this, "请输入身份证号", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!isValidIdCard(idCard)) {
            Toast.makeText(this, "身份证号格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    /**
     * 验证身份证号格式
     */
    private boolean isValidIdCard(String idCard) {
        // 简单的身份证号格式验证
        if (idCard == null) {
            return false;
        }
        
        String trimmedIdCard = idCard.trim();
        
        // 18位或15位身份证号验证
        if (trimmedIdCard.length() == 18) {
            return trimmedIdCard.matches("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
        } else if (trimmedIdCard.length() == 15) {
            return trimmedIdCard.matches("^[1-9]\\d{5}\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}$");
        }
        
        return false;
    }
    
    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
        
        findViewById(R.id.btn_register).setEnabled(!show);
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}