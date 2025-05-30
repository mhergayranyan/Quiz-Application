package com.example.campusapp;

import java.util.List;

public class QuestionModel {
    private final String question;
    private final String correctAnswer;
    private final List<String> options;
    private final String imageUrl;

    public QuestionModel(String question, String correctAnswer, List<String> options, String imageUrl) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.options = options;
        this.imageUrl = imageUrl;
    }

    public String getQuestion() { return question; }
    public String getCorrectAnswer() { return correctAnswer; }
    public List<String> getOptions() { return options; }
    public String getImageUrl() { return imageUrl; }
}
