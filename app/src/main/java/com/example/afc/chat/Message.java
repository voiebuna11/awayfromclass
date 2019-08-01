package com.example.afc.chat;

public class Message {
    private int id;
    private int from;
    private int to;
    private String text;
    private String date;

    public Message(int id, int from, int to, String text, String date) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.text = text;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
