package com.example.taskmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class SettingsPage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        Button save = findViewById(R.id.button4);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView username = findViewById(R.id.username);
                SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = p.edit();
                editor.putString("username",username.getText().toString());
                editor.apply();

                Intent i = new Intent(SettingsPage.this,MainActivity.class);
                SettingsPage.this.startActivity(i);

//
//                Toast usernameSubmitted = Toast.makeText(getApplicationContext(), username, Toast.LENGTH_SHORT);
//                usernameSubmitted.show();


            }
        });
    }
}
