package com.example.doit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.speech.RecognitionService;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
/***
 * A event operation class that contain basic information and operation.
 * Enable users to edit event's name and description Directly
 * **/
public class EventActivity extends AppCompatActivity {
    EditText name;
    EditText desc;
    EditText date;
    EditText time;
    EditText location;
    String flag = "-1";
    String INDEX;

    public static int LOCATION_CONTACT=1;

    private Calendar calendar;
    private Calendar timeCalendar;

    private int eYear;
    private int eMonth;
    private int eDay;
    private int eHour;
    private int eMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //getting components
        name=(EditText)findViewById(R.id.name);
        desc=(EditText)findViewById(R.id.description);
        date = (EditText) findViewById(R.id.date);
        time=(EditText)findViewById(R.id.time) ;
        location=(EditText)findViewById(R.id.location);

        Button add=(Button)findViewById(R.id.add);

        date.setInputType(InputType.TYPE_NULL);
        time.setInputType(InputType.TYPE_NULL);
        calendar = Calendar.getInstance();
        timeCalendar=Calendar.getInstance();

        String eName;
        String eDescription;
        String eDate;
        String eTime;
        String eLocation;
        final Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            add.setText("Edit");
            eName = bundle.getString("name");
            eDate=bundle.getString("date");
            eTime=bundle.getString("time");
            eDescription = bundle.getString("description");
            eLocation=bundle.getString("location");
            INDEX = bundle.getString("index");
            name.setText(eName);
            date.setText(eDate);
            time.setText(eTime);
            desc.setText(eDescription);
            location.setText(eLocation);
        }else{
            add.setText("ADD");
        }

        //the add bottom's onclick method
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = name.getText().toString();
                String da=date.getText().toString();
                String t=time.getText().toString();
                String d = desc.getText().toString();
                String l=location.getText().toString();
                Bundle newBundle = new Bundle();
                if(bundle != null){
                    String index = bundle.getString("index");
                    newBundle.putString("index",index);
                }
                newBundle.putString("name",n);
                newBundle.putString("date",da);
                newBundle.putString("time",t);
                newBundle.putString("description",d);
                newBundle.putString("location",l);
                Intent intent = new Intent(EventActivity.this,MainActivity.class);
                intent.putExtras(newBundle);
                startActivity(intent);
            }
        });
        /***
         * Below are a series of operation that allow users
         * choose date, time, location when they click related editview once
         *-Lue Cai, 15/10/2019
         * **/
        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    dateSelection();
                }
            }
        });
       date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateSelection();
            }
        });
       time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View view, boolean b) {
               if (b){
                   timeSelection();
               }
           }
       });
       time.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               timeSelection();
           }
       });
       location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Intent intent=new Intent(EventActivity.this,MapsActivity.class);
                startActivityForResult(intent,LOCATION_CONTACT);
            }
        });
       location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(EventActivity.this,MapsActivity.class);
                startActivityForResult(intent,LOCATION_CONTACT);
            }
        });
    }
    /**A function that create a calendar dialog for user to choose a date
     * -Lue Cai, 15/10/2019
     * **/
    private void dateSelection(){
        new DatePickerDialog(EventActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int month, int day) {
                        // TODO Auto-generated method stub
                        eYear = year;
                        eMonth = month;
                        eDay = day;
                        // calculate the date
                        date.setText(new StringBuilder()
                                .append(eYear)
                                .append("-")
                                .append((eMonth + 1) < 10 ? "0"
                                        + (eMonth + 1) : (eMonth + 1))
                                .append("-")
                                .append((eDay < 10) ? "0" + eDay : eDay));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    /**A function that create a time picker dialog for user to choose a time(24h)
     * -Lue Cai, 15/10/2019
     * **/
    private void timeSelection(){
        new TimePickerDialog(EventActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                eHour = i;
                eMinute = i1;

                time.setText(new StringBuilder()
                        .append(eHour)
                        .append(":")
                        .append(eMinute));
            }
        }, timeCalendar.get(Calendar.HOUR), timeCalendar.get(Calendar.MINUTE),true).show();

    }

    /**A simple method that customizes the menu
     * -Lue Cai, 14/10/2019
     * **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_menu, menu);
        MenuItem del=menu.findItem(R.id.delete);
        del.setActionView(R.layout.item_menu);
        //the delete bottom's onclick method
        del.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventActivity.this,MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("flag",flag);
                bundle.putString("index",INDEX);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**A method that receive location data from the MapsActivity.java
     *-Lue Cai, 14/10/2019
     * **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_CONTACT && resultCode == Activity.RESULT_OK ){
            location.setText(data.getStringExtra("location"));
        }
    }
}
