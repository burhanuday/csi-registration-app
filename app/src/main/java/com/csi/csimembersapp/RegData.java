package com.csi.csimembersapp;

public class RegData {
    String event_name, name, email, college, date_registered, registeredby, id, phone;
    int amount_paid, amount_pending, event_price;
    boolean used;

    public RegData(){

    }

    public RegData(int amount_paid, int amount_pending, String college, String date_registered, String email,
                   String event_name, int event_price, String name, String registeredby, String phone, boolean used){
        this.amount_paid = amount_paid;
        this.amount_pending = amount_pending;
        this.college = college;
        this.date_registered = date_registered;
        this.email = email;
        this.event_name = event_name;
        this.event_price = event_price;
        this.name = name;
        this.registeredby = registeredby;
        this.phone = phone;
        this.used = used;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAmount_paid() {
        return amount_paid;
    }

    public int getAmount_pending() {
        return amount_pending;
    }

    public int getEvent_price() {
        return event_price;
    }

    public String getCollege() {
        return college;
    }

    public String getDate_registered() {
        return date_registered;
    }

    public String getEmail() {
        return email;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getRegisteredby() {
        return registeredby;
    }

    public void setAmount_paid(int amount_paid) {
        this.amount_paid = amount_paid;
    }

    public void setAmount_pending(int amount_pending) {
        this.amount_pending = amount_pending;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setDate_registered(String date_registered) {
        this.date_registered = date_registered;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public void setEvent_price(int event_price) {
        this.event_price = event_price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegisteredby(String registeredby) {
        this.registeredby = registeredby;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean getUsed(){
        return used;
    }
}
