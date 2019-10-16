package com.example.doit;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.CheckBox;
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
            TextView e_Date=(TextView)convertView.findViewById(R.id.even_date);
            //CheckBox e_complete=(CheckBox) convertView.findViewById(R.id.complete);
            e_Name.setText(events.get(i).getEventName());
            e_Description.setText(events.get(i).getDescription());
            e_Date.setText(events.get(i).getDate());
            if (events.get(i).getComplete()){
                e_Name.setTextColor(Color.RED);
                e_Description.setTextColor(Color.RED);
                e_Date.setTextColor(Color.RED);
            }
            return convertView;
        }
    }

    //this two are use in a simple test
    private ListView list_events;
    public ArrayList<Event> events;
    private EventAdapator eAdapter;
    private Context eContext;
    private ArrayList<AlarmManager> alarmManagers;

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
            String eDate=bunlde.getString("date");
            String eTime=bunlde.getString("time");

            Log.i(TAG,"etime = "+eTime);
            Log.i(TAG,"eDate = " + eDate);

            String eDescription = bunlde.getString("description");
            String eLocation=bunlde.getString("location");
            boolean eComplete=bunlde.getBoolean("complete");
            String index = bunlde.getString("index");
            String flag = bunlde.getString("flag");

            if (index != null) {
                int i = Integer.parseInt(index);
                if(flag == null){
                    events.get(i).setEventName(eName);
                    events.get(i).setDate(eDate);
                    events.get(i).setTime(eTime);
                    events.get(i).setDescription(eDescription);
                    events.get(i).setLocation(eLocation);
                    events.get(i).setComplete(eComplete);
                    save();
                }else{
                    events.remove(i);
                    save();
                }
            } else {
                if(flag == null){
                    Event newEvent = new Event(eName,eDate,eTime, eDescription,eLocation,eComplete);
                    events.add(newEvent);
                    save();

                    long timegap = timeGap(eDate,eTime);
                    alarmManagers = new ArrayList<>();
                    setAlarm(events.size()-1, timegap, eName);
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
                String t=event.getTime();
                String loc=event.getLocation();
                boolean c=event.getComplete();
                String index = Integer.toString(i);
                Bundle bundle = new Bundle();
                bundle.putString("index",index);
                bundle.putString("name",n);
                bundle.putString("time",t);
                bundle.putString("date",da);
                bundle.putString("description",d);
                bundle.putString("location",loc);
                bundle.putBoolean("complete",c);
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

    private static long timeGap(String date, String time ){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curTime =  new Date(System.currentTimeMillis());
        String specificTime = date + " "+ time + ":00";
        long between = 0;
        try{
            Date futureTime = formatter.parse(specificTime);
            between = futureTime.getTime()- curTime.getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        return between;
    }

    public  void setAlarm(int index, long between,String name){
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        intent.putExtra("Name", name);
        intent.putExtra("Index",index);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), index, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent);
        alarmManagers.add(alarmManager);

    }

    public void deleteAlarm(){

    }


}
