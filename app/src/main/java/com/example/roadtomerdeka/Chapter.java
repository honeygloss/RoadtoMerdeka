package com.example.roadtomerdeka;

public class Chapter {
    private String id;                // Chapter ID
    private String quizId;            // Associated Quiz ID
    private String chapterHeader;
    private String title;
    private String description;
    private String imageResId;
    private String content;
    private String imageChapter;

    // Default Constructor (required for Firebase)
    public Chapter() {}

    // Parameterized Constructor
    public Chapter(String id, String quizId, String chapterHeader, String title, String description, String imageResId, String content, String imageChapter) {
        this.id = id;
        this.quizId = quizId;  // Initialize quizId
        this.chapterHeader = chapterHeader;
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.content = content;
        this.imageChapter = imageChapter;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getChapterHeader() {
        return chapterHeader;
    }

    public void setChapterHeader(String chapterHeader) {
        this.chapterHeader = chapterHeader;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageResId() {
        return imageResId;
    }

    public void setImageResId(String imageResId) {
        this.imageResId = imageResId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageChapter() {
        return imageChapter;
    }

    public void setImageChapter(String imageChapter) {
        this.imageChapter = imageChapter;
    }
}
