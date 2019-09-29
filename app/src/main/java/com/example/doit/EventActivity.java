package com.example.doit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.speech.RecognitionService;
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

    private static final String EVENTS = "events";
    private static final String EVENT = "event";

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private ArrayList<Event> events;

    EditText name;
    EditText desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Button add=(Button)findViewById(R.id.add);
        name=(EditText)findViewById(R.id.name);
        desc=(EditText)findViewById(R.id.description);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n= name.getText().toString();
                String d=desc.getText().toString();
                setVisible(false);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_menu, menu);
        MenuItem del=menu.findItem(R.id.delete);
        del.setActionView(R.layout.item_menu);
        del.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               setVisible(false);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
