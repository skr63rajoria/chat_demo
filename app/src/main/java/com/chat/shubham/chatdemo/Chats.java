package com.chat.shubham.chatdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Chats extends AppCompatActivity {
    TextView chat;
    Button send;
    EditText msg;
    ListView listView;
    String username,chatroom,key;
    ArrayList<String> chatArr = new ArrayList<>();
   MsgAdapter msgAdapter;
    DatabaseReference root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        //chat = (TextView)findViewById(R.id.chat);
        send = (Button)findViewById(R.id.btnSEnd);
        msg = (EditText) findViewById(R.id.ed_msg);
        listView =  (ListView)findViewById(R.id.chatListView);
        username = getIntent().getStringExtra("user");
        chatroom = getIntent().getStringExtra("roomname");

        setTitle("Room "+chatroom.replace(MainActivity.name,"").trim());
        root = FirebaseDatabase.getInstance().getReference().child("users").child(chatroom);

        msgAdapter = new MsgAdapter(Chats.this,chatArr);
        listView.setAdapter(msgAdapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!msg.getText().toString().equals(""))
                {
                    Map<String, Object> map = new HashMap<String, Object>();
                    key = root.push().getKey();
                    root.updateChildren(map);
                    DatabaseReference messageRoot = root.child(key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("name", username);
                    map2.put("message", msg.getText().toString());
                    messageRoot.updateChildren(map2);
                    msg.setText("");
                }
                else {
                    Toast.makeText(getApplicationContext(),"please enter data",Toast.LENGTH_SHORT).show();
                }

            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                //String data = dataSnapshot.getValue().toString();
                String sr = s;

                //String msg = root.child(s).toString();
                //Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();

                appendChatConversation(dataSnapshot);
                //Toast.makeText(getApplicationContext(),"data added",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                appendChatConversation(dataSnapshot);
                //Toast.makeText(getApplicationContext(),"data changed",Toast.LENGTH_SHORT).show();


            }



            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
    String chatMsg,chatuserNaem;
    private void appendChatConversation(DataSnapshot dataSnapshot)
    {
        Iterator i = dataSnapshot.getChildren().iterator();

        //Iterator i2 = dataSnapshot.getChildren().iterator();
        //Log.d("key", (String)((DataSnapshot)i2.next()).getKey());
        while (i.hasNext())
        {
            chatMsg = (String)((DataSnapshot)i.next()).getValue();
            chatuserNaem = (String)((DataSnapshot)i.next()).getValue();
            chatArr.add(chatuserNaem+" "+chatMsg);
        }
        msgAdapter.notifyDataSetChanged();

    }
}

