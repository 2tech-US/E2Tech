package com.example.e2tech.Models;

public class BannerModel {

    int imgId;
    String img_url;

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public BannerModel(String img_url) {
        this.img_url = img_url;
    }

    public BannerModel() {
    }

    public BannerModel(int imgId) {
        this.imgId = imgId;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }



}
