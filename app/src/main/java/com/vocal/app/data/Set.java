package com.vocal.app.data;

/**
 * Created by rrasane on 5/9/14.
 */
public class Set{
    private boolean at_beginning;
    private boolean at_end;
    private boolean at_last_track;
    private boolean skip_allowed;
    private Track track;

    public boolean getAt_beginning(){
        return this.at_beginning;
    }
    public void setAt_beginning(boolean at_beginning){
        this.at_beginning = at_beginning;
    }
    public boolean getAt_end(){
        return this.at_end;
    }
    public void setAt_end(boolean at_end){
        this.at_end = at_end;
    }
    public boolean getAt_last_track(){
        return this.at_last_track;
    }
    public void setAt_last_track(boolean at_last_track){
        this.at_last_track = at_last_track;
    }
    public boolean getSkip_allowed(){
        return this.skip_allowed;
    }
    public void setSkip_allowed(boolean skip_allowed){
        this.skip_allowed = skip_allowed;
    }
    public Track getTrack(){
        return this.track;
    }
    public void setTrack(Track track){
        this.track = track;
    }
}