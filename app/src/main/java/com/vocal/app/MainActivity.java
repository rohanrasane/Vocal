package com.vocal.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vocal.app.data.Mixes;
import com.vocal.app.data.MyCollection;
import com.vocal.app.data.Play;
import com.vocal.app.data.PlayToken;
import com.vocal.app.data.Set;
import com.vocal.app.data.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;


public class MainActivity extends Activity {


    private TextView txtText =null;

    private SpeechRecognizer sr;

    private static final String TAG = "RohanRasane";

    private static final String API_URL = "http://8tracks.com";

    private static final String API_KEY ="5fccdc1f40f2727b6e37ff3f3bb6439e0dcd9b9b";

    private static String playToken = "";

    List<Mixes> mixesList = null;

    MediaPlayer mMediaPlayer = null;

    Intent voiceIntent = null;

    ProgressDialog mDialog = null;


    Boolean isSongVoice = true;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart(){
        super.onStart();
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());
        voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        if (voiceIntent.resolveActivity(getPackageManager()) != null) {
            voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            voiceIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "rohan.rasane");
            voiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        }
        else{
            Toast t = Toast.makeText(getApplicationContext(),
                    "Voice Service is under maintainance ",
                    Toast.LENGTH_SHORT);
            t.show();
        }
    }

    @Override
    protected void onStop(){
        if(null != sr){
            sr.cancel();
            sr.destroy();
        }
        super.onDestroy();
    }

    Callback<PlayToken> callbackGetPlayToken = new Callback<PlayToken>() {
        @Override
        public void success(PlayToken o, Response response) {

            playToken = o.getPlay_token();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API_URL)
                    .setRequestInterceptor(requestInterceptor)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();

            EightTracksPlaySong eTracks = restAdapter.create(EightTracksPlaySong.class);
            eTracks.contributors("json",playToken,mixesList.get(0).getId().toString(),simpleCallback);


        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mDialog.dismiss();
        }
    };

    Callback<Play> simpleCallback = new Callback<Play>() {
        @Override
        public void success(Play o, Response response) {
            Set s = o.getSet();
            Track t = s.getTrack();
            EditText myTextView = (EditText)findViewById(R.id.edittext3);
            myTextView.setText(t.getName());

            String url = t.getUrl(); // your URL here
            if(null != mMediaPlayer){
                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                }
            }
            else{
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnCompletionListener(onSongCompleteCallback);
            }
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mMediaPlayer.prepare(); // might take long! (for buffering, etc)
            } catch (IOException e) {
                e.printStackTrace();
            }
            mDialog.dismiss();
            mMediaPlayer.start();



        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mDialog.dismiss();
        }
    };



    Callback<MyCollection> getMixesFromSearchFieldCallback = new Callback<MyCollection>() {
        @Override
        public void success(MyCollection o, Response response) {

            mixesList =  o.getMixes();

            if(mixesList == null || mixesList.size() < 1){
                mDialog.dismiss();
                Toast t = Toast.makeText(getApplicationContext(),
                        "Sorry, no songs found, please try with a new keyword",
                        Toast.LENGTH_SHORT);
                t.show();
            }
            else {
                Mixes firstItem = mixesList.get(0);


                String res = firstItem.getName();

                EditText myTextView = (EditText) findViewById(R.id.edittext2);
                myTextView.setText(res);


                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(API_URL)
                        .setRequestInterceptor(requestInterceptor)
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .build();


                EightTracksGetPlayToken eTracks = restAdapter.create(EightTracksGetPlayToken.class);
                eTracks.contributors("json", callbackGetPlayToken);

            }

        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mDialog.dismiss();
        }
    };


    RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("User-Agent", "VocalApp");
            request.addHeader("Accept", "application/json");
            request.addHeader("X-Api-Key", API_KEY);
            request.addHeader("Content-Type", "application/json");
        }
    };

    interface EightTracksGetMixes {
        @GET("/mix_sets/tags:{tag}")
        void contributors(@Query("format") String format,@Query("include")String include, @Path("tag")String tag, Callback<MyCollection> cb);
    }

    interface EightTracksGetPlayToken {
        @GET("/sets/new")
        void contributors(@Query("format") String format, Callback<PlayToken> cb);
    }

    interface EightTracksPlaySong {
        @GET("/sets/{playToken}/play")
        void contributors(@Query("format") String format,@Path("playToken")String playToken, @Query("mix_id") String id,Callback<Play> cb);
    }

    interface EightTracksNextSong {
        @GET("/sets/{playToken}/next")
        void contributors(@Query("format") String format,@Path("playToken")String playToken, @Query("mix_id") String id,Callback<Play> cb);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtText = (TextView) findViewById(R.id.textView1);
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
    }

    class listener implements RecognitionListener
    {
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
            Log.d(TAG,  "error " +  error);
            txtText.setText("error " + error);
            mDialog.dismiss();
            isSongVoice = true;
        }
        public void onResults(Bundle results)
        {
            mDialog.dismiss();
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i);
            }

            String voiceData = data.get(0).toString();
            EditText myEditTextView = (EditText) findViewById(R.id.editText);
            myEditTextView.setText(voiceData);
            if(false == isSongVoice){
                ShowDirectionOnMap(voiceData);
                isSongVoice = true;
                txtText.setText("None :)");
            }
            else {

                PlaySearchedMusic(voiceData);
                txtText.setText("None :)");
            }
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    MediaPlayer.OnCompletionListener onSongCompleteCallback
            = new MediaPlayer.OnCompletionListener(){

        @Override
        public void onCompletion(MediaPlayer arg0) {
            PlayNextSong();
        }};


    public void PlayNextSong(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        EightTracksNextSong eTracks = restAdapter.create(EightTracksNextSong.class);
        eTracks.contributors("json",playToken,mixesList.get(0).getId().toString(),simpleCallback);

    }

    public void HandleVoiceToTextClick(View view) {
        if(null != sr && voiceIntent.resolveActivity(getPackageManager()) != null){
            sr.startListening(voiceIntent);
            mDialog.setMessage("Listening...");
            mDialog.show();
            Log.d(TAG, "Started Listening");
        }
        else{
            Toast t = Toast.makeText(getApplicationContext(),
                    "Speech Service is under maintainance ",
                    Toast.LENGTH_SHORT);
            t.show();
        }
    }


    public void PlaySearchedMusic(String strKeyword){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        EightTracksGetMixes eTracks = restAdapter.create(EightTracksGetMixes.class);
        eTracks.contributors("json","mixes",strKeyword,getMixesFromSearchFieldCallback);
        mDialog.setMessage("Loading...");
        mDialog.show();
    }

    public void HandleSearchClick(View view) {
        EditText myEditTextView = (EditText)findViewById(R.id.editText);
        String searchField = myEditTextView.getText().toString();
        PlaySearchedMusic(searchField);
    }


    public void HandleNextSongClick(View view) {
        PlayNextSong();
    }

    public void HandleMapsClick(View view) {

        if(null != sr && voiceIntent.resolveActivity(getPackageManager()) != null){
            isSongVoice = false;
            sr.startListening(voiceIntent);
            mDialog.setMessage("Listening...");
            mDialog.show();
            Log.d(TAG, "Started Listening");
        }
        else{
            Toast t = Toast.makeText(getApplicationContext(),
                    "Speech Service is under maintainance ",
                    Toast.LENGTH_SHORT);
            t.show();
        }
    }

    public void HandleMapsClickSearchText(View view) {

        EditText myTextView = (EditText)findViewById(R.id.editText);
        ShowDirectionOnMap(myTextView.getText().toString());
    }

    public void HandleWhatsAppClick(View view) {

        EditText myTextView = (EditText)findViewById(R.id.editText);

        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("content://com.android.contacts/data/" + myTextView.getText()));
        i.setType("text/plain");
        i.setPackage("com.whatsapp");           // so that only Whatsapp reacts and not the chooser
        i.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        i.putExtra(Intent.EXTRA_TEXT, "I'm the body.");
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivity(i);
        }
        else{
            Toast t = Toast.makeText(getApplicationContext(),
                    "Whatsapp Service is under maintainance ",
                    Toast.LENGTH_SHORT);
            t.show();
        }
    }



    public void ShowDirectionOnMap(String address){
        StringBuilder strAddress =new StringBuilder();
        strAddress.append("google.navigation:q=");
        strAddress.append(address);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(strAddress.toString()));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else{
            Toast t = Toast.makeText(getApplicationContext(),
                    "Maps Service is under maintainance ",
                    Toast.LENGTH_SHORT);
            t.show();
        }
    }
}
