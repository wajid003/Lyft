package com.example.wajid.lyft.Model;

/**
 * Created by wajid on 14-Jan-18.
 */

public class User {
    private String email,password,name,phone;

    public User() {

    }
    public User(String email, String password, String name, String phone){
        this.email=email;
        this.password=password;
        this.name=name;
        this.phone=phone;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
