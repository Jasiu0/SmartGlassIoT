package com.example.sylwia.connectionhelloworld;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sylwia.connectionhelloworld.connection.RestConnection;
import com.kingfisherphuoc.quickactiondialog.AlignmentFlag;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sylwia on 09/01/2017.
 */

public class RoomAdministratorActivity extends AppCompatActivity {

    private Context mContext;

    private JSONObject mRoomJson;
    private JSONObject mHostsJson;
    private JSONObject mHostDataJson;

    private TextView mHostName, mRoomNumber;
    private EditText mConsultations, mMessage;
    private CheckBox mPresent, mBusy;
    private ImageView mLightIcon, mMovementIcon, mNoiseIcon, mGasIcon, mSaveIcon;

    private String mCurrentHostName;
    private String mRoomId;

    private Map<String, String> mSensorNamesMap;
    private Map<String, String> mSensorsLastChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_administrator);

        mContext = getApplicationContext();

        mRoomId = "0";
        mCurrentHostName = "Jacek Rumiński";

        findUiElements();

        RestConnection restConnection = new RestConnection(mContext);
        restConnection.getJsonData("rooms", mRoomId, new RestConnection.IGetRequestObserver() {
            @Override
            public void onRequestCompleted(int responseCode, JSONObject json) {
                if (responseCode == HttpURLConnection.HTTP_OK){
                    try {
                        mRoomJson = json.getJSONObject("room");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    updateUi();
                }
            }
        });

        setIconsListeners();
        setSaveOnClickListener();

        mSensorNamesMap = new HashMap<>();
        mSensorNamesMap.put("gas", "Czujnik gazu");
        mSensorNamesMap.put("light", "Czujnik światła");
        mSensorNamesMap.put("movement", "Czujnik ruchu");
        mSensorNamesMap.put("noise", "Czujnik dźwięku");

        getSensorsLastChange();
    }

    private void findUiElements() {
        mRoomNumber = (TextView) findViewById(R.id.roomNumber);
        mHostName = (TextView) findViewById(R.id.hostName);
        mConsultations = (EditText) findViewById(R.id.consultations);
        mMessage = (EditText) findViewById(R.id.message);
        mPresent = (CheckBox) findViewById(R.id.present);
        mBusy = (CheckBox) findViewById(R.id.busy);
        mLightIcon = (ImageView) findViewById(R.id.lightIcon);
        mMovementIcon = (ImageView) findViewById(R.id.movementIcon);
        mNoiseIcon = (ImageView) findViewById(R.id.noiseIcon);
        mGasIcon = (ImageView) findViewById(R.id.gasIcon);
        mSaveIcon = (ImageView) findViewById(R.id.saveIcon);
    }

    private void updateUi() {
        try {
            mHostsJson = mRoomJson.getJSONObject("hosts");
            mHostDataJson = mHostsJson.getJSONObject(mCurrentHostName);

            mRoomNumber.setText(mRoomJson.getString("name"));
            mHostName.setText(mCurrentHostName);
            updateIconView(mLightIcon, mRoomJson.getBoolean("light"));
            updateIconView(mMovementIcon, mRoomJson.getBoolean("movement"));
            updateIconView(mNoiseIcon, mRoomJson.getBoolean("noise"));
            updateIconView(mGasIcon, mRoomJson.getBoolean("gas"));

            mPresent.setChecked(mHostDataJson.getBoolean("present"));
            mBusy.setChecked(mHostDataJson.getBoolean("busy"));

            mConsultations.setText(mHostDataJson.getString("consultations"));
            mMessage.setText(mHostDataJson.getString("messages"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateIconView(ImageView imageView, boolean present) {
        if(present) {
            imageView.setAlpha(1.0f);
        } else {
            imageView.setAlpha(0.15f);
        }
    }

    private void setIconsListeners() {
        setIconListener(mGasIcon, "gas");
        setIconListener(mLightIcon, "light");
        setIconListener(mMovementIcon, "movement");
        setIconListener(mNoiseIcon, "noise");
    }

    private void setIconListener(ImageView icon, final String sensorName) {
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                BubbleDialogFragment bubbleDialogFragment = new BubbleDialogFragment();
                bubbleDialogFragment.setAnchorView(view);
                bubbleDialogFragment.setAligmentFlags(AlignmentFlag.ALIGN_ANCHOR_VIEW_LEFT | AlignmentFlag.ALIGN_ANCHOR_VIEW_BOTTOM);
                bubbleDialogFragment.show(getSupportFragmentManager(), null);
                bubbleDialogFragment.setDialogContent(mSensorNamesMap.get(sensorName), mSensorsLastChange.get(sensorName));
            }
        });
    }

    private void getSensorsLastChange(){
        mSensorsLastChange = new HashMap<>();

        String[] sensors = new String[]{"gas", "light", "movement", "noise"};
        for(final String sensor : sensors){
            RestConnection restConnection = new RestConnection(mContext);
            restConnection.getJsonData("history/last/" + mRoomId, sensor, new RestConnection.IGetRequestObserver() {
                @Override
                public void onRequestCompleted(int responseCode, JSONObject json) {
                    if(responseCode == 404){
                        mSensorsLastChange.put(sensor, "-");
                    }

                    try{
                        Date date = new Date((long) (1000 * json.getDouble("timestamp")));
                        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
                        mSensorsLastChange.put(sensor, dateFormat.format(date));
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void setSaveOnClickListener() {
        mSaveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postChanges();
                Toast.makeText(mContext, "Zmiany zostały zapisane", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postChanges() {

        JSONObject jsonToPost = prepareJsonToPost();
        RestConnection restConnection = new RestConnection(mContext);
        restConnection.postDeviceData("rooms", mRoomId, jsonToPost);
        Log.d("OK", "POST");
    }

    private JSONObject prepareJsonToPost() {
        JSONObject jsonToPost = new JSONObject();
        try {
            mHostDataJson.put("consultations", mConsultations.getText());
            mHostDataJson.put("messages", mMessage.getText());
            mHostDataJson.put("busy", String.valueOf(mBusy.isChecked()));
            mHostDataJson.put("present", String.valueOf(mPresent.isChecked()));
            mHostsJson.put(mCurrentHostName, mHostDataJson);
            jsonToPost.put("hosts", mHostsJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonToPost;
    }
}
