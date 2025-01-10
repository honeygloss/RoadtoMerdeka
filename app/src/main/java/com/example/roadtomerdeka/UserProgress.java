package com.example.roadtomerdeka;

import java.util.Map;

public class UserProgress {
    private Map<String, Boolean> readChapters;
    private Map<String, Boolean> completedQuizzes;

    // Default constructor
    public UserProgress() { }

    public Map<String, Boolean> getReadChapters() { return readChapters; }
    public void setReadChapters(Map<String, Boolean> readChapters) { this.readChapters = readChapters; }

    public Map<String, Boolean> getCompletedQuizzes() { return completedQuizzes; }
    public void setCompletedQuizzes(Map<String, Boolean> completedQuizzes) { this.completedQuizzes = completedQuizzes; }
}
