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
    String flag = "-1";
    String INDEX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //getting components
        name=(EditText)findViewById(R.id.name);
        desc=(EditText)findViewById(R.id.description);
        Button add=(Button)findViewById(R.id.add);

        String eName;
        String eDescription;
        final Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            add.setText("Edit");
            eName = bundle.getString("name");
            eDescription = bundle.getString("description");
            INDEX = bundle.getString("index");
            name.setText(eName);
            desc.setText(eDescription);
        }else{
            add.setText("ADD");
        }

        //the add bottom's onclick method
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = name.getText().toString();
                String d = desc.getText().toString();
                Bundle newBundle = new Bundle();
                if(bundle != null){
                    String index = bundle.getString("index");
                    newBundle.putString("index",index);
                }
                newBundle.putString("name",n);
                newBundle.putString("description",d);
                Intent intent = new Intent(EventActivity.this,MainActivity.class);
                intent.putExtras(newBundle);
                startActivity(intent);
            }
        });

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


}
