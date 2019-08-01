package com.example.afc.app;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String type;
    private String user;
    private String email;
    private String fname;
    private String lname;
    private String city;
    private String phone;
    private String year;
    private String spec;
    private String pic;
    private String chat_id;

    public User(String id, String type, String user, String email, String fname, String lname, String city, String phone, String year, String spec, String pic, String chat_id) {
        this.id = id;
        this.type = type;
        this.user = user;
        this.email = email;
        this.fname = fname;
        this.lname = lname;
        this.city = city;
        this.phone = phone;
        this.year = year;
        this.spec = spec;
        this.pic = pic;
        this.chat_id = chat_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }
}