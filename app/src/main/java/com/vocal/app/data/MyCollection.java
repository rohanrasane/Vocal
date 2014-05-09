package com.vocal.app.data;

import java.util.List;

/**
 * Created by rrasane on 5/9/14.
 */
public class MyCollection{
    private Number api_version;
    private List api_warning;
    private String canonical_path;
    private String errors;
    private Number id;
    private boolean logged_in;
    private List<Mixes> mixes;
    private String name;
    private Number next_page;
    private String notices;
    private Number page;
    private String path;
    private Number per_page;
    private String previous_page;
    private String restful_url;
    private String smart_id;
    private String status;
    private Number total_entries;
    private Number total_pages;

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
    public String getCanonical_path(){
        return this.canonical_path;
    }
    public void setCanonical_path(String canonical_path){
        this.canonical_path = canonical_path;
    }
    public String getErrors(){
        return this.errors;
    }
    public void setErrors(String errors){
        this.errors = errors;
    }
    public Number getId(){
        return this.id;
    }
    public void setId(Number id){
        this.id = id;
    }
    public boolean getLogged_in(){
        return this.logged_in;
    }
    public void setLogged_in(boolean logged_in){
        this.logged_in = logged_in;
    }
    public List<Mixes> getMixes(){
        return this.mixes;
    }
    public void setMixes(List<Mixes> mixes){
        this.mixes = mixes;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public Number getNext_page(){
        return this.next_page;
    }
    public void setNext_page(Number next_page){
        this.next_page = next_page;
    }
    public String getNotices(){
        return this.notices;
    }
    public void setNotices(String notices){
        this.notices = notices;
    }
    public Number getPage(){
        return this.page;
    }
    public void setPage(Number page){
        this.page = page;
    }
    public String getPath(){
        return this.path;
    }
    public void setPath(String path){
        this.path = path;
    }
    public Number getPer_page(){
        return this.per_page;
    }
    public void setPer_page(Number per_page){
        this.per_page = per_page;
    }
    public String getPrevious_page(){
        return this.previous_page;
    }
    public void setPrevious_page(String previous_page){
        this.previous_page = previous_page;
    }
    public String getRestful_url(){
        return this.restful_url;
    }
    public void setRestful_url(String restful_url){
        this.restful_url = restful_url;
    }
    public String getSmart_id(){
        return this.smart_id;
    }
    public void setSmart_id(String smart_id){
        this.smart_id = smart_id;
    }
    public String getStatus(){
        return this.status;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public Number getTotal_entries(){
        return this.total_entries;
    }
    public void setTotal_entries(Number total_entries){
        this.total_entries = total_entries;
    }
    public Number getTotal_pages(){
        return this.total_pages;
    }
    public void setTotal_pages(Number total_pages){
        this.total_pages = total_pages;
    }
}