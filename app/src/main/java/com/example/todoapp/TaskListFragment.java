package com.example.todoapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskListFragment extends Fragment {
    private static final String KEY_EXTRA_TASK_ID = "extra_task_id";
    RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private boolean subtitleVisible = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        recyclerView = view.findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_task_menu, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (subtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.new_task) {
            Task task = new Task();
            TaskStorage.getInstance().addTask(task);
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra(TaskListFragment.KEY_EXTRA_TASK_ID, task.getId());
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.show_subtitle) {
            subtitleVisible = !subtitleVisible;
            getActivity().invalidateOptionsMenu();
            updateSubtitle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateView() {
        TaskStorage taskStorage = TaskStorage.getInstance();
        List<Task> tasks = taskStorage.getTasks();

        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(tasks);
            recyclerView.setAdapter(taskAdapter);
        } else {
            taskAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    public void updateSubtitle() {
        TaskStorage taskStorage = TaskStorage.getInstance();
        List<Task> tasks = taskStorage.getTasks();
        int todoTasksCount = 0;
        for (Task task : tasks) {
            if (!task.isDone()) {
                todoTasksCount++;
            }
        }
        String subtitle = getString(R.string.subtitle_format, todoTasksCount);
        if (!subtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
    }


    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }


    private class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView nameTextView;
        private ImageView iconImageView;
        private CheckBox checkBox;

        private final TextView dateTextView;
        private Task task;

        public TaskHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_task, parent, false));
            itemView.setOnClickListener(this);
            nameTextView = itemView.findViewById(R.id.item_task_name);
            dateTextView = itemView.findViewById(R.id.item_task_date);
            iconImageView = itemView.findViewById(R.id.task_category_icon);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void bind(Task task) {
            this.task = task;
            if (task.getName().length() < 20) {
                nameTextView.setText(task.getName());
            } else {
                nameTextView.setText(task.getName().substring(0, 21) + "...");
            }
            if (task.getCategory().equals(Category.HOME)) {
                iconImageView.setImageResource(R.drawable.ic_home);
            } else {
                iconImageView.setImageResource(R.drawable.ic_studies);
            }
            dateTextView.setText(task.getDate().toString());
            checkBox.setChecked(task.isDone());
            if (task.isDone()) {
                nameTextView.setPaintFlags(nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                nameTextView.setPaintFlags(nameTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra(KEY_EXTRA_TASK_ID, task.getId());
            startActivity(intent);
        }
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {
        private List<Task> taskList;

        public TaskAdapter(List<Task> taskList) {
            this.taskList = taskList;
        }

        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TaskHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
            holder.bind(taskList.get(position));
            CheckBox checkBox = holder.getCheckBox();
            checkBox.setChecked((taskList.get(position).isDone()));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    taskList.get(holder.getBindingAdapterPosition()).setDone(isChecked));
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }
    }
}
