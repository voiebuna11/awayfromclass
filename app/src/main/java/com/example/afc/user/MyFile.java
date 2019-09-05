package com.example.afc.user;

public class MyFile {
    private String id;
    private String content_id;
    private String name;
    private String author;
    private String type;
    private String date;

    public MyFile(String id, String author, String name, String type, String date, String content_id) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.type = type;
        this.date = date;
        this.content_id = content_id;
    }

    public MyFile(String id, String author, String name, String type, String date) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.type = type;
        this.date = date;
    }


    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
