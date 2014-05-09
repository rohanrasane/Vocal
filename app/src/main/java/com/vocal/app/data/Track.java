package com.vocal.app.data;

/**
 * Created by rrasane on 5/9/14.
 */
public class Track{
    private String buy_icon;
    private String buy_link;
    private boolean faved_by_current_user;
    private Number id;
    private String name;
    private String performer;
    private Number play_duration;
    private String release_name;
    private String stream_source;
    private String url;
    private String year;

    public String getBuy_icon(){
        return this.buy_icon;
    }
    public void setBuy_icon(String buy_icon){
        this.buy_icon = buy_icon;
    }
    public String getBuy_link(){
        return this.buy_link;
    }
    public void setBuy_link(String buy_link){
        this.buy_link = buy_link;
    }
    public boolean getFaved_by_current_user(){
        return this.faved_by_current_user;
    }
    public void setFaved_by_current_user(boolean faved_by_current_user){
        this.faved_by_current_user = faved_by_current_user;
    }
    public Number getId(){
        return this.id;
    }
    public void setId(Number id){
        this.id = id;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getPerformer(){
        return this.performer;
    }
    public void setPerformer(String performer){
        this.performer = performer;
    }
    public Number getPlay_duration(){
        return this.play_duration;
    }
    public void setPlay_duration(Number play_duration){
        this.play_duration = play_duration;
    }
    public String getRelease_name(){
        return this.release_name;
    }
    public void setRelease_name(String release_name){
        this.release_name = release_name;
    }
    public String getStream_source(){
        return this.stream_source;
    }
    public void setStream_source(String stream_source){
        this.stream_source = stream_source;
    }
    public String getUrl(){
        return this.url;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public String getYear(){
        return this.year;
    }
    public void setYear(String year){
        this.year = year;
    }
}