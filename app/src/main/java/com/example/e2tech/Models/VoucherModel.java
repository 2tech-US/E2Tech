package com.example.e2tech.Models;

import androidx.lifecycle.ViewModel;

import java.io.Serializable;

public class VoucherModel extends ViewModel implements Serializable {
    String code;
    double discount;
    String description;
    String img_url;

    public VoucherModel() {}

    public VoucherModel(String code, double discount, String description, String img_url) {
        this.code = code;
        this.discount = discount;
        this.description = description;
        this.img_url = img_url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
