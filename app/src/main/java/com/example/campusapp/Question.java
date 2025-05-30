package com.example.campusapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Question {
    private String question;
    private String correct_answer;
    private List<String> options;
    private String difficulty;

    // Getters with null checks
    public String getQuestion() {
        return question != null ? question : "";
    }

    public String getCorrectAnswer() {
        return correct_answer != null ? correct_answer : "";
    }

    public List<String> getOptions() {
        return options != null ? options : new ArrayList<>();
    }
}