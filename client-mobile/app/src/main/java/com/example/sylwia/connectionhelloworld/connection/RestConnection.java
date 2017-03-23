package com.example.sylwia.connectionhelloworld.connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RestConnection {
    private String mBaseUrlString;

    public RestConnection(Context context){
        SharedPreferences preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String serverIp = preferences.getString("ServerIP", "192.168.0.101");

        mBaseUrlString = String.format("http://%s:5000/api/", serverIp);
        Log.d("RestConnection", mBaseUrlString);
    }

    public void getJsonData(final String endpoint, final String tokenId, final IGetRequestObserver observer){
        AsyncTask<Void, Void, Void> getTask = new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;
            int responseCode = HttpURLConnection.HTTP_NOT_FOUND;

            @Override
            protected Void doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                try {
                    String url = "";
                    if (tokenId != null){
                        url = mBaseUrlString + endpoint + "/" + tokenId;
                        Log.d("OK", url);
                    } else {
                        url = mBaseUrlString;
                    }
                    Request request = new Request.Builder()
                            .url(url)
                            .get()
                            .build();
                    Response response = client.newCall(request).execute();
                    jsonObject = new JSONObject(response.body().string());
                    responseCode = response.code();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                observer.onRequestCompleted(responseCode, jsonObject);
                Log.d("OK", "SUCCES");
            }
        };
        getTask.execute();
    }

    public void postDeviceData(final String endpoint, final String tokenId, final JSONObject jsonObject){
        Thread postRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String json = jsonObject.toString();

                OkHttpClient client = new OkHttpClient();
                try {
                    RequestBody body = RequestBody.create(JSON, json);
                    Request request = new Request.Builder()
                            .url(mBaseUrlString + endpoint + "/" + tokenId)
                            .post(body)
                            .build();
                    client.newCall(request).execute();
                    Log.d("OK", json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        postRequestThread.start();
    }

    public interface IGetRequestObserver{
        void onRequestCompleted(int responseCode, JSONObject json);
    }
}
