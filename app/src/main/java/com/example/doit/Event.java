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
    public Event(String eName, String desc){
        this.eventName=eName;
        this.description=desc;

    }
    public Event editEvent(String eName, String desc){
        return new Event(eName,desc);
    }

    public String getEventName() {
        return eventName;
    }

    public String getDescription() {
        return description;
    }
}
