package com.example.wajid.lyft.Model;

/**
 * Created by wajid on 13-Mar-18.
 */

public class Data {
    public String title;
    public String body;

    public Data() {

    }

    public Data(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

