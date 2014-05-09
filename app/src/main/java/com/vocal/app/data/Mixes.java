package com.vocal.app.data;

/**
 * Created by rrasane on 5/9/14.
 */
public class Mixes{
    private Cover_urls cover_urls;
    private String description;
    private Number duration;
    private String first_published_at;
    private Number id;
    private boolean liked_by_current_user;
    private Number likes_count;
    private String name;
    private boolean nsfw;
    private String path;
    private Number plays_count;
    private boolean published;
    private String restful_url;
    private String slug;
    private String tag_list_cache;
    private Number tracks_count;
    private User user;
    private String web_path;

    public Cover_urls getCover_urls(){
        return this.cover_urls;
    }
    public void setCover_urls(Cover_urls cover_urls){
        this.cover_urls = cover_urls;
    }
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public Number getDuration(){
        return this.duration;
    }
    public void setDuration(Number duration){
        this.duration = duration;
    }
    public String getFirst_published_at(){
        return this.first_published_at;
    }
    public void setFirst_published_at(String first_published_at){
        this.first_published_at = first_published_at;
    }
    public Number getId(){
        return this.id;
    }
    public void setId(Number id){
        this.id = id;
    }
    public boolean getLiked_by_current_user(){
        return this.liked_by_current_user;
    }
    public void setLiked_by_current_user(boolean liked_by_current_user){
        this.liked_by_current_user = liked_by_current_user;
    }
    public Number getLikes_count(){
        return this.likes_count;
    }
    public void setLikes_count(Number likes_count){
        this.likes_count = likes_count;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public boolean getNsfw(){
        return this.nsfw;
    }
    public void setNsfw(boolean nsfw){
        this.nsfw = nsfw;
    }
    public String getPath(){
        return this.path;
    }
    public void setPath(String path){
        this.path = path;
    }
    public Number getPlays_count(){
        return this.plays_count;
    }
    public void setPlays_count(Number plays_count){
        this.plays_count = plays_count;
    }
    public boolean getPublished(){
        return this.published;
    }
    public void setPublished(boolean published){
        this.published = published;
    }
    public String getRestful_url(){
        return this.restful_url;
    }
    public void setRestful_url(String restful_url){
        this.restful_url = restful_url;
    }
    public String getSlug(){
        return this.slug;
    }
    public void setSlug(String slug){
        this.slug = slug;
    }
    public String getTag_list_cache(){
        return this.tag_list_cache;
    }
    public void setTag_list_cache(String tag_list_cache){
        this.tag_list_cache = tag_list_cache;
    }
    public Number getTracks_count(){
        return this.tracks_count;
    }
    public void setTracks_count(Number tracks_count){
        this.tracks_count = tracks_count;
    }
    public User getUser(){
        return this.user;
    }
    public void setUser(User user){
        this.user = user;
    }
    public String getWeb_path(){
        return this.web_path;
    }
    public void setWeb_path(String web_path){
        this.web_path = web_path;
    }
}