package com.example.doit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //this two are use in a simple test
    private ListView listView;
    public ListActivity la;

    private final int ADD=0;
    private final int UPDATE=1;

    public ArrayList<String> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button add=(Button) findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,EventActivity.class);
                startActivity(intent);
            }
        });
//some setting about the listview
//        listView = new ListView(this);
//        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,getData()));
//        setContentView(listView);


    }
//this method can show a list in MainActivity
//    private List<String> getData(){
//
//        List<String> data = new ArrayList<String>();
//        data.add("test1");
//        data.add("test2");
//        data.add("test3");
//        data.add("test4");
//
//        return data;
//    }
}
