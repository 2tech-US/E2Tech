package com.example.e2tech.Models;

public class CategoryModel {

    String id;
    String name;

    public CategoryModel() {

    }

    public CategoryModel(String name) {
        this.name = name;
    }

    public CategoryModel(String id, String name) {
        this.id = id;
        this.name = name;
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
}
