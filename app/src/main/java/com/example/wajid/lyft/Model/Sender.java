package com.example.wajid.lyft.Model;

/**
 * Created by wajid on 13-Mar-18.
 */

public class Sender {


        public Data data;
        public String to;

    public Sender(){

    }

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
