package com.example.project_3;

public class News {
    private String title;
    private String content;
    private String imageUrl;

    public News(String title, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;

    }

    public String getImageUrl() {
        return imageUrl;
    }
}
