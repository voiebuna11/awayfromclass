package com.example.afc.chat;

import com.example.afc.user.User;

public class LobbyUser {
    private User user;
    private int from;
    private int to;
    private String text;
    private String date;

    public LobbyUser(User user, int from, int to, String text, String date) {
        this.user = user;
        this.from = from;
        this.to = to;
        this.text = text;
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
