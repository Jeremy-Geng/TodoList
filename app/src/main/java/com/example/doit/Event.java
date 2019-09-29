package com.example.doit;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class Event{
    private String eventName;
    private String description;
    private Date date;
    private boolean ifComplete;
    private Location location;
    public Event(String eName, String desc){
        this.eventName=eName;
        this.description=desc;
        //this.date=d;
        //this.location=l;
    }
    public Event editEvent(String eName, String desc){
        this.eventName=eName;
        this.description=desc;
        //this.date=d;
        //this.location=l;
        return new Event(eName,desc);
    }

    public String getEventName() {
        return eventName;
    }

    public String getDescription() {
        return description;
    }
}
