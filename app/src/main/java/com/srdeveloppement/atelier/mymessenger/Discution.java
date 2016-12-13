package com.srdeveloppement.atelier.mymessenger;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Sam on 08/03/2016.
 */
public class Discution implements Serializable {
    boolean senderVS;
    private String senderText;
    boolean reciverVS;
    private String reciverText;
    Calendar c;
    int hour;
    int minute;



    public Discution(Calendar c,String reciverText, boolean reciverVS, String senderText, boolean senderVS) {
        this.c = c;
        Calendar.getInstance();
       // int hour = c.get(Calendar.HOUR);
       // int minute = c.get(Calendar.MINUTE);
        this.reciverText = reciverText;
        this.reciverVS = reciverVS;
        this.senderVS = senderVS;
        this.senderText = senderText;
    }

    public Calendar getC() {
        return c;
    }

    public String getReciverText() {
        return reciverText;
    }

    public boolean isReciverVS() {
        return reciverVS;
    }

    public String getSenderText() {
        return senderText;
    }

    public boolean isSenderVS() {
        return senderVS;
    }

    public void setC(Calendar c) {
        this.c = c;
    }

    public void setReciverText(String reciverText) {
        this.reciverText = reciverText;
    }

    public void setReciverVS(boolean reciverVS) {
        this.reciverVS = reciverVS;
    }

    public void setSenderText(String senderText) {
        this.senderText = senderText;
    }

    public void setSenderVS(boolean senderVS) {
        this.senderVS = senderVS;
    }
}
