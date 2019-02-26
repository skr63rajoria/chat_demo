package com.chat.shubham.chatdemo;

/**
 * Created by CDAC on 1/4/2017.
 */
public class SenderReciver
{
    String getSenderName(String name,String sender)
    {
        String sName = "";
        return sName;
    }

    String getReciverName(String name, String sender)
    {
        String rName = name.replace(sender,"").trim();
        return rName;

    }

}
