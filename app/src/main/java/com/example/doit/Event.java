package com.example.doit;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    private String eventName;
    private String date;
    private String time;
    private String description;
    private String ifComplete;
    private String location;
    public Event(String eName,String date,String time ,String desc,String location){
        this.eventName=eName;
        this.date=date;
        this.time=time;
        this.description=desc;
        this.location=location;
    }
    public Event editEvent(String eName, String date,String time,String desc,String location){
        return new Event(eName,date,time,desc,location);
    }

    public String getEventName() {
        return eventName;
    }

    public String getDescription() {
        return description;
    }

    public String getDate(){return date;}

    public String getLocation(){return location;}

    public String getTime(){return time;}

    public void setEventName(String name){
        eventName = name;
    }
    public void setDescription(String description){
        this.description = description ;
    }
    public void setDate(String date){this.date=date;}
    public void setLocation(String location){this.location=location;}
    public void setTime(String time){this.time=time;}
}
