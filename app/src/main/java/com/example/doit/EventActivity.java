package com.example.doit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
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

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity {


    EditText name;
    EditText desc;
    EditText date;
    EditText location;
    String flag = "-1";
    String INDEX;

    public static int LOCATION_CONTACT=1;

    private Calendar calendar;
    private int eYear;
    private int eMonth;
    private int eDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //getting components
        name=(EditText)findViewById(R.id.name);
        desc=(EditText)findViewById(R.id.description);
        Button add=(Button)findViewById(R.id.add);
        date = (EditText) findViewById(R.id.date);
        date.setInputType(InputType.TYPE_NULL);
        calendar = Calendar.getInstance();

        location=(EditText)findViewById(R.id.location);

        String eName;
        String eDescription;
        String eDate;
        String eLocation;
        final Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            add.setText("Edit");
            eName = bundle.getString("name");
            eDescription = bundle.getString("description");
            eDate=bundle.getString("date");
            eLocation=bundle.getString("location");
            INDEX = bundle.getString("index");
            name.setText(eName);
            desc.setText(eDescription);
            date.setText(eDate);
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
                String d = desc.getText().toString();
                String l=location.getText().toString();
                Bundle newBundle = new Bundle();
                if(bundle != null){
                    String index = bundle.getString("index");
                    newBundle.putString("index",index);
                }
                newBundle.putString("name",n);
                newBundle.putString("description",d);
                newBundle.putString("date",da);
                newBundle.putString("location",l);
                Intent intent = new Intent(EventActivity.this,MainActivity.class);
                intent.putExtras(newBundle);
                startActivity(intent);
            }
        });

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

        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Intent intent=new Intent(EventActivity.this,MapsActivity.class);
                startActivityForResult(intent,LOCATION_CONTACT);
            }
        });
       /* location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(EventActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });*/

    }
    //choose a date from calendar
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
                }, calendar.get(Calendar.YEAR), calendar
                .get(Calendar.MONTH), calendar
                .get(Calendar.DAY_OF_MONTH)).show();
    }

    //customize the menu
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_CONTACT && resultCode == Activity.RESULT_OK ){
            //location = data.getExtras().getParcelable("location");
            location.setText(data.getStringExtra("location"));
            //onResume();
        }
    }
}
