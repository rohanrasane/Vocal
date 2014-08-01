package com.vocal.app.service.speechservice;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.vocal.app.common.Events;
import com.vocal.app.MyApplication;
import com.vocal.app.R;

import java.util.ArrayList;

/**
 * Created by rrasane on 7/31/14.
 */
public class VocalSpeechRecognizer implements RecognitionListener
{
    private static final String TAG = MyApplication.getContext().getString(R.string.app_name);
    public void onReadyForSpeech(Bundle params)
    {
        Log.d(TAG, "onReadyForSpeech");
    }
    public void onBeginningOfSpeech()
    {
        Log.d(TAG, "onBeginningOfSpeech");
    }
    public void onRmsChanged(float rmsdB)
    {
        Log.d(TAG, "onRmsChanged");
    }
    public void onBufferReceived(byte[] buffer)
    {
        Log.d(TAG, "onBufferReceived");
    }
    public void onEndOfSpeech()
    {
        Log.d(TAG, "onEndofSpeech");
    }
    public void onError(int error)
    {
        Log.d(TAG, "error " + error);
        UpdateUI(Events.PASS_ERROR, MyApplication.getContext().getString(error), "");
    }
    public void onResults(Bundle results)
    {
        String str = new String();
        Log.d(TAG, "onResults " + results);
        ArrayList data = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < data.size(); i++)
        {
            Log.d(TAG, "result " + data.get(i));
            str += data.get(i);
        }

        String voiceData = data.get(0).toString();
        UpdateUI(Events.UPDATE_VOICE_DATA, voiceData, null);
    }
    public void onPartialResults(Bundle partialResults)
    {
        Log.d(TAG, "onPartialResults");
    }
    public void onEvent(int eventType, Bundle params)
    {
        Log.d(TAG, "onEvent " + eventType);
    }


    void UpdateUI(int event,String message,String extraData){
        Intent intent = new Intent("ui-events");
        // add data
        intent.putExtra("eventType", event);
        if(null != message && message.trim().length()>0 ) {
            intent.putExtra("extraData1", message);
        }
        if(null != extraData && extraData.trim().length()>0) {
            intent.putExtra("extraData2", extraData);
        }
        LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);

    }
}
