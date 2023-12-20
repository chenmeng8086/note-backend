package com.example.demo.Controller;

public class User {
    private String name;
    private String chineseName;
    private int id;
    private Boolean admin;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public String getName() {
        return name;
    }

    public String getChineseName() {
        return chineseName;
    }

    public int getId() {
        return id;
    }

    public User(String name, String chineseName, int id) {
        this.name = name;
        this.chineseName = chineseName;
        this.id = id;
    }

    public User() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
