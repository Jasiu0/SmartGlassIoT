//package com.example.sylwia.connectionhelloworld.voice;
//
//import android.content.Context;
//import android.util.Log;
//import android.util.Pair;
//
//import com.example.sylwia.connectionhelloworld.connection.RestConnection;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.net.HttpURLConnection;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by Jurek on 2016-06-04.
// */
//public class Grammar {
//    private static final int TEXT_CONTENT = 100;
//    private static final int NUMBER_CONTENT = 101;
//
//    private static final String TAG = Grammar.class.getSimpleName();
//    private Context mContext;
//
//    private Map<String, Pair<String, Object>> mCommands;
//    private Map<String, Integer> mIds;
//
//    private String mMessage;
//    private IResultObserver mResultObserver;
//
//    public Grammar(Context context){
//        this.mContext = context;
//
//        mCommands = new HashMap<>();
//        mIds = new HashMap<>();
//
//        updateIdList(false);
//        createDefaultCommands();
//    }
//
//    public void decodeMessage(String message, IResultObserver observer){
//        mMessage = message.toLowerCase();
//        mResultObserver = observer;
//        updateIdList(true);
//    }
//
//    public interface IResultObserver {
//        void onResultsReady(JSONObject deviceJson);
//    }
//
//    private void createDefaultCommands(){
//        mCommands.put("włącz", new Pair<String, Object>("state", true));
//        mCommands.put("wyłącz", new Pair<String, Object>("state", false));
//        mCommands.put("zmień nazwę na", new Pair<String, Object>("name", TEXT_CONTENT));
//        mCommands.put("zmień opis na", new Pair<String, Object>("description", TEXT_CONTENT));
//        mCommands.put("ustaw czas na", new Pair<String, Object>("time", NUMBER_CONTENT));
//    }
//
//    private void updateIdList(final boolean gotMessage) {
//        mIds.clear();
//
//        RestConnection connection = new RestConnection(mContext);
//        connection.getJsonData(null, new RestConnection.IGetRequestObserver() {
//            @Override
//            public void onRequestCompleted(int responseCode, JSONObject json) {
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    try {
//                        Map<Integer, JSONObject> devicesMap = new HashMap<>();
//                        JSONArray devicesJson = json.getJSONArray("devices");
//                        for (int i = 0; i < devicesJson.length(); i++) {
//                            String name = devicesJson.getJSONObject(i).getString("name").toLowerCase();
//                            int id = devicesJson.getJSONObject(i).getInt("id");
//                            if (!name.isEmpty()){
//                                mIds.put(name.substring(0, name.length() - 1), id);
//                            }
//                            if (gotMessage){
//                                devicesMap.put(id, devicesJson.getJSONObject(i));
//                            }
//                        }
//                        if (gotMessage){
//                            decodeMessageOnListReady(devicesMap);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
//    private void decodeMessageOnListReady(Map<Integer, JSONObject> devicesMap){
//        JSONObject jsonObject = null;
//        Result result = new Result();
//
//        for (String key : mIds.keySet()){
//            if (mMessage.contains(key)){
//                result.id = mIds.get(key);
//                jsonObject = devicesMap.get(result.id);
//                break;
//            }
//        }
//
//        if (result.id == null){
//            return;
//        }
//
//        String commandFound = "";
//        for (String key : mCommands.keySet()){
//            if (mMessage.contains(key)){
//                commandFound = key;
//                result.propertyKey = mCommands.get(key).first;
//                result.propertyValue = mCommands.get(key).second;
//                break;
//            }
//        }
//
//        if (result.propertyKey == null || result.propertyValue == null){
//            return;
//        }
//
//        try {
//            if ((Integer)result.propertyValue == TEXT_CONTENT){
//                int commandContentIndex = mMessage.indexOf(commandFound) + commandFound.length() + 1;
//                result.propertyValue = mMessage.substring(commandContentIndex);
//            } else if ((Integer)result.propertyValue == NUMBER_CONTENT){
//                int commandContentIndex = mMessage.indexOf(commandFound) + commandFound.length() + 1;
//                result.propertyValue = Integer.parseInt(mMessage.substring(commandContentIndex));
//            }
//        } catch (ClassCastException e) {
//            Log.d(TAG, "Result already completed");
//        }
//
//        try {
//            jsonObject.put("id", result.id);
//            jsonObject.put(result.propertyKey, result.propertyValue);
//            mResultObserver.onResultsReady(jsonObject);
//            return;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        mResultObserver.onResultsReady(null);
//    }
//
//    private class Result {
//        public Integer id;
//        public String propertyKey;
//        public Object propertyValue;
//    }
//}
