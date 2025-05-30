package com.example.campusapp;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CapitalCitiesQuizFragment extends Fragment {

    private TextView questionText, questionCounterText, resultText;
    private Button[] optionButtons = new Button[4];
    private ProgressBar progressBar;

    private List<QuestionModel> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private MediaPlayer correctSound, wrongSound;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private MaterialButton restartQuizButton;
    private MaterialButton endQuizButton;
    private Set<Integer> answeredQuestions = new HashSet<>();
    private List<QuestionModel> questions = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capital_cities_quiz, container, false);






        // UI References
        questionText = view.findViewById(R.id.question_text);
        questionCounterText = view.findViewById(R.id.question_counter);
        resultText = view.findViewById(R.id.result_text);
        progressBar = view.findViewById(R.id.progress_bar);

        optionButtons[0] = view.findViewById(R.id.option1);
        optionButtons[1] = view.findViewById(R.id.option2);
        optionButtons[2] = view.findViewById(R.id.option3);
        optionButtons[3] = view.findViewById(R.id.option4);

        correctSound = MediaPlayer.create(getContext(), R.raw.correct_sound);
        wrongSound = MediaPlayer.create(getContext(), R.raw.wrong_sound);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("results");

        // Set click listeners once
        for (Button btn : optionButtons) {
            btn.setOnClickListener(view1 -> checkAnswer(((Button) view1).getText().toString(), (Button) view1));
        }

        loadQuestionsFromJson();
        displayQuestion();

        return view;
    }

    private void loadQuestionsFromJson() {
        questionList = new ArrayList<>();
        try {
            InputStream is = getResources().openRawResource(R.raw.capital_quiz);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String jsonStr = new String(buffer, StandardCharsets.UTF_8);
            JSONArray questionsArray = new JSONObject(jsonStr).getJSONArray("questions");

            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject qObj = questionsArray.getJSONObject(i);
                String question = qObj.getString("question");
                String correctAnswer = qObj.getString("correct_answer");

                List<String> options = new ArrayList<>();
                JSONArray optionsArray = qObj.getJSONArray("options");
                for (int j = 0; j < optionsArray.length(); j++) {
                    options.add(optionsArray.getString(j));
                }

                Collections.shuffle(options);
                questionList.add(new QuestionModel(question, correctAnswer, options));
            }

            Collections.shuffle(questionList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            resetOptionButtons();

            QuestionModel current = questionList.get(currentQuestionIndex);
            questionText.setText(current.getQuestion());
            questionCounterText.setText("Question " + (currentQuestionIndex + 1) + "/" + questionList.size());
            progressBar.setProgress((int) (((double) (currentQuestionIndex + 1) / questionList.size()) * 100));

            List<String> options = current.getOptions();
            for (int i = 0; i < optionButtons.length; i++) {
                optionButtons[i].setText(options.get(i));
                optionButtons[i].setEnabled(true);
            }
        } else {
            showResult();
        }
    }

    private void resetOptionButtons() {
        int white = Color.WHITE;
        int black = Color.BLACK;

        for (Button btn : optionButtons) {
            btn.setBackgroundColor(white);
            btn.setTextColor(black);
            btn.setEnabled(true);
        }
    }

    private void checkAnswer(String selectedAnswer, Button selectedButton) {
        QuestionModel current = questionList.get(currentQuestionIndex);

        for (Button btn : optionButtons) {
            btn.setEnabled(false);
        }

        if (selectedAnswer.equals(current.getCorrectAnswer())) {
            selectedButton.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
            correctSound.start();
            score++;
        } else {
            selectedButton.setBackgroundColor(Color.parseColor("#F44336")); // Red
            for (Button btn : optionButtons) {
                if (btn.getText().toString().equals(current.getCorrectAnswer())) {
                    btn.setBackgroundColor(Color.parseColor("#4CAF50"));
                }
            }
            wrongSound.start();
        }

        selectedButton.postDelayed(() -> {
            currentQuestionIndex++;
            displayQuestion();
        }, 1000);
    }

    private void showResult() {
        for (Button btn : optionButtons) {
            btn.setVisibility(View.GONE);
        }

        questionText.setVisibility(View.GONE);
        questionCounterText.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);





        resultText.setVisibility(View.VISIBLE);
        resultText.setText("You answered " + score + " out of " + questionList.size() + " correctly.");
        resultText.setTextColor(Color.BLACK);
        resultText.setTextSize(20);
        resultText.setTypeface(null, android.graphics.Typeface.BOLD);
        resultText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            databaseReference.child(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    long totalQuizzes = 0;
                    long highestScore = 0;

                    if (snapshot.exists()) {
                        if (snapshot.child("totalQuizzes").getValue() != null)
                            totalQuizzes = snapshot.child("totalQuizzes").getValue(Long.class);

                        if (snapshot.child("highestScore").getValue() != null)
                            highestScore = snapshot.child("highestScore").getValue(Long.class);
                    }

                    totalQuizzes += 1;
                    if (score > highestScore) {
                        highestScore = score;
                    }

                    databaseReference.child(uid).child("totalQuizzes").setValue(totalQuizzes);
                    databaseReference.child(uid).child("highestScore").setValue(highestScore);
                }
            });
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (correctSound != null) correctSound.release();
        if (wrongSound != null) wrongSound.release();
    }

    // Inner class
    static class QuestionModel {
        private final String question;
        private final String correctAnswer;
        private final List<String> options;

        public QuestionModel(String question, String correctAnswer, List<String> options) {
            this.question = question;
            this.correctAnswer = correctAnswer;
            this.options = options;
        }

        public String getQuestion() {
            return question;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public List<String> getOptions() {
            return options;
        }
    }
}
