package com.chat.shubham.chatdemo;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity
{
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    int notificationID;
    RemoteViews remoteViews;
    Context context;
    PendingIntent pendingIntent;
    Intent btnIntent;


    ListView buddyList;
    Button addBuddy;
    EditText buddyName;
    ArrayList<String> chatRoom = new ArrayList<>();
    ArrayAdapter<String> adapter;

    MsgAdapter msgAdapter = null;
    ChatDataBaseHelper baseHelper;
  public static String name = "nothing";
    String str = "";
    String buddy = "";
    boolean addSetFlag = false;
    public static HashSet<String> listSet = new HashSet<>();


    private boolean offlineStatus = false;
    Set<String> set = new HashSet<String>();
   static HashMap<String, String> friendsHash = new HashMap<>();

    public static String chatuser = "";
    DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot().child("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;


        Toast.makeText(context,getIntent().getStringExtra("data"),Toast.LENGTH_SHORT).show();
        remoteViews = new RemoteViews(getPackageName(),R.layout.notification_layout);
        remoteViews.setTextViewText(R.id.tv_mainDetail,"Main detail");
        remoteViews.setTextViewText(R.id.tv_idDetatiledData,"data of detail");
        notificationID = (int)System.currentTimeMillis();



        boolean netStatus = false;
        netStatus = isNetworkAvailable();
        if(netStatus==false)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false);

            builder.setTitle("No Internet Connection Found");

            builder.setMessage("please connect to Internet Connection");

            builder.setPositiveButton("close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    offlineStatus =true;
                    dialog.cancel();
                    finish();
                }
            });



            builder.show();
        }





        baseHelper = new ChatDataBaseHelper(getApplicationContext());
        Cursor cursor = baseHelper.getUser();
        if(cursor.moveToNext())
        {
            name = cursor.getString(0).trim().toUpperCase();
        }
        else {
            requestUserName();
        }

        Cursor cursor1 = baseHelper.getFriends();
        if(cursor1.moveToNext())
        {
            String fname = cursor1.getString(0);
            String key = cursor1.getString(1);
            friendsHash.put(fname,key);
           listSet.add(fname);

        }
        if(!listSet.isEmpty())
        {
            chatRoom.addAll(listSet);
        }


        buddyList = (ListView)findViewById(R.id.buddyList);
        addBuddy = (Button)findViewById(R.id.addBuddy);
        buddyName = (EditText)findViewById(R.id.ed_buddy);
        //msgAdapter = new MsgAdapter(getApplicationContext(),chatRoom);
        adapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,chatRoom);
        buddyList.setAdapter(adapter);

        addBuddy.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v)
            {

                startService(new Intent(getBaseContext(),MyService.class));
                addSetFlag = true;
                buddy = buddyName.getText().toString().toUpperCase();
                if(!buddy.equals(""))
                {
                    DatabaseReference newRef = FirebaseDatabase.getInstance().getReference().getRoot().child("users").child(buddy+" "+name);
                    newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if(dataSnapshot.exists())
                            {
                                ChatDataBaseHelper baseHelper = new ChatDataBaseHelper(getApplicationContext());
                                baseHelper.addFreinds(buddy,buddy+" "+name);
                                listSet.add(buddy);
                                chatRoom.clear();
                                chatRoom.addAll(listSet);
                                adapter.notifyDataSetChanged();
                                //Toast.makeText(getApplicationContext(),"already added "+buddy,Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put(name + " " + buddyName.getText().toString().trim().toUpperCase(), "");
                                root.updateChildren(map);
                                baseHelper.addFreinds(buddy,name+" "+buddy);
                                listSet.add(buddy);
                                chatRoom.clear();
                                chatRoom.addAll(listSet);
                                //makeNotification();
                                //Toast.makeText(getApplicationContext(),"now added "+buddy,Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                                buddyName.setText("");
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"please eneter data",Toast.LENGTH_SHORT).show();
                }
            }
        });




        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                String  str = dataSnapshot.getKey();
                if(str.contains(name))
                {
                    if(!friendsHash.containsKey(str.replace(name,"")))
                    {
                        ChatDataBaseHelper baseHelper = new ChatDataBaseHelper(getApplicationContext());
                        friendsHash.put(str.replace(name,"").trim(),str);
                        baseHelper.addFreinds(str.replace(name,"").trim(),str);
                        listSet.add(str.replace(name,"").trim());
                        chatRoom.clear();
                        chatRoom.addAll(listSet);
                        adapter.notifyDataSetChanged();
                    }

                }
                //Toast.makeText(getApplicationContext(),"added listner  "+str,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                //Toast.makeText(getApplicationContext(),"changed listner  "+s,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





       /* root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
               boolean val = dataSnapshot.hasChild("ASHISH SHUBHAM");
                boolean val1 = dataSnapshot.hasChild("ASHIvhSH SHUBHAM");
                //String str = dataSnapshot.getValue().toString();
                    Iterator i = dataSnapshot.getChildren().iterator();


                //chatRoom.clear();
                while (i.hasNext())
                {
                   str = ((DataSnapshot)i.next()).getKey();
                    //set.add(str);
                   if(str.contains(name)) {
                       String listName = str.replace(name, "").trim();
                       if (!friendsHash.containsKey(listName))
                       {
                           friendsHash.put(listName,str);
                           baseHelper = new ChatDataBaseHelper(getApplicationContext());
                           baseHelper.addFreinds(listName,str);
                           //chatRoom.add(listName);
                           set.add(listName.toUpperCase());
                           //addSetFlag = false;
                       }
                   }
                }

                chatRoom.clear();
                *//*chatRoom.addAll(set);*//*
                chatRoom.addAll(set);

                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"add new member",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });*/

        buddyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean netStatus = false;
                netStatus = isNetworkAvailable();
                if (netStatus == false) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(false);

                    builder.setTitle("No Internet Connection Found");

                    builder.setMessage("please connect to Internet Connection");

                    builder.setPositiveButton("close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            offlineStatus = true;
                            dialog.cancel();
                            //finish();
                        }
                    });


                    builder.show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), Chats.class);
                    //chatuser = name + " " + ((TextView) view).getText().toString().trim().toUpperCase();
                    //chatuser = name+" "+((TextView)view).getText().toString().trim().toUpperCase();
                    String friend = ((TextView) view).getText().toString().trim().toUpperCase();
                    intent.putExtra("roomname", friendsHash.get(friend));
                    intent.putExtra("user", name);
                    startActivity(intent);
                }
            }
        });


        buddyList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {


                DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot().child("users");
                final String friend = ((TextView)view).getText().toString().trim().toUpperCase();
                root.child(name+" "+friend).removeValue(new DatabaseReference.CompletionListener()
                {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(getApplicationContext(),friend+" deleted",Toast.LENGTH_SHORT).show();
                        baseHelper = new ChatDataBaseHelper(getApplicationContext());
                        baseHelper.deleteFriend(friend);
                        listSet.remove(friend);
                        chatRoom.clear();
                        chatRoom.addAll(listSet);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(),"your removed "+friend,Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater men = getMenuInflater();
        men.inflate(R.menu.mymenus,menu);
        return super.onCreateOptionsMenu(menu);

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==R.id.uploadData)
        {
            startActivity(new Intent(getApplicationContext(),DataFirebase.class));
        }

        return super.onOptionsItemSelected(item);
    }



    void requestUserName()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter name");
        builder.setCancelable(false);
        final EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                name = editText.getText().toString().trim().toUpperCase();
                baseHelper = new ChatDataBaseHelper(getApplicationContext());
                baseHelper.deleteUser(name);
               boolean i = baseHelper.adduser(name);
                if(i)
                {
                    Toast.makeText(getApplicationContext(),"you are registered",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"registration failed",Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
                requestUserName();
            }
        });
        builder.show();

    }

    private boolean isNetworkAvailable()
    {
        boolean mobilenet =false;
        boolean wifinet=false;

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        //Network[] networkInfo = connectivityManager.getAllNetworks();
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for(NetworkInfo ni : networkInfo)
        {
            if(ni.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if(ni.isConnected())
                {
                    wifinet=true;
                }
            }
            if(ni.getTypeName().equalsIgnoreCase("MOBILE"))
            {
                if(ni.isConnected())
                {
                    mobilenet=true;
                }
            }
        }
        return mobilenet||wifinet;

    }

    void makeNotification()
    {
        btnIntent = new Intent(context,MainActivity.class);

        btnIntent.putExtra("data","main data");
        btnIntent.putExtra("id",notificationID);

        btnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        pendingIntent = PendingIntent.getActivity(context,0,btnIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_do_Something,pendingIntent);


        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(MainActivity.this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)

                .setContent(remoteViews)
                .setContentIntent(pendingIntent);
        builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(notificationID,builder.build());
    }


}
