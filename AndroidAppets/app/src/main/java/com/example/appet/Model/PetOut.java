package com.example.appet.Model;

import java.util.List;
import java.util.UUID;

public class PetOut {
    private String id;
    private String name;
    private String image;
    private String type;
    private List<Tag> tags;
    private boolean isDeleted;

    public PetOut(String name, String type, String image, boolean isDeleted, List<Tag> tags) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.image = image;
        this.isDeleted = isDeleted;
        this.tags = tags;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
