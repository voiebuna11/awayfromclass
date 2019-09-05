package com.example.afc.content;

public class Content {
    private int id;
    private int course_id;
    private String name;
    private String description;

    public Content(int id, int course_id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.course_id = course_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
