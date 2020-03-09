
package com.example.taskmaster;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

//import com.amazonaws.amplify.generated.graphql.ListTasksQuery;

public class AllTasks extends AppCompatActivity {

    static AppDatabase db;
    public EditText username;
    public TextView result;
    public Button buttonSubmit;
    public AWSAppSyncClient awsAppSyncClient;

    public String TAG = "stg.MainActivity";
    List<Task> listOfTasks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        this.listOfTasks = new ArrayList<Task>();
        getTaskItems();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyTaskRecyclerViewAdapter(this.listOfTasks, null));

    }
//    @Override
//    public void onClick(Task task){
//        Toast.makeText(getApplicationContext(), task.getBody(), Toast.LENGTH_LONG).show();
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        db = (AppDatabase) taskDao().getTasks();
//        TextView allTasks = findViewById(R.id.recyclerView);
//        allTasks.setText((CharSequence) db);
//        Log.i(TAG, "resumed");
//    }


    //this method enables me to query data stored in dynamodb to render on my front page
    public void getTaskItems() {

        awsAppSyncClient.query(ListTasksQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(tasksCallback);
    }

    private GraphQLCall.Callback<ListTasksQuery.Data> tasksCallback = new GraphQLCall.Callback<ListTasksQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListTasksQuery.Data> response) {
            Log.i(TAG, response.data().listTasks().items().toString());

            if (listOfTasks.size() == 0 || response.data().listTasks().items().size() != listOfTasks.size()) {

                listOfTasks.clear();

                for (ListTasksQuery.Item item : response.data().listTasks().items()) {
                    Task addTask = new Task(item.title(), item.body(), item.state());
                    listOfTasks.add(addTask);
                }
                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {
                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                };
                handler.obtainMessage().sendToTarget();
            }
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, e.toString());
            AppDatabase.taskDao().getTasks();
        }
    };
}