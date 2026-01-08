package com.example.cms_android.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.model.Employment;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EmploymentAdapter extends RecyclerView.Adapter<EmploymentAdapter.EmploymentViewHolder> {
    private Context context;
    private List<Employment> employmentList;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Employment employment);
    }

    public EmploymentAdapter(Context context, List<Employment> employmentList) {
        this.context = context;
        this.employmentList = employmentList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public EmploymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employment, parent, false);
        return new EmploymentViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull EmploymentViewHolder holder, int position) {
        Employment employment = employmentList.get(position);

        holder.tvResidentName.setText(employment.getResidentName() != null ? employment.getResidentName() : "未填写");
        holder.tvPosition.setText(employment.getPosition() != null ? employment.getPosition() : "未填写");

        String startDateStr = employment.getStartDate() != null ? employment.getStartDate().format(dateFormatter) : "";
        String endDateStr = employment.getEndDate() != null ? employment.getEndDate().format(dateFormatter) : "至今";
        holder.tvDateRange.setText(String.format("%s - %s", startDateStr, endDateStr));

        // 使用company作为工作单位
        String workUnit = employment.getCompany() != null ? employment.getCompany() : "未填写";
        holder.tvWorkUnit.setText(workUnit);

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(employment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return employmentList.size();
    }

    public static class EmploymentViewHolder extends RecyclerView.ViewHolder {
        TextView tvResidentName, tvPosition, tvDateRange, tvWorkUnit;
        View tvStatus;

        public EmploymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResidentName = itemView.findViewById(R.id.tv_resident_name);
            tvPosition = itemView.findViewById(R.id.tv_position);
            tvDateRange = itemView.findViewById(R.id.tv_date_range);
            tvWorkUnit = itemView.findViewById(R.id.tv_work_unit);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}