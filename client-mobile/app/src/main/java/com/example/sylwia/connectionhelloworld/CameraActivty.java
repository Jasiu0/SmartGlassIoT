package com.example.sylwia.connectionhelloworld;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.sylwia.connectionhelloworld.connection.RestConnection;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CameraActivty extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView mScannerView;
    private Context mContext;
    private int mRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mContext = getApplicationContext();

        if(mScannerView == null) {
            mScannerView = new ZXingScannerView(this);
            setContentView(mScannerView);
            Log.d("CAMERA", "CONSTRUCTOR");
        }
        Log.d("CAMERA", "CREATE");
    }

    @Override
    protected void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        Log.d("CAMERA", "RESUME");
    }

    @Override
    public void onPause() {
        mScannerView.stopCamera();
        Log.d("CAMERA", "PAUSE");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mScannerView.destroyDrawingCache();
        mScannerView = null;
        Log.d("CAMERA", "DESTROY");
        super.onDestroy();
    }

    @Override
    public void handleResult(Result result) {
        RestConnection restConnection = new RestConnection(mContext);
        restConnection.getJsonData("qrtokens", result.getText(), new RestConnection.IGetRequestObserver() {
            @Override
            public void onRequestCompleted(int responseCode, JSONObject json) {
                if (responseCode == HttpURLConnection.HTTP_OK){
                    try {
                        mRoomId = json.getInt("roomId");
                        Log.d("OK", String.valueOf(mRoomId));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Intent intent = new Intent(mContext, RoomViewActivity.class);
        intent.putExtra("roomId", String.valueOf(mRoomId));
        startActivity(intent);
    }
}
