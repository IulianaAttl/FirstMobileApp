package com.example.notesapp;

//note class
public class Note {
    private String title;
    private String info;
    private String tag;
    private String date;
    private String time;

    //empty constructor
    public Note(){

    }

    //constructor
    public Note(String title, String info, String tag, String date, String time){
        this.title = title;
        this.info = info;
        this.tag = tag;
        this.date = date;
        this.time = time;
    }

    //getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
