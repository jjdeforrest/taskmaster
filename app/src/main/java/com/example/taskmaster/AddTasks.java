package com.example.taskmaster;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;

import type.CreateTaskInput;

public class AddTasks extends AppCompatActivity {


    public AppDatabase db;
    public String statusPicked;
    public AWSAppSyncClient awsAppSyncClient;
    private static final int OPEN_DOCUMENT_CODE = 2;
    public String TAG = "stg.MainActivity";
    public Uri imageUri;
    private List<Task> tasks;
    public ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tasks);


        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();


        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "task_items").allowMainThreadQueries().build();


        Button save = findViewById(R.id.submitted);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tasktitle = findViewById(R.id.taskTitle);
                String title = tasktitle.getText().toString();


                EditText taskdescription = findViewById(R.id.taskDescription);
                String description = taskdescription.getText().toString();

                runTaskMutation(title, description, "Not Complete");



                Task newTask = new Task(title, description, "Not Complete");
                TextView submitted = findViewById(R.id.submitted);
                submitted.setVisibility(View.VISIBLE);
                Intent i = new Intent(AddTasks.this, MainActivity.class);
                AddTasks.this.startActivity(i);
                Button fab = findViewById(R.id.submitted);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "submitted!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });

            }
        });

        Button uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, OPEN_DOCUMENT_CODE);


            }
        });



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == OPEN_DOCUMENT_CODE && resultCode == RESULT_OK) {
            if (resultData != null) {
                Uri imageUri = resultData.getData();
                ImageView imageContainer = findViewById(R.id.imageView);
                imageContainer.setImageURI(imageUri);
                imageContainer.setVisibility(View.VISIBLE);
                uploadWithTransferUtility(convertUriToFilePath(imageUri));
            }
        }
    }


    public void runTaskMutation(String title, String body, String state){
        CreateTaskInput createTaskInput = CreateTaskInput.builder().
                title(title).
                body(body).
                state(state).build();

        awsAppSyncClient.mutate(CreateTaskMutation.builder().input(createTaskInput).build())
                .enqueue(taskMutationCallback);//performs the callback when we want the data gets inserted

    }

    private GraphQLCall.Callback<CreateTaskMutation.Data> taskMutationCallback = new GraphQLCall.Callback<CreateTaskMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateTaskMutation.Data> response)
        {
            Log.i(TAG, "Added Task");
            Intent takeMeBackToMainPage = new Intent(AddTasks.this, MainActivity.class);
            AddTasks.this.startActivity(takeMeBackToMainPage);
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, e.toString());
        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        Log.i(TAG, "started");
    }

    private String convertUriToFilePath(Uri uri) {
        Log.i("filepath", uri.toString());
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        Log.i("filepath", "" + filePath);
        cursor.close();
        return filePath;
    }

    public void uploadWithTransferUtility(String filePath) {
        if(filePath == null){
            Toast.makeText(this, "No file found", Toast.LENGTH_LONG).show();
            return;
        }

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();
        File file = new File(filePath);

        TransferObserver uploadObserver =
                transferUtility.upload(
                        "public/sample.txt", file);
                        //new File(picturePath));
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int)percentDonef;
                Log.d(TAG, "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }
            @Override
            public void onError(int id, Exception ex) {
            }
        });
        if (TransferState.COMPLETED == uploadObserver.getState()) {
        }
        Log.d(TAG, "Bytes Transferred: " + uploadObserver.getBytesTransferred());
        Log.d(TAG, "Bytes Total: " + uploadObserver.getBytesTotal());
    }

}