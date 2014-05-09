package com.vocal.app.data;

import com.vocal.app.data.Avatar_urls;

/**
 * Created by rrasane on 5/9/14.
 */
public class User{
    private Avatar_urls avatar_urls;
    private boolean followed_by_current_user;
    private Number id;
    private String login;
    private String slug;

    public Avatar_urls getAvatar_urls(){
        return this.avatar_urls;
    }
    public void setAvatar_urls(Avatar_urls avatar_urls){
        this.avatar_urls = avatar_urls;
    }
    public boolean getFollowed_by_current_user(){
        return this.followed_by_current_user;
    }
    public void setFollowed_by_current_user(boolean followed_by_current_user){
        this.followed_by_current_user = followed_by_current_user;
    }
    public Number getId(){
        return this.id;
    }
    public void setId(Number id){
        this.id = id;
    }
    public String getLogin(){
        return this.login;
    }
    public void setLogin(String login){
        this.login = login;
    }
    public String getSlug(){
        return this.slug;
    }
    public void setSlug(String slug){
        this.slug = slug;
    }
}