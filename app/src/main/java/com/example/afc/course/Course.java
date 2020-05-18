package com.example.afc.course;

public class Course {
    private int id;
    private String name;
    private String enroll;
    private String author;
    private String folder;

    public Course(int id, String name, String enroll, String author, String folder) {
        this.id = id;
        this.name = name;
        this.enroll = enroll;
        this.author = author;
        this.folder = folder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnroll() {
        return enroll;
    }

    public void setEnroll(String enroll) {
        this.enroll = enroll;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
