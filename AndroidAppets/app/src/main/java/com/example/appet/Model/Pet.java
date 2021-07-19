package com.example.appet.Model;

import java.util.List;

public class Pet {
    private String id;
    private String name;
    private String type;
    private String image;
    private boolean isDeleted;
    private List<Tag> tags;
    private String ownerId;
    private List<Post> posts ;
    private String phone;

    public Pet(String id, String name, String type, String image, boolean isDeleted, List<Tag> tags, String ownerId, List<Post> posts, String phone) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.image = image;
        this.isDeleted = isDeleted;
        this.tags = tags;
        this.ownerId = ownerId;
        this.posts = posts;
        this.phone = phone;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
