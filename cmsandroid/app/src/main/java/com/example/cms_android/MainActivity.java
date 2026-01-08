package com.example.cms_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.cms_android.activity.LoginActivity;
import com.example.cms_android.ListActivity.ResidentListActivity;
import com.example.cms_android.ListActivity.HouseholdListActivity;
import com.example.cms_android.ListActivity.EducationListActivity;
import com.example.cms_android.ListActivity.EmploymentListActivity;
import com.example.cms_android.ListActivity.MedicalListActivity;
import com.example.cms_android.ListActivity.VehicleListActivity;
import com.example.cms_android.ListActivity.PropertyListActivity;
import com.example.cms_android.ListActivity.SocialSecurityListActivity;
import com.example.cms_android.activity.UserProfileActivity;
import com.example.cms_android.utils.TokenManager;

public class MainActivity extends AppCompatActivity {

    private TextView textCurrentTime;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable timeRunnable;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 显示主页面
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化时间显示
        textCurrentTime = findViewById(R.id.text_current_time);
        updateCurrentTime();

        // 定期更新时间（每秒）
        timeRunnable = () -> {
            updateCurrentTime();
            handler.postDelayed(timeRunnable, 1000);
        };
        handler.postDelayed(timeRunnable, 1000);

        // 初始化用户名显示

        TextView tvHeaderUsername = findViewById(R.id.tv_header_username);
        final TokenManager tokenManager = new TokenManager(this);

        String username = tokenManager.getUsername();
        if (username != null && !username.isEmpty()) {
            tvHeaderUsername.setText(username);
        }

        // 设置用户名点击跳转到个人信息
        View layoutUserProfile = findViewById(R.id.layout_user_profile);
        layoutUserProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        // 初始化控件
        Button btnResidentManagement = findViewById(R.id.btn_resident_management);
        Button btnHouseholdManagement = findViewById(R.id.btn_household_management);
        Button btnEducationManagement = findViewById(R.id.btn_education_management);
        Button btnEmploymentManagement = findViewById(R.id.btn_employment_management);
        Button btnMedicalManagement = findViewById(R.id.btn_medical_management);
        Button btnSocialSecurityManagement = findViewById(R.id.btn_social_security_management);
        Button btnVehicleManagement = findViewById(R.id.btn_vehicle_management);
        Button btnPropertyManagement = findViewById(R.id.btn_property_management);

        // 设置点击事件
        btnResidentManagement.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResidentListActivity.class);
            startActivity(intent);
        });

        btnHouseholdManagement.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HouseholdListActivity.class);
            startActivity(intent);
        });

        btnEducationManagement.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EducationListActivity.class);
            startActivity(intent);
        });

        btnEmploymentManagement.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmploymentListActivity.class);
            startActivity(intent);
        });

        btnMedicalManagement.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MedicalListActivity.class);
            startActivity(intent);
        });

        btnSocialSecurityManagement.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SocialSecurityListActivity.class);
            startActivity(intent);
        });

        btnVehicleManagement.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VehicleListActivity.class);
            startActivity(intent);
        });

        btnPropertyManagement.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PropertyListActivity.class);
            startActivity(intent);
        });

    }

    private void updateCurrentTime() {
        Date currentDate = new Date();
        String formattedTime = dateFormat.format(currentDate);
        textCurrentTime.setText(formattedTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除定时器，避免内存泄漏
        if (timeRunnable != null) {
            handler.removeCallbacks(timeRunnable);
        }
    }
}