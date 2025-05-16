package com.example.campusapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GeographyTimerChallengeFragment extends Fragment {

    private TextView questionCounter, questionText, timerText;
    private ImageView flagImage;
    private Button option1, option2, option3, option4;
    private List<Country> countries = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();
    private int currentQuestion = 0;
    private int score = 0;
    private CountDownTimer timer;
    private long timeLeftInMillis = 90000; // 1.5 minutes

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geography_timer_challenge, container, false);

        questionCounter = view.findViewById(R.id.questionCounter);
        questionText = view.findViewById(R.id.questionText);
        timerText = view.findViewById(R.id.timerText);
        flagImage = view.findViewById(R.id.flagImage);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);

        loadQuestionsFromFirebase();
        startTimer();

        return view;
    }

    private void startTimer() {
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                timerText.setText("00:00");
                showResults();
            }
        }.start();
    }

    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerText.setText(timeLeftFormatted);
    }

    private void loadQuestionsFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("countries");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Country country = snapshot.getValue(Country.class);
                    if (country != null) {
                        countries.add(country);
                    }
                }
                prepareQuestions();
                showNextQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void prepareQuestions() {
        // Create capital questions
        for (Country country : countries) {
            questions.add(new Question(
                    "What is the capital of " + country.getName() + "?",
                    country.getCapital(),
                    null,
                    "capital"
            ));
        }

        // Create flag questions (only for countries with flag URLs)
        for (Country country : countries) {
            if (country.getFlagUrl() != null) {
                questions.add(new Question(
                        "Which country's flag is this?",
                        country.getName(),
                        country.getFlagUrl(),
                        "flag"
                ));
            }
        }

        Collections.shuffle(questions);
        questionCounter.setText("1/" + questions.size());
    }

    private void showNextQuestion() {
        if (currentQuestion < questions.size() && timeLeftInMillis > 0) {
            Question currentQ = questions.get(currentQuestion);
            questionCounter.setText((currentQuestion + 1) + "/" + questions.size());

            if (currentQ.getType().equals("flag")) {
                flagImage.setVisibility(View.VISIBLE);
                Glide.with(this).load(currentQ.getImageUrl()).into(flagImage);
                questionText.setText(currentQ.getQuestionText());
            } else {
                flagImage.setVisibility(View.GONE);
                questionText.setText(currentQ.getQuestionText());
            }

            // Get options
            List<String> options = new ArrayList<>();
            options.add(currentQ.getCorrectAnswer());

            Random random = new Random();
            while (options.size() < 4) {
                if (currentQ.getType().equals("flag")) {
                    // For flag questions, add other country names
                    int randomIndex = random.nextInt(countries.size());
                    String randomName = countries.get(randomIndex).getName();
                    if (!options.contains(randomName)) {
                        options.add(randomName);
                    }
                } else {
                    // For capital questions, add other capitals
                    int randomIndex = random.nextInt(countries.size());
                    String randomCapital = countries.get(randomIndex).getCapital();
                    if (!options.contains(randomCapital)) {
                        options.add(randomCapital);
                    }
                }
            }

            Collections.shuffle(options);

            option1.setText(options.get(0));
            option2.setText(options.get(1));
            option3.setText(options.get(2));
            option4.setText(options.get(3));

            option1.setBackgroundTintList(null);
            option2.setBackgroundTintList(null);
            option3.setBackgroundTintList(null);
            option4.setBackgroundTintList(null);

            option1.setOnClickListener(v -> checkAnswer(option1, currentQ.getCorrectAnswer()));
            option2.setOnClickListener(v -> checkAnswer(option2, currentQ.getCorrectAnswer()));
            option3.setOnClickListener(v -> checkAnswer(option3, currentQ.getCorrectAnswer()));
            option4.setOnClickListener(v -> checkAnswer(option4, currentQ.getCorrectAnswer()));
        } else {
            // Quiz finished
            showResults();
        }
    }

    private void checkAnswer(Button selectedButton, String correctAnswer) {
        String selectedAnswer = selectedButton.getText().toString();

        if (selectedAnswer.equals(correctAnswer)) {
            selectedButton.setBackgroundColor(Color.GREEN);
            score++;
        } else {
            selectedButton.setBackgroundColor(Color.RED);
            // Highlight correct answer
            if (option1.getText().toString().equals(correctAnswer)) option1.setBackgroundColor(Color.GREEN);
            if (option2.getText().toString().equals(correctAnswer)) option2.setBackgroundColor(Color.GREEN);
            if (option3.getText().toString().equals(correctAnswer)) option3.setBackgroundColor(Color.GREEN);
            if (option4.getText().toString().equals(correctAnswer)) option4.setBackgroundColor(Color.GREEN);
        }

        // Disable all buttons after answer is selected
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false);

        // Move to next question after delay
        option1.postDelayed(() -> {
            currentQuestion++;
            showNextQuestion();
        }, 1500);
    }

    private void showResults() {
        if (timer != null) {
            timer.cancel();
        }

        // Show results dialog
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Time's Up!")
                .setMessage("Your score: " + score + " correct answers")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Go back to quiz selection
                    getParentFragmentManager().popBackStack();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}

class Question {
    private String questionText;
    private String correctAnswer;
    private String imageUrl;
    private String type;

    public Question(String questionText, String correctAnswer, String imageUrl, String type) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getType() {
        return type;
    }
}