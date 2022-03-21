package com.example.e2tech.Models;

import java.io.Serializable;

public class OrderModel implements Serializable {

    String receiverName, address, createAt, id, note, phone, status, orderBy, userName, nameProducts;
    int fee_ship, subTotal, total, quantity;

    public OrderModel( String id, String address, String createAt, String nameProducts, int total, int quantity) {
        this.address = address;
        this.createAt = createAt;
        this.id = id;
        this.nameProducts = nameProducts;
        this.total = total;
        this.quantity = quantity;
    }

    public String getNameProducts() {
        return nameProducts;
    }

    public void setNameProducts(String nameProducts) {
        this.nameProducts = nameProducts;
    }

    public OrderModel(String receiverName, String address, String createAt, String id, String note, String phone, String status, String orderBy, String userName, int fee_ship, int subTotal, int total, int quantity) {
        this.receiverName = receiverName;
        this.address = address;
        this.createAt = createAt;
        this.id = id;
        this.note = note;
        this.phone = phone;
        this.status = status;
        this.orderBy = orderBy;
        this.userName = userName;
        this.fee_ship = fee_ship;
        this.subTotal = subTotal;
        this.total = total;
        this.quantity = quantity;
    }

    public OrderModel() {
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getFee_ship() {
        return fee_ship;
    }

    public void setFee_ship(int fee_ship) {
        this.fee_ship = fee_ship;
    }

    public int getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
