package com.example.doit;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    private String eventName;
    private String description;
    private String date;
    private String ifComplete;
    private String location;
    public Event(String eName, String desc,String date,String location){
        this.eventName=eName;
        this.description=desc;
        this.date=date;
        this.location=location;
    }
    public Event editEvent(String eName, String desc,String date,String location){
        return new Event(eName,desc,date,location);
    }

    public String getEventName() {
        return eventName;
    }

    public String getDescription() {
        return description;
    }

    public String getDate(){return date;}

    public String getLocation(){return location;}

    public void setEventName(String name){
        eventName = name;
    }
    public void setDescription(String description){
        this.description = description ;
    }
    public void setDate(String date){this.date=date;}
    public void setLocation(String location){this.location=location;}
}
