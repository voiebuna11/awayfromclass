package com.example.afc.user;

//@author Claudiu
// target_type => course, user, content, file, etc / (items)
// type => course_enrollment, course_managing.. etc / (types of action)
// action => added, removed, edited, created.. etc (actions)

import com.example.afc.content.Content;

public class News {
    private int id;
    private int author_id;
    private String target_id;
    private int context_id;
    private String target_type;
    private String context;
    private String action;
    private String date;
    private User author;
    private Content content;
    private User targetUser;

    public News(int id, int author_id, String target_id, int context_id, String target_type, String context, String action, String date, User author) {
        this.id = id;
        this.author_id = author_id;
        this.target_id = target_id;
        this.context_id = context_id;
        this.target_type = target_type;
        this.context = context;
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

    public String getTarget_id() {
        return target_id;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }

    public String getTarget_type() {
        return target_type;
    }

    public void setTarget_type(String target_type) {
        this.target_type = target_type;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
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

    public int getContext_id() {
        return context_id;
    }

    public void setContext_id(int context_id) {
        this.context_id = context_id;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }
}
