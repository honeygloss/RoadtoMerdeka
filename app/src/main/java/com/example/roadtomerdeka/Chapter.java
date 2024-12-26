package com.example.roadtomerdeka;

public class Chapter {
    private String title;
    private boolean isLocked;

    public Chapter() {
        // Required empty constructor for Firebase
    }

    public Chapter(String title, boolean isLocked) {
        this.title = title;
        this.isLocked = isLocked;
    }

    public String getTitle() {
        return title;
    }

    public boolean isLocked() {
        return isLocked;
    }
}

