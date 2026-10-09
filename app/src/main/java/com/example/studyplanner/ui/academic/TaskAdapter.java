package com.example.studyplanner.ui.academic;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyplanner.R;
import com.example.studyplanner.data.dao.TaskDao;
import com.example.studyplanner.data.model.Task;
import android.content.Intent;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;
    private TaskDao taskDao;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
        this.taskDao = new TaskDao(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.tvTaskTitle.setText(task.getTitle());
        holder.tvTaskDesc.setText(task.getDescription());
        holder.tvDeadline.setText("🕒 " + task.getDeadline());
        holder.tvLocation.setText("📍 " + task.getLocation());

        if (task.getPriority() == 3) {
            holder.tvPriority.setText("High Priority");
            holder.tagPriority.setCardBackgroundColor(Color.parseColor("#E53935"));
        } else if (task.getPriority() == 2) {
            holder.tvPriority.setText("Medium Priority");
            holder.tagPriority.setCardBackgroundColor(Color.parseColor("#FB8C00"));
        } else {
            holder.tvPriority.setText("Low Priority");
            holder.tagPriority.setCardBackgroundColor(Color.parseColor("#43A047"));
        }

        // Bấm dấu 3 chấm
        holder.btnMoreOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.btnMoreOptions);
            popupMenu.getMenu().add(0, 0, 0, "Sửa");
            popupMenu.getMenu().add(0, 1, 1, "Xóa");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 0) {
                    // BẤM SỬA: Mở AddEditTaskActivity và truyền TASK_ID
                    Intent intent = new Intent(context, AddEditTaskActivity.class);
                    intent.putExtra("TASK_ID", task.getId());
                    context.startActivity(intent);
                } else if (item.getItemId() == 1) {
                    // BẤM XÓA
                    taskDao.deleteTask(task.getId());
                    taskList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Đã xóa!", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateData(List<Task> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle, tvTaskDesc, tvDeadline, tvLocation, tvPriority;
        CardView tagPriority;
        ImageButton btnMoreOptions;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskDesc = itemView.findViewById(R.id.tvTaskDesc);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tagPriority = itemView.findViewById(R.id.tagPriority);
            btnMoreOptions = itemView.findViewById(R.id.btnMoreOptions);
        }
    }
}