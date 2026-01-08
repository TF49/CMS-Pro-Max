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
import com.example.cms_android.network.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etIdCard, etNewPassword, etConfirmPassword;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        
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
        etIdCard = findViewById(R.id.et_id_card);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void initServices() {
        apiService = new ApiService();
        executorService = Executors.newSingleThreadExecutor();
    }
    
    private void setupListeners() {
        findViewById(R.id.btn_reset_password).setOnClickListener(v -> attemptResetPassword());
        findViewById(R.id.tv_login).setOnClickListener(v -> navigateToLogin());
    }
    
    private void attemptResetPassword() {
        String username = etUsername.getText().toString().trim();
        String idCard = etIdCard.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        
        // 输入验证
        if (!validateInput(username, idCard, newPassword, confirmPassword)) {
            return;
        }
        
        // 显示进度条
        showProgress(true);
        
        // 在后台线程执行密码重置请求
        executorService.execute(() -> {
            try {
                // 调用密码重置API
                LoginResponse response = apiService.resetPassword(username, idCard, newPassword);
                
                // 切换到主线程更新UI
                runOnUiThread(() -> {
                    showProgress(false);
                    
                    if (response.isSuccess()) {
                        // 密码重置成功，跳转到登录页面
                        Toast.makeText(ForgotPasswordActivity.this, "密码重置成功，请登录", Toast.LENGTH_SHORT).show();
                        navigateToLogin();
                    } else {
                        String message = response.getMessage() != null ? response.getMessage() : "密码重置失败";
                        Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ForgotPasswordActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    /**
     * 输入验证
     */
    private boolean validateInput(String username, String idCard, String newPassword, String confirmPassword) {
        if (username.isEmpty()) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
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
        
        if (newPassword.isEmpty()) {
            Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (newPassword.length() < 6) {
            Toast.makeText(this, "密码长度不能少于6个字符", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
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
        
        findViewById(R.id.btn_reset_password).setEnabled(!show);
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
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