package com.example.doit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";

    public class EventAdapator extends BaseAdapter{

        public ArrayList<Event> events;
        private Context econtext;

        public EventAdapator(ArrayList<Event> events,Context econtext){
            super();
            this.events = events;
            this.econtext = eContext;
        }

        @Override
        public int getCount() {
            return events.size();
        }

        @Override
        public Object getItem(int i) {
            return events.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(econtext).inflate(R.layout.listview_item,null);
            TextView e_Name = (TextView) convertView.findViewById(R.id.even_name);
            TextView e_Description = (TextView) convertView.findViewById(R.id.event_discription);
            e_Name.setText(events.get(i).getEventName());
            e_Description.setText(events.get(i).getDescription());
            return convertView;
        }
    }

    //this two are use in a simple test
    private ListView list_events;
    public ArrayList<Event> events;
    private EventAdapator eAdapter;
    private Context eContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button add=(Button) findViewById(R.id.add);
        list_events = (ListView)findViewById(R.id.list_item);
        eContext = MainActivity.this;

        events  = new ArrayList<>();
        restore();

        Bundle bunlde = getIntent().getExtras();
        if(bunlde != null) {

            String eName = bunlde.getString("name");
            String eDescription = bunlde.getString("description");
            String eDate=bunlde.getString("date");
            String eLocation=bunlde.getString("location");
            String index = bunlde.getString("index");
            String flag = bunlde.getString("flag");

            if (index != null) {
                int i = Integer.parseInt(index);
                if(flag == null){
                    events.get(i).setEventName(eName);
                    events.get(i).setDescription(eDescription);
                    events.get(i).setDate(eDate);
                    events.get(i).setLocation(eLocation);
                    save();
                }else{
                    events.remove(i);
                    save();
                }
            } else {
                if(flag == null){
                    Event newEvent = new Event(eName, eDescription,eDate,eLocation);
                    events.add(newEvent);
                    save();
                }
            }
            for (Event e: events) {
                if (e.getDate() != null) {
                    if (calculateTime(e.getDate()) <= 0) {

                    }
                }
            }

        }
        eAdapter = new EventAdapator(events,eContext);
        list_events.setAdapter(eAdapter);

        list_events.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Event event =  (Event)adapterView.getItemAtPosition(i);
                String n = event.getEventName();
                String d = event.getDescription();
                String da=event.getDate();
                String loc=event.getLocation();
                String index = Integer.toString(i);
                Bundle bundle = new Bundle();
                bundle.putString("name",n);
                bundle.putString("description",d);
                bundle.putString("index",index);
                bundle.putString("date",da);
                bundle.putString("location",loc);
                Intent intent = new Intent(MainActivity.this,EventActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,EventActivity.class);
                startActivity(intent);
            }
        });

    }

    public void save(){
        try{
            File filesDir = new File(getDir("myFile", MODE_PRIVATE).getAbsolutePath()) ;
            Log.i(TAG,"file_dir="+filesDir);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(filesDir.toString() +"/event.ser"));
            objectOutputStream.writeObject(events);
            objectOutputStream.close();


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void restore(){
        try{
            File filesDir = new File(getDir("myFile", MODE_PRIVATE).getAbsolutePath()) ;
            if(filesDir.exists()) {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filesDir.toString() +"/event.ser"));

                events = (ArrayList<Event>) objectInputStream.readObject();
                Log.i("name",events.get(0).getEventName() );

                objectInputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static double calculateTime(String date){
        double different=0;
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd");
        Date curDate =  new Date(System.currentTimeMillis());
        String   cD   =   formatter.format(curDate);
        String[] t0=cD.split("-");
        String[] t=date.split("-");
        for (int i=0;i<3;i++){
            if ((Integer.valueOf(t[i])-Integer.valueOf(t0[i]))>0){
                different+=(Integer.valueOf(t[i])-Integer.valueOf(t0[i]));
            }
        }
        return different;
    }


}
