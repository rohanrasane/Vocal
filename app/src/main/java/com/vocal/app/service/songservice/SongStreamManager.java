package com.vocal.app.service.songservice;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.vocal.app.common.Events;
import com.vocal.app.MyApplication;
import com.vocal.app.R;
import com.vocal.app.data.Mixes;
import com.vocal.app.data.MyCollection;
import com.vocal.app.data.Play;
import com.vocal.app.data.PlayToken;
import com.vocal.app.data.Set;
import com.vocal.app.data.Track;

import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by rrasane on 7/31/14.
 */
public class SongStreamManager {

    List<Mixes> mixesList;
    private static String playToken ;
    RestAdapter restAdapter;

    public SongStreamManager(){
        restAdapter = getRestAdaptor();
        playToken = "";
        mixesList =null;
    }

    RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("User-Agent", "VocalApp");
            request.addHeader("Accept", "application/json");

            request.addHeader("X-Api-Key", MyApplication.getContext().getString(R.string.retrofit_api_key));
            request.addHeader("Content-Type", "application/json");
        }
    };

    private RestAdapter getRestAdaptor(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MyApplication.getContext().getString(R.string.api_url))
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter;
    }


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

    Callback<Play> trackDetailsCallBack = new Callback<Play>() {
        @Override
        public void success(Play o, Response response) {
            Set s = o.getSet();
            Track track = s.getTrack();

            if(null != track) {
                UpdateUI(Events.PUSH_TRACK_DETAILS, track.getName(), track.getUrl());
            }
            else{
                UpdateUI(Events.PASS_ERROR, MyApplication.getContext().getString(R.string.err_music_streaming),"");
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            UpdateUI(Events.PASS_ERROR, MyApplication.getContext().getString(R.string.err_music_streaming),"");
        }
    };

    Callback<PlayToken> getPlayTokenCallback = new Callback<PlayToken>() {
        @Override
        public void success(PlayToken o, Response response) {
            playToken = o.getPlay_token();
            if(null != restAdapter) {
                UpdateUI(Events.SHOW_PROGRESS,"Yes","");
                EightTracksPlaySong eTracks = restAdapter.create(EightTracksPlaySong.class);
                eTracks.contributors("json", playToken, mixesList.get(0).getId().toString(), trackDetailsCallBack);
            }
            else{
                UpdateUI(Events.PASS_ERROR,MyApplication.getContext().getString(R.string.err_network_provider),"");
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            UpdateUI(Events.PASS_ERROR, MyApplication.getContext().getString(R.string.err_music_streaming),"");
        }
    };

    Callback<MyCollection> getMixesFromSearchFieldCallback = new Callback<MyCollection>() {
        @Override
        public void success(MyCollection o, Response response) {
            mixesList =  o.getMixes();
            if(mixesList == null || mixesList.size() < 1){
                UpdateUI(Events.PASS_ERROR,MyApplication.getContext().getString(R.string.err_no_playlists_found),"");

            }
            else {
                Mixes firstItem = mixesList.get(0);
                UpdateUI(Events.UPDATE_PLAYLIST,firstItem.getName(),null);
                if(null != restAdapter) {
                    UpdateUI(Events.SHOW_PROGRESS,"Show","");
                    EightTracksGetPlayToken eTracks = restAdapter.create(EightTracksGetPlayToken.class);
                    eTracks.contributors("json", getPlayTokenCallback);
                }
                else{
                    UpdateUI(Events.PASS_ERROR,MyApplication.getContext().getString(R.string.err_network_provider),"");
                }
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            UpdateUI(Events.PASS_ERROR, MyApplication.getContext().getString(R.string.err_music_streaming),"");
        }
    };

    public void SearchPlayList(String key){
        if(null != restAdapter) {
            EightTracksGetMixes eTracks = restAdapter.create(EightTracksGetMixes.class);
            eTracks.contributors("json", "mixes", key, getMixesFromSearchFieldCallback);
        }
        else{
            UpdateUI(Events.PASS_ERROR,MyApplication.getContext().getString(R.string.err_network_provider),"");
        }
    }

    public void PlayNextTrack(){
        if(null != mixesList && null != playToken) {
            if(null != restAdapter) {
                EightTracksNextSong eTracks = restAdapter.create(EightTracksNextSong.class);
                eTracks.contributors("json", playToken, mixesList.get(0).getId().toString(), trackDetailsCallBack);
            }
            else{
                UpdateUI(Events.PASS_ERROR,MyApplication.getContext().getString(R.string.err_network_provider),"");
            }
        }

    }
}
