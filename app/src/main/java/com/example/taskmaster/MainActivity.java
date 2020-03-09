package com.example.taskmaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignOutOptions;
import com.amazonaws.mobile.client.UserState;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static AppDatabase db;
    public AWSAppSyncClient awsAppSyncClient;
    public Context context;
    public RecyclerView recyclerView;

    public String TAG = "stg.MainActivity";
    public List<Task> listOfTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));

        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();
        //getTaskItems();

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "task_items").allowMainThreadQueries().build();
        this.listOfTasks = new ArrayList<Task>();

        this.listOfTasks = AppDatabase.taskDao().getTasks();
        for (Task item : listOfTasks){
            Log.i(TAG, item.body + item.title + item.state);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyTaskRecyclerViewAdapter(this.listOfTasks, null));

        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));

        context = this.getApplicationContext();

        Button addTask = findViewById(R.id.button);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAddTask = new Intent(MainActivity.this, AddTasks.class);
                MainActivity.this.startActivity(goToAddTask);
            }
        });


        Button goToAllTasksPage = findViewById(R.id.alltasks);
        goToAllTasksPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoAllTasks = new Intent(MainActivity.this, AllTasks.class);
                MainActivity.this.startActivity((gotoAllTasks));
            }
        });

        Button goToSettingsPage = findViewById(R.id.settings);
        goToSettingsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettingsPage = new Intent(MainActivity.this, Settings.class);
                MainActivity.this.startActivity((goToSettingsPage));
            }
        });
        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));


        //uncomment right away
        Button logOutButton = findViewById(R.id.signout);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AWSMobileClient.getInstance().signOut(SignOutOptions.builder().signOutGlobally(true).build(), new Callback<Void>() {
                    @Override
                    public void onResult(final Void result) {

                        Log.d(TAG, "signed-out");
                        Intent i = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(i);


                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "sign-out error", e);
                    }
                });

            }
        });


        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {

                    @Override
                    public void onResult(UserStateDetails userStateDetails) {
//                        Log.i("INIT", "onResult: " + userStateDetails.getUserState());
                        if (userStateDetails.getUserState().equals(UserState.SIGNED_OUT)) {

                            AWSMobileClient.getInstance().showSignIn(MainActivity.this, new Callback<UserStateDetails>() {
                                @Override
                                public void onResult(UserStateDetails result) {
//

                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e(TAG, "onError: ", e);

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("INIT", "Initialization error.", e);
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String helloUser = sharedPreferences.getString("user_name", "default");
        TextView nameUser = findViewById(R.id.textView4);
        nameUser.setText(AWSMobileClient.getInstance().getUsername() + "'s tasks");
    }
    }