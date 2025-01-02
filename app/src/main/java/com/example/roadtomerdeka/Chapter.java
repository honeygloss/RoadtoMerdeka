package com.example.roadtomerdeka;
public class Chapter {
    private int id;
    private String title;
    private String description;
    private int imageResId;
    private boolean isLocked;

    // Constructor
    public Chapter(int id, String title, String description, int imageResId, boolean isLocked) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.isLocked = isLocked;
    }

    // Getters
    public int getId() { return id; }
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
