package com.chat.shubham.chatdemo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by CDAC on 1/4/2017.
 */
public class MsgAdapter extends ArrayAdapter<String>
{

    ArrayList<String> resource;
    Context context;
    public MsgAdapter(Context context, ArrayList<String> data) {
        super(context, 0, data);
        this.resource = data;
        this.context = context;
    }


    @Override
    public int getPosition(String item) {
        return super.getPosition(item);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView = LayoutInflater.from(context).inflate(R.layout.msgtemplet,parent,false);
        TextView textView = (TextView)convertView.findViewById(R.id.reciverTv);
        TextView textView1 = (TextView)convertView.findViewById(R.id.senderTV);
        String str = getItem(position);
        String []strings = str.split(" ");
        String firstWord = strings[0];
        str = str.replace(firstWord,"").trim();
        //String name = new ChatDataBaseHelper(context).getUser().getString(0);
        if(firstWord.equals(MainActivity.name))
        {
            textView1.setVisibility(View.VISIBLE);
           // textView1.setTextColor(Color.RED);
            textView1.setText(str);
            textView.setVisibility(View.GONE);
        }
        else {
            textView.setVisibility(View.VISIBLE);
            //textView.setTextColor(Color.GREEN);
            textView.setText(str);
            textView1.setVisibility(View.GONE);
        }

        return convertView;
    }
}
