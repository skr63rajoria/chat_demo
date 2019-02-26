package com.chat.shubham.chatdemo;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by CDAC on 1/9/2017.
 */
public class MyService extends Service
{


    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    int notificationID;
    RemoteViews remoteViews;
    Context context;
    PendingIntent pendingIntent;
    Intent btnIntent;



    DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot().child("users");
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                makeNotification();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Stop location services",Toast.LENGTH_SHORT).show();
    }


    void makeNotification()
    {
        remoteViews = new RemoteViews(getPackageName(),R.layout.notification_layout);
        remoteViews.setTextViewText(R.id.tv_mainDetail,"Main detail");
        remoteViews.setTextViewText(R.id.tv_idDetatiledData,"data of detail");
        notificationID = (int)System.currentTimeMillis();
        btnIntent = new Intent(MyService.this,MainActivity.class);

        btnIntent.putExtra("data","main data");
        btnIntent.putExtra("id",notificationID);

        btnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        pendingIntent = PendingIntent.getActivity(MyService.this,0,btnIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_do_Something,pendingIntent);


        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        PackageManager manager = getPackageManager();

        builder = new NotificationCompat.Builder(MyService.this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)

                .setContent(remoteViews)
                .setContentIntent(pendingIntent);
        builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(notificationID,builder.build());
    }



}
