package com.example.sylwia.connectionhelloworld;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DevicesActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        mContext = getApplicationContext();

        Button device1Button = (Button)findViewById(R.id.device1_button);
        Button device2Button = (Button)findViewById(R.id.device2_button);

        assert device1Button != null;
        device1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RoomViewActivity.class);
                intent.putExtra("id", "1");
                startActivity(intent);
            }
        });

        assert device2Button != null;
        device2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RoomViewActivity.class);
                intent.putExtra("id", "2");
                startActivity(intent);
            }
        });
    }
}
