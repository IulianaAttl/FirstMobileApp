package com.example.notesapp;

//user class
public class User {
    //declare the variables
    private String name;
    private String phone;

    //empty constructor
    public User(){

    }

    //constructor
    public User(String name, String phone){
        this.name = name;
        this.phone = phone;
    }

    //getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
