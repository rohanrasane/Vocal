package com.vocal.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vocal.app.common.Events;
import com.vocal.app.service.songservice.SongStreamManager;
import com.vocal.app.service.speechservice.VocalSpeechRecognizer;

import java.io.IOException;
import java.util.Random;


public class MainActivity extends Activity {


    SpeechRecognizer speechRecognizer;

    MediaPlayer mMediaPlayer = null;

    Intent voiceIntent = null;

    ProgressDialog mDialog = null;

    Boolean isAddressSelected = false;

    int mediaPlayerPausedPosition = 0;

    SongStreamManager songStreamManager =null;

    private void ShowError(String message){
        Toast t = Toast.makeText(getApplicationContext(),
                "Error: " + message,
                Toast.LENGTH_SHORT);
        t.show();
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

        songStreamManager = new SongStreamManager();

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new VocalSpeechRecognizer());
        voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        if (null != voiceIntent && voiceIntent.resolveActivity(getPackageManager()) != null) {
            voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            voiceIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getString(R.string.app_name));
            voiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        }
        else{
            ShowError(getString(R.string.err_speech_maintainance));
        }
    }

    @Override
    protected void onStop(){
        if(null != speechRecognizer){
            speechRecognizer.cancel();
            speechRecognizer.destroy();
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


    @Override
    public void onResume() {
        super.onResume();

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(localMessageSubscriber,new IntentFilter("ui-events"));
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localMessageSubscriber);
        super.onPause();
    }

    private BroadcastReceiver localMessageSubscriber = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ShowProgressDialog(false,"");

            switch(intent.getIntExtra("eventType",0)){
                case Events.PASS_ERROR:
                    ShowError(intent.getStringExtra("extraData1"));
                    break;
                case Events.UPDATE_PLAYLIST:
                    UpdateTextView(R.id.textViewAlbum,intent.getStringExtra("extraData1"));
                    break;
                case Events.PUSH_TRACK_DETAILS:
                    songDetailsHandler(intent.getStringExtra("extraData1"),intent.getStringExtra("extraData2"));
                    break;
                case Events.SHOW_PROGRESS:
                    String strYes = "Yes";
                    if(strYes.equalsIgnoreCase(intent.getStringExtra("extraData1"))){
                        ShowProgressDialog(true,getString(R.string.progress_message_loading));
                    }
                    break;
                case Events.UPDATE_VOICE_DATA:
                    String voiceData = intent.getStringExtra("extraData1");
                    UpdateTextView(R.id.editText,voiceData);
                    Search(voiceData);
            }
        }
    };


    void ShowProgressDialog(boolean show, String message){
        if(true == show){
            mDialog.setMessage(message);
            mDialog.show();
        }
        else{
            mDialog.dismiss();
        }
    }

    void UpdateTextView(int id,String name){
        TextView myTextView = (TextView) findViewById(id);
        myTextView.setText(name);
    }

    void songDetailsHandler(String trackName, String trackURL){
        TextView myTextView = (TextView)findViewById(R.id.textViewSong);
        myTextView.setText(trackName);

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
            mMediaPlayer.setDataSource(trackURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mMediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        ImageButton btn=(ImageButton) findViewById(R.id.ImageButtonPlayPause);
        btn.setImageResource(R.drawable.pause);
        ImageView imgView = (ImageView)findViewById(R.id.ImageViewMusicBox);
        imgView.setVisibility(View.VISIBLE);
        ImageView imgView1 = (ImageView)findViewById(R.id.ImageViewDirectionsBox);
        imgView1.setVisibility(View.INVISIBLE);
    }

    MediaPlayer.OnCompletionListener onSongCompleteCallback = new MediaPlayer.OnCompletionListener(){
        @Override
        public void onCompletion(MediaPlayer arg0) {
            PlayNextSong();
        }
    };


    public void PlayNextSong(){
        ShowProgressDialog(true, getString(R.string.progress_message_loading));
        songStreamManager.PlayNextTrack();
    }

    public void PlaySearchedMusic(String strKeyword){
        ShowProgressDialog(true, getString(R.string.progress_message_loading));
        songStreamManager.SearchPlayList(strKeyword);
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
        if(null != speechRecognizer && null != voiceIntent.resolveActivity(getPackageManager())){
            speechRecognizer.startListening(voiceIntent);
            ShowProgressDialog(true, getString(R.string.progress_message_listening));
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
        if(null != whatsAppIntent &&  null != whatsAppIntent.resolveActivity(getPackageManager())) {
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
        strAddress.append(getString(R.string.google_map_query));
        strAddress.append(address);
        Intent mapsIntent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(strAddress.toString()));

        if (null != mapsIntent && null != mapsIntent.resolveActivity(getPackageManager())) {
            startActivity(mapsIntent);
        }
        else{
            ShowError(getString(R.string.err_map_maintainance));
        }
    }
}
