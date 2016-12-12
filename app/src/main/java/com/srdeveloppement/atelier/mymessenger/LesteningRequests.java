package com.srdeveloppement.atelier.mymessenger;

import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

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
    MessageQuerry msg;
    EmmeteurIP ip ;
    String myIP;

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

    public LesteningRequests(MyAdapter adapter,RecyclerView recyclerView,EditText area,Button send,EmmeteurIP ip,String myIp) {
        buffer = new byte[6000];
        this.adapter=adapter;
        this.recyclerView = recyclerView;
        this.area= area;
        this.send=send;
        hn = new Handler();
        this.ip =ip;
        this.myIP =myIp;
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

                    switch (msg.getQuerry()){
                        case 1 :
                            ChatActivity.Disc.add(new Discution(Calendar.getInstance(), msg.getMessage(),true,"",false));
                            hn.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.loadNewData(ChatActivity.Disc);
                                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                    area.setText("");
                                    send.setBackgroundResource(R.drawable.send_disabled);
                                }
                            });
                            break;
                        case 2 :
                            DatagramSocket sc = new DatagramSocket();
                            MessageQuerry ms = new MessageQuerry("",3,"","");
                            Object m =ms ;
                            byte[] conect= serialize(m);
                            sleep(300);
                            DatagramPacket pk = new DatagramPacket(conect,conect.length, ip.getAdr(),9999);
                            sc.send(pk);
                            sc.close();
                                ServerSocket socket = new ServerSocket(9999);
                                Socket s = socket.accept();
                                BufferedInputStream in = new BufferedInputStream(s.getInputStream());
                                File Source = Environment.getExternalStorageDirectory();
                                String Path = Source.getAbsolutePath()+"/MyMessenger/"+msg.getFileName()+msg.getFileFormat();
                                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(Path)));
                                while (true){
                                    int line = in.read();
                                    if(line<0){
                                        break;
                                    }
                                    out.write(line);
                                }
                                ChatActivity.Disc.add(new Discution(Calendar.getInstance(), msg.getFileName()+msg.getFileFormat(),true,"",false));
                                hn.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.loadNewData(ChatActivity.Disc);
                                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                        area.setText("");
                                        send.setBackgroundResource(R.drawable.send_disabled);
                                    }
                                });
                                socket.close();
                                s.close();
                    }
                }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                socketRecieve.close();
            }


    }
}
