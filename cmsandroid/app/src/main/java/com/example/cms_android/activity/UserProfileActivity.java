package com.example.cms_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cms_android.R;
import com.example.cms_android.auth.User;
import com.example.cms_android.utils.TokenManager;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserProfileActivity extends AppCompatActivity {

    private TokenManager tokenManager;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btn_back).getRootView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        tokenManager = new TokenManager(this);
        initViews();
    }

    private void initViews() {
        // Back button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Get user info
        User user = tokenManager.getUserInfo();
        String currentUsername = tokenManager.getUsername();

        // Top section
        TextView tvUsername = findViewById(R.id.tv_profile_username);
        TextView tvRole = findViewById(R.id.tv_profile_role);

        tvUsername.setText(currentUsername != null ? currentUsername : "未知用户");

        if (user != null) {
            tvRole.setText(getRoleName(user.getRole()));

            // Detail items
            setupInfoItem(R.id.layout_id_card, R.drawable.ic_person_rounded, "身份证号",
                    (user.getIdCard() != null && !user.getIdCard().isEmpty()) ? maskIdCard(user.getIdCard()) : "未绑定");

            setupInfoItem(R.id.layout_account_status, R.drawable.ic_person_rounded, "账号状态",
                    (user.getIsActive() != null && user.getIsActive()) ? "正常" : "禁用");

            setupInfoItem(R.id.layout_create_time, R.drawable.ic_person_rounded, "注册时间",
                    user.getCreateTime() != null ? dateTimeFormat.format(user.getCreateTime()) : "未知");

            setupInfoItem(R.id.layout_last_login, R.drawable.ic_person_rounded, "最后登录",
                    user.getLastLoginTime() != null ? dateTimeFormat.format(user.getLastLoginTime()) : "刚刚");
        } else {
            tvRole.setText("普通用户");
            setupInfoItem(R.id.layout_id_card, R.drawable.ic_person_rounded, "身份证号", "未获取到信息");
            setupInfoItem(R.id.layout_account_status, R.drawable.ic_person_rounded, "账号状态", "正常");
            setupInfoItem(R.id.layout_create_time, R.drawable.ic_person_rounded, "注册时间", "未知");
            setupInfoItem(R.id.layout_last_login, R.drawable.ic_person_rounded, "最后登录", "刚刚");
        }

        // Logout button
        findViewById(R.id.btn_logout_profile).setOnClickListener(v -> {
            tokenManager.clearToken();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupInfoItem(int layoutId, int iconRes, String label, String value) {
        View layout = findViewById(layoutId);
        ImageView ivIcon = layout.findViewById(R.id.iv_icon);
        TextView tvLabel = layout.findViewById(R.id.tv_label);
        TextView tvValue = layout.findViewById(R.id.tv_value);

        ivIcon.setImageResource(iconRes);
        tvLabel.setText(label);
        tvValue.setText(value);
    }

    private String getRoleName(String role) {
        if (role == null)
            return "未知角色";
        switch (role.toUpperCase()) {
            case "ADMIN":
                return "管理员";
            case "USER":
                return "普通用户";
            default:
                return role;
        }
    }

    private String maskIdCard(String idCard) {
        if (idCard.length() < 10)
            return idCard;
        return idCard.substring(0, 4) + "**********" + idCard.substring(idCard.length() - 4);
    }
}
