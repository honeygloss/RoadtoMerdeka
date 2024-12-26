package com.example.roadtomerdeka;
public class Chapter {
    private String title;
    private String description;
    private int imageResId;
    private boolean isLocked;

    // Constructor
    public Chapter(String title, String description, int imageResId, boolean isLocked) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.isLocked = isLocked;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public boolean isLocked() {
        return isLocked;
    }
}
