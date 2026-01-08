package com.example.cms_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.model.Education;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.ViewHolder> {
    private Context context;
    private List<Education> educationList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Education education);
    }

    public EducationAdapter(Context context, List<Education> educationList) {
        this.context = context;
        this.educationList = educationList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_education, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Education education = educationList.get(position);
        // 交换姓名和学校名称的数据
        holder.tvName.setText(education.getResidentName() != null ? education.getResidentName() : "");
        
        // 学历转换：将英文转换为中文
        String educationLevelText = "";
        if (education.getEducationLevel() != null) {
            switch (education.getEducationLevel()) {
                case "PRIMARY":
                    educationLevelText = "小学";
                    break;
                case "JUNIOR":
                    educationLevelText = "初中";
                    break;
                case "SENIOR":
                    educationLevelText = "高中";
                    break;
                case "VOCATIONAL":
                    educationLevelText = "中专";
                    break;
                case "COLLEGE":
                    educationLevelText = "大专";
                    break;
                case "BACHELOR":
                    educationLevelText = "本科";
                    break;
                case "MASTER":
                    educationLevelText = "硕士";
                    break;
                case "DOCTOR":
                    educationLevelText = "博士";
                    break;
                default:
                    educationLevelText = education.getEducationLevel();
                    break;
            }
        }
        holder.tvEducationLevel.setText(educationLevelText);
        
        holder.tvResidentName.setText(education.getSchoolName() != null ? education.getSchoolName() : "");
        holder.tvMajor.setText(education.getMajor() != null ? education.getMajor() : "");
        
        // 学位类型转换：将英文转换为中文
        String diplomaTypeText = "";
        if (education.getDiplomaType() != null) {
            switch (education.getDiplomaType()) {
                case "BACHELOR_DEGREE":
                    diplomaTypeText = "学士学位";
                    break;
                case "MASTER_DEGREE":
                    diplomaTypeText = "硕士学位";
                    break;
                case "DOCTOR_DEGREE":
                    diplomaTypeText = "博士学位";
                    break;
                case "ASSOCIATE_DEGREE":
                    diplomaTypeText = "副学士学位";
                    break;
                case "DIPLOMA":
                    diplomaTypeText = "毕业证书";
                    break;
                default:
                    diplomaTypeText = education.getDiplomaType();
                    break;
            }
        }
        holder.tvDegree.setText(diplomaTypeText);
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(education);
            }
        });
    }

    @Override
    public int getItemCount() {
        return educationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEducationLevel, tvMajor, tvDegree, tvResidentName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEducationLevel = itemView.findViewById(R.id.tv_education_level);
            tvMajor = itemView.findViewById(R.id.tv_major);
            tvDegree = itemView.findViewById(R.id.tv_degree);
            tvResidentName = itemView.findViewById(R.id.tv_resident_name);
        }
    }
}