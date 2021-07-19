package com.example.appet.Model;

import java.util.List;

public class FoundModel {
    private String description;
    private String title;
    private String ubication;
    private String image;
    private int type;
    private List<Tag> tags;
    private String reporterPhone;

    public FoundModel(String description, String title, String ubication, String image, int type, List<Tag> tags, String reporterPhone) {
        this.description = description;
        this.title = title;
        this.ubication = ubication;
        this.image = image;
        this.type = type;
        this.tags = tags;
        this.reporterPhone = reporterPhone;
    }
}