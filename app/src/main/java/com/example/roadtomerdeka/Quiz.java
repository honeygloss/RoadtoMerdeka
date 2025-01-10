package com.example.roadtomerdeka;

import java.util.Map;

public class Quiz {
    private String quizId;
    private String title;
    private Map<String, Question> questions;

    // Default constructor required for calls to DataSnapshot.getValue(Quiz.class)
    public Quiz() { }

    public Quiz(String quizId, String title, Map<String, Question> questions) {
        this.quizId = quizId;
        this.title = title;
        this.questions = questions;
    }

    // Getters and setters
    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Map<String, Question> getQuestions() { return questions; }
    public void setQuestions(Map<String, Question> questions) { this.questions = questions; }
}
