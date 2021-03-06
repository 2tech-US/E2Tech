package com.example.e2tech.Models;

public class CategoryModel {

    String id;
    String name;
    String img_url;

    public CategoryModel() {

    }

    public CategoryModel(String name) {
        this.name = name;
    }

    public CategoryModel(String id, String name, String img_url) {
        this.id = id;
        this.name = name;
        this.img_url = img_url;
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

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
