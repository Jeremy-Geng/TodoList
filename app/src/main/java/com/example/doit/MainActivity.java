package com.example.doit;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.app.Activity;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "MyActivity";

    /**Cutomized BaseAdapter class to showcase different tasks in a listview
     * -Shuhao Geng 16/10/2019
     * **/
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
            e_Name.setTextColor(Color.BLACK);
            //CheckBox e_complete=(CheckBox) convertView.findViewById(R.id.complete);
            e_Name.setText(events.get(i).getEventName());
            e_Description.setText(events.get(i).getDescription());
            e_Date.setText(events.get(i).getDate());
            if (events.get(i).getComplete()){
                e_Name.setTextColor(Color.LTGRAY);
                e_Description.setTextColor(Color.LTGRAY);
                e_Date.setTextColor(Color.LTGRAY);
            }
            return convertView;
        }
    }

    private ListView list_events;
    public ArrayList<Event> events;
    private EventAdapator eAdapter;
    private Context eContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setStatusBarColor(this,Color.LTGRAY);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);

        list_events = (ListView)findViewById(R.id.list_item);
        eContext = MainActivity.this;

        events  = new ArrayList<>();
        restore();
        Bundle bunlde = getIntent().getExtras();

        /**Add, edit and delete one specific task
         * -Shuhao Geng 13/10/2019
         * **/
        if(bunlde != null) {
            String eName = bunlde.getString("name");
            String eDate=bunlde.getString("date");
            String eTime=bunlde.getString("time");
            String eDescription = bunlde.getString("description");
            String eLocation=bunlde.getString("location");
            boolean eComplete=bunlde.getBoolean("complete",false);
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
                    long timegap = timeGap(eDate, eTime);
                    if(eComplete == false){
                        if(timegap == 0){
                            timegap = -1000;
                        }
                        setAlarm(events.get(i).getRequestCode(),timegap,eName);
                    }else{
                        deleteAlarm(i);
                    }
                    save();

                }else{
                    deleteAlarm(i);
                    events.remove(i);
                    save();
                }
            } else {
                if(flag == null){
                    Event newEvent = new Event(eName,eDate,eTime, eDescription,eLocation,eComplete);
                    newEvent.setRequestCode();
                    events.add(newEvent);
                    long timegap = timeGap(eDate, eTime);
                    if(timegap == 0){
                        timegap = -1000;
                    }
                    /**
                     * notify the user when he/her reach the destination
                     * Lue Cai -17/10/2019
                     * ***/
                    MapsActivity adf=new MapsActivity();
                    if (adf.distance()<1000){
                        setAlarm(newEvent.getRequestCode(), (long)adf.distance(), "reach the destination");
                    }
                    save();
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,EventActivity.class);
                startActivity(intent);
            }
        });
    }

    /**Sava tasks' information into local memory in order to restore them when the APP is opened again.
     * -Shuhao Geng 13/10/2019
     * **/
    public void save(){
        try{
            File filesDir = new File(getDir("myFile", MODE_PRIVATE).getAbsolutePath()) ;
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(filesDir.toString() +"/event.ser"));
            objectOutputStream.writeObject(events);

            objectOutputStream.close();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**Restore tasks' information from local memory
     * -Shuhao Geng 13/10/2019
     * **/
    public void restore(){
        try{
            File filesDir = new File(getDir("myFile", MODE_PRIVATE).getAbsolutePath()) ;
            if(filesDir.exists()) {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filesDir.toString() +"/event.ser"));
                events = (ArrayList<Event>) objectInputStream.readObject();
                objectInputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    /** Auxiliary method to calculate time difference for reminder
     * -Shuhao Geng 16/10/2019
     * **/
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

    /** Auxiliary method of setting one alarm manager for reminder
     * -Shuhao Geng 16/10/2019
     * **/
    public  void setAlarm(int requestCode, long between,String name){
        Intent intent;
        if(between < 0){
            intent = new Intent(this, SilentBroadcastReceiver.class);
        }else{
            intent = new Intent(this, MyBroadcastReceiver.class);
        }
        intent.putExtra("Name", name);
        intent.putExtra("RequestCode",requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + between, pendingIntent);

    }

    /** Auxiliary method of deleting one alarm manager for reminder
     * -Shuhao Geng 16/10/2019
     * **/
    public void deleteAlarm(int index){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), events.get(index).getRequestCode(), intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    /** set the color of the statusbar
     * -Shuhao Geng 16/10/2019
     * **/
    static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(statusColor);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }
}
