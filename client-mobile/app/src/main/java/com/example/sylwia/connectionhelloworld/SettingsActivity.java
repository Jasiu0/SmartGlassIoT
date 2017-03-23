package com.example.sylwia.connectionhelloworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    private Context mContext;
    private String mServerIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mContext = getApplicationContext();

        Button okButton = (Button)findViewById(R.id.ok_settings_button);
        EditText ipEditText = (EditText)findViewById(R.id.ip_edit_text);

        SharedPreferences preferences = mContext.getSharedPreferences("Settings", MODE_PRIVATE);
        mServerIp = preferences.getString("ServerIP", "192.168.0.11");

        assert ipEditText != null;
        ipEditText.setText(mServerIp);

        ipEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mServerIp = s.toString();
            }
        });

        assert okButton != null;
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = mContext.getSharedPreferences("Settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ServerIP", mServerIp);
                editor.apply();
                onBackPressed();
            }
        });
    }
}
