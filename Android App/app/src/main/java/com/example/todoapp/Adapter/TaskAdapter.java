package com.example.todoapp.Adapter;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todoapp.Model.Task;
import com.example.todoapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private TaskActionsListener listener;
    private boolean isDarkModeEnabled;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public TaskAdapter(List<Task> taskList, TaskActionsListener listener, boolean isDarkModeEnabled) {
        this.taskList = taskList;
        this.listener = listener;
        this.isDarkModeEnabled = isDarkModeEnabled;
    }

    public void setDarkModeEnabled(boolean isDarkModeEnabled) {
        this.isDarkModeEnabled = isDarkModeEnabled;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.taskTitle.setText(task.getTitle());
        holder.taskCompleted.setOnCheckedChangeListener(null);
        holder.taskCompleted.setChecked(task.isCompleted());

        int backgroundColor = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            backgroundColor = isDarkModeEnabled ?
                    holder.itemView.getResources().getColor(R.color.dark_background_color, holder.itemView.getContext().getTheme()) :
                    holder.itemView.getResources().getColor(R.color.light_background_color, holder.itemView.getContext().getTheme());
        }

        int textColor = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textColor = isDarkModeEnabled ?
                    holder.itemView.getResources().getColor(R.color.dark_text_color, holder.itemView.getContext().getTheme()) :
                    holder.itemView.getResources().getColor(R.color.light_text_color, holder.itemView.getContext().getTheme());
        }

        holder.taskTitle.setTextColor(textColor);
        holder.dueDateTextView.setTextColor(textColor);
        holder.taskCompleted.setButtonTintList(ColorStateList.valueOf(textColor));

        holder.itemView.setBackgroundColor(backgroundColor);

        if (task.isCompleted()) {
            holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.taskCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            listener.onUpdateTask(task);
            notifyItemChanged(position);
        });

        if (task.getDueDate() != null) {
            holder.dueDateTextView.setText(task.getDueDate());
        } else {
            holder.dueDateTextView.setText("No date set");
        }

        holder.taskTitle.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String newTitle = holder.taskTitle.getText().toString();
                if (!newTitle.equals(task.getTitle())) {
                    task.setTitle(newTitle);
                    listener.onUpdateTask(task);
                }
            }
        });

        holder.deleteTaskButton.setOnClickListener(v -> listener.onDeleteTask(task));

        holder.dueDateTextView.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    holder.itemView.getContext(),
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        String selectedDate = dateFormat.format(calendar.getTime());
                        task.setDueDate(selectedDate);
                        listener.onUpdateTask(task);
                        holder.dueDateTextView.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox taskCompleted;
        EditText taskTitle;
        TextView dueDateTextView;
        ImageButton deleteTaskButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCompleted = itemView.findViewById(R.id.taskCompleted);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            dueDateTextView = itemView.findViewById(R.id.dueDateTextView);
            deleteTaskButton = itemView.findViewById(R.id.deleteTaskButton);
        }
    }

    public interface TaskActionsListener {
        void onUpdateTask(Task task);
        void onDeleteTask(Task task);
    }
}
