package com.example.doit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
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


    EditText name;
    EditText desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //getting components
        Button add=(Button)findViewById(R.id.add);
        name=(EditText)findViewById(R.id.name);
        desc=(EditText)findViewById(R.id.description);


        //the add bottom's onclick method
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = name.getText().toString();
                String d = desc.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("name",n);
                bundle.putString("description",d);
                Intent intent = new Intent(EventActivity.this,MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    //customize the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_menu, menu);
        MenuItem del=menu.findItem(R.id.delete);
        del.setActionView(R.layout.item_menu);
        //the delete bottom's onclick method
        del.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisible(false);//a test action
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


}
