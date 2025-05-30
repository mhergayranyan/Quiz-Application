package com.example.campusapp;

import java.util.ArrayList;
import java.util.List;

public class QuizData {
    private List<Question> questions;

    // Getters
    public List<Question> getQuestions() {
        return questions != null ? questions : new ArrayList<>();
    }
}