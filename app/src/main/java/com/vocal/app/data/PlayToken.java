package com.vocal.app.data;

import java.util.List;

/**
 * Created by rrasane on 5/9/14.
 */
public class PlayToken{
    private Number api_version;
    private List api_warning;
    private String errors;
    private String notices;
    private String play_token;
    private String status;

    public Number getApi_version(){
        return this.api_version;
    }
    public void setApi_version(Number api_version){
        this.api_version = api_version;
    }
    public List getApi_warning(){
        return this.api_warning;
    }
    public void setApi_warning(List api_warning){
        this.api_warning = api_warning;
    }
    public String getErrors(){
        return this.errors;
    }
    public void setErrors(String errors){
        this.errors = errors;
    }
    public String getNotices(){
        return this.notices;
    }
    public void setNotices(String notices){
        this.notices = notices;
    }
    public String getPlay_token(){
        return this.play_token;
    }
    public void setPlay_token(String play_token){
        this.play_token = play_token;
    }
    public String getStatus(){
        return this.status;
    }
    public void setStatus(String status){
        this.status = status;
    }
}