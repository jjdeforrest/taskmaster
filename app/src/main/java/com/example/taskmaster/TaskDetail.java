package com.example.taskmaster;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class TaskDetail extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
//
//        String title = getIntent().getStringExtra("taskTitle");
//        String description = getIntent().getStringExtra("taskDescription");
//
//        TextView taskTitle = findViewById(R.id.taskTitle);
//        TextView taskDescription = findViewById(R.id.taskDescription);
//
//
//        taskTitle.setText(title);
//        taskDescription.setText(description);



//                Toast usernameSubmitted = Toast.makeText(getApplicationContext(), ta, Toast.LENGTH_SHORT);
//                usernameSubmitted.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        String taskTitle = getIntent().getStringExtra("task");
        String descriptiontask = getIntent().getStringExtra("description");
        String status = getIntent().getStringExtra("status");
        TextView title = findViewById(R.id.taskTitle);
        TextView description = findViewById(R.id.taskDescription);
        TextView taskstatus = findViewById(R.id.status);
        title.setText(taskTitle);
        description.setText(descriptiontask);
        String notcomplete= "Not Complete";
        if(status == null || status == "Not Complete") {
            taskstatus.setText("Not Complete");
        } else {
            taskstatus.setText(status);
        }


        Toast usernameSubmitted = Toast.makeText(getApplicationContext(), taskTitle, Toast.LENGTH_LONG);
        Toast usernameSubmitted2 = Toast.makeText(getApplicationContext(), descriptiontask, Toast.LENGTH_LONG);
        Toast usernameSubmitted3 = Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG);
        usernameSubmitted.show();
        usernameSubmitted2.show();
        usernameSubmitted3.show();


    }

}
