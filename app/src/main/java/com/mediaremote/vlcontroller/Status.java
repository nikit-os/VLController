package com.mediaremote.vlcontroller;

/**
 * Created by neon on 23.06.15.
 */

public class Status {

    private static Status instance =new Status();

    private String state = "";
    private int volume = 0;
    private String artist = "";
    private String filename = "";
    private int length = 0;
    private int time = 0;

    private Status() {
    }

    public static Status getInstance(){
        return instance;
    }

    public String getState() {return state; }

    public int getVolume() { return volume; }

    public String getArtist() {return artist;}

    public String getFilename() {
        return filename;
    }

    public int getLength() {
        return length;
    }

    public int getTime() {return time; }

    public void setState(String state) {this.state = state; }

    public void setVolume(int volume) {this.volume = volume;}

    public void setArtist(String artist) {this.artist = artist;}

    public void setFilename(String filename) {this.filename = filename;}

    public void setLength(int length) {this.length = length;}

    public void setTime(int time) {this.time = time;}
}
