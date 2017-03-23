package com.example.sylwia.connectionhelloworld;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sylwia.connectionhelloworld.connection.RestConnection;
import com.kingfisherphuoc.quickactiondialog.AlignmentFlag;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RoomViewActivity extends AppCompatActivity {
    private Context mContext;

    private String mRoomId;
    private JSONObject mRoomJson;

    private TextView mRoomNumber, mRoomDescription, mConsultations, mPresent, mBusy, mMessage;
    private ImageView mLightIcon, mMovementIcon, mNoiseIcon, mGasIcon;
    private Spinner mSpinner;

    private String currentHostName;

    private Map<String, String> mSensorNamesMap;
    private Map<String, String> mSensorsLastChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_view);

        mContext = getApplicationContext();

        Intent intent = getIntent();
        mRoomId = intent.getStringExtra("roomId");
        Log.d("Ok", mRoomId);

        findUiElements();

        RestConnection restConnection = new RestConnection(mContext);
        restConnection.getJsonData("rooms", mRoomId, new RestConnection.IGetRequestObserver() {
            @Override
            public void onRequestCompleted(int responseCode, JSONObject json) {
                if (responseCode == HttpURLConnection.HTTP_OK){
                    mRoomJson = json;
                    updateUi();
                }
            }
        });

        setIconsListeners();

        mSensorNamesMap = new HashMap<>();
        mSensorNamesMap.put("gas", "Czujnik gazu");
        mSensorNamesMap.put("light", "Czujnik światła");
        mSensorNamesMap.put("movement", "Czujnik ruchu");
        mSensorNamesMap.put("noise", "Czujnik dźwięku");

        getSensorsLastChange();


        Button historyButton = (Button)this.findViewById(R.id.historyButton);
        assert historyButton != null;
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HistoryChartActivity.class);
                intent.putExtra("roomId", String.valueOf(mRoomId));
                startActivity(intent);
            }
        });

    }

    private void findUiElements() {
        mRoomNumber = (TextView) findViewById(R.id.roomNumber);
        mRoomDescription = (TextView) findViewById(R.id.roomDescription);
        mConsultations = (TextView) findViewById(R.id.consultations);
        mPresent = (TextView) findViewById(R.id.present);
        mBusy = (TextView) findViewById(R.id.busy);
        mMessage = (TextView) findViewById(R.id.message);
        mLightIcon = (ImageView) findViewById(R.id.lightIcon);
        mMovementIcon = (ImageView) findViewById(R.id.movementIcon);
        mNoiseIcon = (ImageView) findViewById(R.id.noiseIcon);
        mGasIcon = (ImageView) findViewById(R.id.gasIcon);
        mSpinner = (Spinner) findViewById(R.id.spinner);
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

    private void updateUi() {
        try {
            final JSONObject roomJson = mRoomJson.getJSONObject("room");
            final JSONObject hostsJson = roomJson.getJSONObject("hosts");

            mRoomNumber.setText(roomJson.getString("name"));
            mRoomDescription.setText(roomJson.getString("description"));

            updateIconView(mLightIcon, roomJson.getBoolean("light"));
            updateIconView(mMovementIcon, roomJson.getBoolean("movement"));
            updateIconView(mNoiseIcon, roomJson.getBoolean("noise"));
            updateIconView(mGasIcon, roomJson.getBoolean("gas"));

            setupSpinner(hostsJson);

            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    currentHostName = adapterView.getItemAtPosition(i).toString();
                    try {
                        final JSONObject hostDataJson = hostsJson.getJSONObject(currentHostName);

                        mConsultations.setText(hostDataJson.getString("consultations"));
                        mMessage.setText(hostDataJson.getString("messages"));

                        updateBooleanTextView(mPresent, hostDataJson.getBoolean("present"));
                        updateBooleanTextView(mBusy, hostDataJson.getBoolean("busy"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    mConsultations.setText("");
                    mMessage.setText("");
                    mPresent.setText("");
                    mBusy.setText("");
                }
            });
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

    private void updateBooleanTextView(TextView textView, boolean value) {
        if(value) {
            textView.setText("Tak");
        } else {
            textView.setText("Nie");
        }
    }

    private void setupSpinner(final JSONObject jsonObject) {
        List<String> nameList = new ArrayList<>();
        Iterator<String> hostNamesIt = jsonObject.keys();

        while (hostNamesIt.hasNext()) {
            nameList.add(hostNamesIt.next());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nameList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(arrayAdapter);
    }
}
