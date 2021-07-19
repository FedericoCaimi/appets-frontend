package com.example.appet.Model;

import java.util.List;

public class User {
    private String id;
    private String userName;
    private String email;
    private String phone;
    private List<Pet> pets;
    private List<Post> posts;
    private boolean isDeleted;

    public User(String id, String userName, String email, String phone, List<Pet> pets, List<Post> posts, boolean isDeleted) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.pets = pets;
        this.posts = posts;
        this.isDeleted = isDeleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }
}
