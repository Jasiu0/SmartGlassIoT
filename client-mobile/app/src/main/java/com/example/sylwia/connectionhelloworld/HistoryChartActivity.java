package com.example.sylwia.connectionhelloworld;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.sylwia.connectionhelloworld.connection.RestConnection;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class HistoryChartActivity extends AppCompatActivity {
    private Context mContext;
    private String mRoomId;
    private JSONObject mHistoryJson;

    private ArrayList<Double> gasHistoryTimeStamp = new ArrayList<Double>();
    private ArrayList<Boolean> gasHistoryValue = new ArrayList<Boolean>();
    private ArrayList<Double> lightHistoryTimeStamp = new ArrayList<Double>();
    private ArrayList<Boolean> lightHistoryValue = new ArrayList<Boolean>();
    private ArrayList<Double> noiseHistoryTimeStamp = new ArrayList<Double>();
    private ArrayList<Boolean> noiseHistoryValue = new ArrayList<Boolean>();
    private ArrayList<Double> movementHistoryTimeStamp = new ArrayList<Double>();
    private ArrayList<Boolean> movementHistoryValue = new ArrayList<Boolean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_chart);


        mContext = getApplicationContext();

        Intent intent = getIntent();
        mRoomId = intent.getStringExtra("roomId");
        Log.d("Ok", mRoomId);

        RestConnection restConnection = new RestConnection(mContext);
        restConnection.getJsonData("history/" + mRoomId, "0:999999999999999999", new RestConnection.IGetRequestObserver() {
            @Override
            public void onRequestCompleted(int responseCode, JSONObject json) {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    mHistoryJson = json;
                    try {
                        JSONObject jsonObject = mHistoryJson.getJSONObject("roomHistory");
                        JSONArray gasJsonArray = jsonObject.getJSONArray("gas");
                        JSONArray noiseJsonArray = jsonObject.getJSONArray("noise");
                        JSONArray lightJsonArray = jsonObject.getJSONArray("light");
                        JSONArray movementJsonArray = jsonObject.getJSONArray("movement");
                        jsonArrayToList(gasJsonArray, gasHistoryTimeStamp, gasHistoryValue);
                        jsonArrayToList(noiseJsonArray, lightHistoryTimeStamp, lightHistoryValue);
                        jsonArrayToList(lightJsonArray, noiseHistoryTimeStamp, noiseHistoryValue);
                        jsonArrayToList(movementJsonArray, movementHistoryTimeStamp, movementHistoryValue);


                        LineChart chart = (LineChart) findViewById(R.id.chart);

                        List<Entry> gasEntries = new ArrayList<Entry>();
                        List<Entry> lightEntries = new ArrayList<Entry>();
                        List<Entry> noiseEntries = new ArrayList<Entry>();
                        List<Entry> movementEntries = new ArrayList<Entry>();
        /*double x = 0;
        for (int i = 0; i < 1000; i++) {
            float sinFunction = Float.parseFloat(String.valueOf(Math.sin(x)));
            float cosFunction = Float.parseFloat(String.valueOf(Math.cos(x)));
            x = x + 0.01;
            gasEntries.add(new Entry(i, 0));
            lightEntries.add(new Entry(i * 2, cosFunction));
            noiseEntries.add(new Entry(i, sinFunction));
            movementEntries.add(new Entry(i, 1));
        }*/
                        double x = 0;
                        for(int i = 0; i < gasHistoryTimeStamp.size(); i++){
                            int temp = (gasHistoryValue.get(i)) ? 1 : 0;
                            float temp1 = Float.parseFloat(gasHistoryTimeStamp.get(i).toString());
                            gasEntries.add(new Entry( temp1, temp * 0.9f));

                            temp = (gasHistoryValue.get(i)) ? 0 : 1;
                            temp1 = Float.parseFloat(gasHistoryTimeStamp.get(i).toString());
                            gasEntries.add(new Entry( temp, temp1 * 0.9f));
                        }
                        for(int i = 0; i < lightHistoryTimeStamp.size(); i++){
                            int temp = (lightHistoryValue.get(i)) ? 1 : 0;
                            float temp1 = Float.parseFloat((lightHistoryTimeStamp.get(i)).toString());
                            lightEntries.add(new Entry(temp1, temp * 1.1f));

                            temp = (lightHistoryValue.get(i)) ? 0 : 1;
                            temp1 = Float.parseFloat((lightHistoryTimeStamp.get(i)).toString());
                            lightEntries.add(new Entry(temp1, temp * 1.1f));
                        }
                        for(int i = 0; i < noiseHistoryTimeStamp.size(); i++){
                            int temp = (noiseHistoryValue.get(i)) ? 1 : 0;
                            noiseEntries.add(new Entry(Float.parseFloat(noiseHistoryTimeStamp.get(i).toString()), temp * 1.2f));

                            temp = (noiseHistoryValue.get(i)) ? 0 : 1;
                            noiseEntries.add(new Entry(Float.parseFloat(noiseHistoryTimeStamp.get(i).toString()), temp * 1.2f));
                        }
                        for(int i = 0; i < movementHistoryTimeStamp.size(); i++){
                            int temp = (movementHistoryValue.get(i)) ? 1 : 0;
                            movementEntries.add(new Entry(Float.parseFloat(movementHistoryTimeStamp.get(i).toString()), temp * 0.8f));

                            temp = (movementHistoryValue.get(i)) ? 0 : 1;
                            movementEntries.add(new Entry(Float.parseFloat(movementHistoryTimeStamp.get(i).toString()), temp * 0.8f));
                        }



                        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

                        LineDataSet gasDataSet = new LineDataSet(gasEntries, "Gas");
                        gasDataSet.setDrawCircles(false);
                        gasDataSet.setColor(Color.BLUE);
                        gasDataSet.setDrawValues(false);

                        LineDataSet lightDataSet = new LineDataSet(lightEntries, "Light");
                        lightDataSet.setDrawCircles(false);
                        lightDataSet.setColor(Color.RED);
                        lightDataSet.setDrawValues(false);

                        LineDataSet noiseDataSet = new LineDataSet(noiseEntries, "Noise");
                        noiseDataSet.setDrawCircles(false);
                        noiseDataSet.setDrawValues(false);
                        noiseDataSet.setColor(Color.GREEN);

                        LineDataSet movementDataSet = new LineDataSet(movementEntries, "Movement");
                        movementDataSet.setDrawCircles(false);
                        movementDataSet.setDrawValues(false);
                        movementDataSet.setColor(Color.BLACK);

                        if(gasHistoryTimeStamp.size() != 0)
                        lineDataSets.add(gasDataSet);
                        if(lightHistoryTimeStamp.size() != 0)
                        lineDataSets.add(lightDataSet);
                        if(noiseHistoryTimeStamp.size() != 0)
                        lineDataSets.add(noiseDataSet);
                        if(movementHistoryTimeStamp.size() != 0)
                        lineDataSets.add(movementDataSet);

                        LineData data = new LineData(lineDataSets);
                        chart.getXAxis().setDrawLabels(false);
                        chart.getAxisLeft().setDrawLabels(false);
                        chart.getAxisRight().setDrawLabels(false);
                        chart.setData(new LineData(lineDataSets));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });



    }

    private void jsonArrayToList(JSONArray jsonArray, ArrayList<Double> historyTimeStamp, ArrayList<Boolean> historyValue) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                historyTimeStamp.add(jsonArray.getJSONObject(i).getDouble("timestamp") - 1485010000);
                historyValue.add(jsonArray.getJSONObject(i).getBoolean("value"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
