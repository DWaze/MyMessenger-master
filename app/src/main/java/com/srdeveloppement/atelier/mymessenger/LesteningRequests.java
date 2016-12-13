package com.srdeveloppement.atelier.mymessenger;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Calendar;

import static com.srdeveloppement.atelier.mymessenger.ChatActivity.Disc;

/**
 * Created by lhadj on 12/11/2016.
 */

public class LesteningRequests extends Thread {

    EditText area;
    Button send;
    Handler hn ;
    MyAdapter adapter =null;
    RecyclerView recyclerView = null;
    byte[] buffer =null;
    DatagramSocket socketRecieve;
    EmmeteurIP ip ;
    String myIP;
    MessageQuerry msg ;
    SharedPreferences sharedPreferences;

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream o = new ObjectInputStream(b)){
                return o.readObject();
            }
        }
    }

    public static byte[] serialize(Object obj) throws IOException {
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
                o.writeObject(obj);
            }
            return b.toByteArray();
        }
    }

    public LesteningRequests(MyAdapter adapter, RecyclerView recyclerView, EditText area, Button send, EmmeteurIP ip, String myIp, MessageQuerry msg1, SharedPreferences sharedPreferences) {
        buffer = new byte[6000];
        this.adapter=adapter;
        this.recyclerView = recyclerView;
        this.area= area;
        this.send=send;
        hn = new Handler();
        this.ip =ip;
        this.myIP =myIp;
        this.msg=msg1;
        this.sharedPreferences=sharedPreferences;
    }

    @Override
    public void run() {


            try {
                socketRecieve = new DatagramSocket(9999);
                DatagramPacket rPacket= new DatagramPacket(buffer,buffer.length);
                while(true){
                    socketRecieve.receive(rPacket);
                    String packetIP = rPacket.getAddress().toString();

                if(myIP.equals("/0.0.0.0")){
                    myIP="/192.168.43.1";
                }

                if(!packetIP.equals(myIP)){
                    ip.setAdr(rPacket.getAddress());
                    byte[] messageByte = rPacket.getData();
                    Object mObject;
                    mObject=deserialize(messageByte);
                    msg=(MessageQuerry)mObject;
                    ChatActivity.messageQuerry.setMessage(msg.getMessage());
                    ChatActivity.messageQuerry.setFileName(msg.getFileName());
                    ChatActivity.messageQuerry.setQuerry(msg.getQuerry());

                    switch (msg.getQuerry()){
                        case 1 :
                            Disc.add(new Discution(Calendar.getInstance(), msg.getMessage(),true,"",false));
                            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(Disc);
                            prefsEditor.putString("MyConversation", json);
                            prefsEditor.commit();
                            hn.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.loadNewData(Disc);
                                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                    area.setText("");
                                    send.setBackgroundResource(R.drawable.send_disabled);
                                }
                            });
                            break;

                    }
                }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                socketRecieve.close();
            }


    }
}
