//package com.example.sylwia.connectionhelloworld.voice;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.speech.RecognitionListener;
//import android.speech.SpeechRecognizer;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.example.sylwia.connectionhelloworld.connection.RestConnection;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.List;
//
///**
// * Created by Jurek on 2016-06-04.
// */
//public class VoiceCommander {
//    private static final String TAG = VoiceCommander.class.getSimpleName();
//    private Context mContext;
//
//    private SpeechRecognizer mSpeechRecognizer;
//
//
//    private RecognitionListener mRecognitionListener = new RecognitionListener() {
//        @Override
//        public void onReadyForSpeech(Bundle params) {
//            Log.d("RECOGNITION LISTENER", "Ready!");
//
//        }
//
//        @Override
//        public void onBeginningOfSpeech() {
//            Log.d("RECOGNITION LISTENER", "Started!");
//        }
//
//        @Override
//        public void onRmsChanged(float rmsdB) {
//
//        }
//
//        @Override
//        public void onBufferReceived(byte[] buffer) {
//
//        }
//
//        @Override
//        public void onEndOfSpeech() {
//
//        }
//
//        @Override
//        public void onError(int error) {
//
//        }
//
//        @Override
//        public void onResults(Bundle results) {
//            String recognitionResult = "";
//            List<String> recognitionResults;
//            if ((recognitionResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)) != null){
//                recognitionResult = recognitionResults.get(0);
//            }
//            Log.d(TAG, "Recognition result: " + recognitionResult);
//
//            if (recognitionResult.isEmpty()){
//                Toast.makeText(mContext, "Could you repeat?", Toast.LENGTH_SHORT).show();
//            }
//
//            Grammar grammar = new Grammar(mContext);
//            grammar.decodeMessage(recognitionResult, new Grammar.IResultObserver() {
//                @Override
//                public void onResultsReady(JSONObject deviceJson) {
//                    if (deviceJson != null){
//                        try {
//                            RestConnection restConnection = new RestConnection(mContext);
//                            restConnection.postDeviceData(String.valueOf(deviceJson.getInt("id")), deviceJson);
//                            Toast.makeText(mContext, "Succeed!", Toast.LENGTH_SHORT).show();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        Toast.makeText(mContext, "Could you repeat?", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//        }
//
//        @Override
//        public void onPartialResults(Bundle partialResults) {
//
//        }
//
//        @Override
//        public void onEvent(int eventType, Bundle params) {
//
//        }
//    };
//
//    public VoiceCommander(Context context) {
//        this.mContext = context;
//
//        mSpeechRecognizer  = SpeechRecognizer.createSpeechRecognizer(mContext);
//        if (!SpeechRecognizer.isRecognitionAvailable(mContext)){
//            Toast.makeText(mContext, "Sppech recognition unavailable", Toast.LENGTH_SHORT).show();
//        }
//        mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
//    }
//
//    public void startListening(){
//        mSpeechRecognizer.startListening(new Intent());
//    }
//}
