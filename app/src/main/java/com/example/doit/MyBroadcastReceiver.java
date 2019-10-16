package com.example.doit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.provider.Settings.System.getString;
import static androidx.core.content.ContextCompat.getSystemService;


public class MyBroadcastReceiver extends BroadcastReceiver {
    public static  final String NOTIFICATION_CHANNEL_ID = "channel_id";
    public static final String CHANNEL_NAME = "Notification Channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra("Name");
        int index = intent.getIntExtra("Index",0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.GREEN);
            channel.setVibrationPattern(new long[] {
                    500,
                    500,
                    500,
                    500,
                    500
            });

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Time to do:")
                .setContentText(name)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(index, builder.build());
















    }
}
