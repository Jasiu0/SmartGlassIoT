package com.example.sylwia.connectionhelloworld;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
//
//
//import com.example.sylwia.connectionhelloworld.voice.Grammar;
//
//import com.example.sylwia.connectionhelloworld.voice.Grammar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        Button studentButton = (Button) findViewById(R.id.studentButton);
        Button hostButton = (Button)findViewById(R.id.hostButton);
        Button settingsButton = (Button)findViewById(R.id.settingsButton);

        assert studentButton != null;
        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CameraActivty.class);
                startActivity(intent);
            }
        });

        assert hostButton != null;
        hostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RoomAdministratorActivity.class);
                startActivity(intent);
            }
        });

        assert settingsButton != null;
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}

