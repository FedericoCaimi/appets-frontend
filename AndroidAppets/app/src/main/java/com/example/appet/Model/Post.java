package com.example.appet.Model;

import java.util.List;

public class Post {
    private String postId;
    private String postTitle;
    private String postImage;// deberia ser string
    private String description;
    private String ubication;
    private int type;
    private String reporterPhone;
    private List<Tag> tags;

    public Post(String postId , String postTitle, String postImage, String description, String ubication,int type, List<Tag> tags, String reporterPhone){
        this.postId = postId;
        this.postTitle = postTitle;
        this.description = description;
        this.ubication = ubication;
        this.postImage = postImage;
        this.type = type;
        this.tags = tags;
        this.reporterPhone = reporterPhone;
    }

    public Post(){
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUbication() {
        return ubication;
    }

    public void setUbication(String ubication) {
        this.ubication = ubication;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getReporterPhone() {
        return reporterPhone;
    }

    public void setReporterPhone(String reporterPhone) {
        this.reporterPhone = reporterPhone;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}