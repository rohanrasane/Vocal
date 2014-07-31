package com.vocal.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.Random;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;


public class MainActivity extends Activity {



    private SpeechRecognizer sr;

    private static final String TAG = "RohanRasane";

    private static final String API_URL = "http://8tracks.com";

    private static final String API_KEY ="5fccdc1f40f2727b6e37ff3f3bb6439e0dcd9b9b";

    private static String playToken = "";

    List<Mixes> mixesList = null;

    MediaPlayer mMediaPlayer = null;

    Intent voiceIntent = null;

    ProgressDialog mDialog = null;

    Boolean isAddressSelected = false;

    int mediaPlayerPausedPosition = 0;

    private void ShowError(String message){
        Toast t = Toast.makeText(getApplicationContext(),
                "Error " + message,
                Toast.LENGTH_SHORT);
        t.show();
    }

    RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("User-Agent", "VocalApp");
            request.addHeader("Accept", "application/json");
            request.addHeader("X-Api-Key", API_KEY);
            request.addHeader("Content-Type", "application/json");
        }
    };

    private RestAdapter getRestAdaptor(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter;
    }

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

        if (null != voiceIntent && voiceIntent.resolveActivity(getPackageManager()) != null) {
            voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            voiceIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "rohan.rasane");
            voiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        }
        else{
            ShowError(getString(R.string.err_speech_maintainance));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
    }

    Callback<PlayToken> callbackGetPlayToken = new Callback<PlayToken>() {
        @Override
        public void success(PlayToken o, Response response) {
            playToken = o.getPlay_token();
            RestAdapter restAdapter = getRestAdaptor();
            if(null != restAdapter) {

                EightTracksPlaySong eTracks = restAdapter.create(EightTracksPlaySong.class);
                eTracks.contributors("json", playToken, mixesList.get(0).getId().toString(), simpleCallback);
            }
            else{
                ShowError(getString(R.string.err_network_provider));
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mDialog.dismiss();
            ShowError(getString(R.string.err_music_streaming));
        }
    };

    Callback<Play> simpleCallback = new Callback<Play>() {
        @Override
        public void success(Play o, Response response) {
            Set s = o.getSet();
            Track t = s.getTrack();
            TextView myTextView = (TextView)findViewById(R.id.textViewSong);
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
            ImageButton btn=(ImageButton) findViewById(R.id.ImageButtonPlayPause);
            btn.setImageResource(R.drawable.pause);
            ImageView imgView = (ImageView)findViewById(R.id.ImageViewMusicBox);
            imgView.setVisibility(View.VISIBLE);
            ImageView imgView1 = (ImageView)findViewById(R.id.ImageViewDirectionsBox);
            imgView1.setVisibility(View.INVISIBLE);

        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mDialog.dismiss();
            ShowError(getString(R.string.err_music_streaming));
        }
    };



    Callback<MyCollection> getMixesFromSearchFieldCallback = new Callback<MyCollection>() {
        @Override
        public void success(MyCollection o, Response response) {
            mixesList =  o.getMixes();
            if(mixesList == null || mixesList.size() < 1){
                mDialog.dismiss();
                ShowError(getString(R.string.err_no_playlists_found));
            }
            else {
                Mixes firstItem = mixesList.get(0);
                String res = firstItem.getName();
                TextView myTextView = (TextView) findViewById(R.id.textViewAlbum);
                myTextView.setText(res);

                RestAdapter restAdapter = getRestAdaptor();
                if(null != restAdapter) {

                    EightTracksGetPlayToken eTracks = restAdapter.create(EightTracksGetPlayToken.class);
                    eTracks.contributors("json", callbackGetPlayToken);
                }
                else{
                    ShowError(getString(R.string.err_network_provider));
                }
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mDialog.dismiss();
            ShowError(getString(R.string.err_music_streaming));
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
            Log.d(TAG, "error " + error);
            ShowError(getString(error));
            mDialog.dismiss();
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
            Search(voiceData);
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
        if(null != mixesList && null != playToken) {
            RestAdapter restAdapter = getRestAdaptor();
            if(null != restAdapter) {
                EightTracksNextSong eTracks = restAdapter.create(EightTracksNextSong.class);
                eTracks.contributors("json", playToken, mixesList.get(0).getId().toString(), simpleCallback);
            }
            else{
                ShowError(getString(R.string.err_network_provider));
            }
        }

    }

    public void PlaySearchedMusic(String strKeyword){
        RestAdapter restAdapter = getRestAdaptor();
        if(null != restAdapter) {
            EightTracksGetMixes eTracks = restAdapter.create(EightTracksGetMixes.class);
            eTracks.contributors("json", "mixes", strKeyword, getMixesFromSearchFieldCallback);
            mDialog.setMessage("Loading...");
            mDialog.show();
        }
        else{
            ShowError(getString(R.string.err_network_provider));
        }
    }

    public void HandleNextSongClick(View view){
        PlayNextSong();
    }

    public void HandlePauseOnClick(View view) {
        if(null != mMediaPlayer) {
            ImageButton btn=(ImageButton) findViewById(R.id.ImageButtonPlayPause);
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mediaPlayerPausedPosition=mMediaPlayer.getCurrentPosition();

                btn.setImageResource(R.drawable.play);
            } else {
                mMediaPlayer.seekTo(mediaPlayerPausedPosition);
                mMediaPlayer.start();
                btn.setImageResource(R.drawable.pause);
            }
            ImageView imgView = (ImageView)findViewById(R.id.ImageViewMusicBox);
            imgView.setVisibility(View.VISIBLE);
            ImageView imgView1 = (ImageView)findViewById(R.id.ImageViewDirectionsBox);
            imgView1.setVisibility(View.INVISIBLE);
        }
    }

    private void Search(String searchStr){
        if(true == isAddressSelected){
            ShowDirectionOnMap(searchStr);
        }
        else{
            PlaySearchedMusic(searchStr);
        }
    }

    public void HandleDirectionsOnClick(View view) {
        isAddressSelected = true;
        ImageView imgView = (ImageView)findViewById(R.id.ImageViewMusicBox);
        imgView.setVisibility(View.INVISIBLE);
        ImageView imgView1 = (ImageView)findViewById(R.id.ImageViewDirectionsBox);
        imgView1.setVisibility(View.VISIBLE);
    }

    public void HandleMusicOnClick(View view) {
        isAddressSelected = false;
        ImageView imgView = (ImageView)findViewById(R.id.ImageViewMusicBox);
        imgView.setVisibility(View.VISIBLE);
        ImageView imgView1 = (ImageView)findViewById(R.id.ImageViewDirectionsBox);
        imgView1.setVisibility(View.INVISIBLE);
    }

    public void HandleSearchOnClick(View view) {
        EditText myEditTextView = (EditText) findViewById(R.id.editText);

        String searchText = myEditTextView.getText().toString();
        if(searchText.equalsIgnoreCase("")){
            ShowError(getString(R.string.err_text_box_invalid_text));
        }
        else {
            Search(myEditTextView.getText().toString());
        }
    }

    public void HandleListenOnClick(View view) {
        EditText myEditTextView = (EditText) findViewById(R.id.editText);
        myEditTextView.setText("");
        if(null != sr && voiceIntent.resolveActivity(getPackageManager()) != null){
            sr.startListening(voiceIntent);
            mDialog.setMessage("Listening...");
            mDialog.show();
            Log.d(TAG, "Started Listening");
        }
        else{
            ShowError(getString(R.string.err_speech_maintainance));
        }
    }

    public int getContactIDFromNumber(String contactNumber)
    {
        Context ctx = getApplicationContext();
        contactNumber = Uri.encode(contactNumber);
        int phoneContactID = new Random().nextInt();
        Cursor contactLookupCursor = ctx.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,contactNumber),new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        while(contactLookupCursor.moveToNext()){
            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        contactLookupCursor.close();
        return phoneContactID;
    }


    public void HandleWhatsAppClick(View view) {

        EditText myTextView = (EditText)findViewById(R.id.editText);
        int contactID = getContactIDFromNumber(myTextView.getText().toString());
        Intent whatsAppIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("content://com.android.contacts/data/" + contactID));
                whatsAppIntent.setType("text/plain");
        if(null != whatsAppIntent &&  whatsAppIntent.resolveActivity(getPackageManager()) != null) {
            whatsAppIntent.setPackage("com.whatsapp");           // so that only Whatsapp reacts and not the chooser
            whatsAppIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            whatsAppIntent.putExtra(Intent.EXTRA_TEXT, "I'm the body.");
            startActivity(whatsAppIntent);
        }
        else{
            ShowError(getString(R.string.err_whatsapp_maintainance));
        }
    }

    public void ShowDirectionOnMap(String address){
        StringBuilder strAddress =new StringBuilder();
        strAddress.append("google.navigation:q=");
        strAddress.append(address);
        Intent mapsIntent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(strAddress.toString()));

        if (null != mapsIntent && mapsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapsIntent);
        }
        else{
            ShowError(getString(R.string.err_map_maintainance));
        }
    }
}
