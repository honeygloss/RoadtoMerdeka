package com.example.roadtomerdeka;

public class Chapter {
    private String id;
    private String chapterHeader;
    private String title;
    private String description;
    private String imageResId;
    private String content;
    private String imageChapter;

    // Constructor
    public Chapter() {
        // Required for Firebase
    }

    public Chapter(String id, String chapterHeader, String title, String description, String imageResId, String content, String imageChapter) {
        this.id = id;
        this.chapterHeader = chapterHeader;
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.content = content;
        this.imageChapter = imageChapter;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getChapterHeader() {
        return chapterHeader;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageResId() {
        return imageResId;
    }

    public String getContent() {
        return content;
    }

    public String getImageChapter() {
        return imageChapter;
    }
}
