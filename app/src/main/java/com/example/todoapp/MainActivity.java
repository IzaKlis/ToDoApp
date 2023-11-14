package com.example.todoapp;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import java.util.UUID;

public class MainActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected Fragment createFragment() {
        UUID taskId = (UUID)getIntent().getSerializableExtra("extra_task_id");
        return TaskFragment.newInstance(taskId);
    }
}