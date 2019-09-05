package com.example.afc.user;

//@author Claudiu
// target_type => course, user, content, file, etc / (items)
// type => course_enrollment, course_managing.. etc / (types of action)
// action => added, removed, edited, created.. etc (actions)

public class News {
    private int id;
    private int author_id;
    private int target_id;
    private String target_type;
    private String type;
    private String action;
    private String date;
    private User author;

    public News(int id, int author_id, int target_id, String target_type, String type, String action, String date, User author) {
        this.id = id;
        this.author_id = author_id;
        this.target_id = target_id;
        this.target_type = target_type;
        this.type = type;
        this.action = action;
        this.date = date;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public int getTarget_id() {
        return target_id;
    }

    public void setTarget_id(int target_id) {
        this.target_id = target_id;
    }

    public String getTarget_type() {
        return target_type;
    }

    public void setTarget_type(String target_type) {
        this.target_type = target_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
