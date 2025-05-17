package com.example.campusapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
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

public class TimerQuizFragment extends Fragment {

    private TextView questionCounter, questionText, timerText;
    private ImageView flagImage;
    private MaterialButton option1, option2, option3, option4;
    private List<Country> countries = new ArrayList<>();
    private List<QuizQuestion> questions = new ArrayList<>();
    private int currentQuestion = 0;
    private int score = 0;
    private CountDownTimer timer;
    private long timeLeftInMillis = 90000; // 1.5 minutes

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_quiz, container, false);

        // Initialize views
        questionCounter = view.findViewById(R.id.questionCounter);
        questionText = view.findViewById(R.id.questionText);
        timerText = view.findViewById(R.id.timerText);
        flagImage = view.findViewById(R.id.flagImage);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);

        loadCountriesFromFirebase();
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

    private void loadCountriesFromFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot countrySnapshot : snapshot.getChildren()) {
                    Country country = countrySnapshot.getValue(Country.class);
                    if (country != null) {
                        countries.add(country);
                    }
                }
                prepareQuestions();
                showNextQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load questions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prepareQuestions() {
        // Create capital questions
        for (Country country : countries) {
            if (country.getCapital() != null) {
                questions.add(new QuizQuestion(
                        "What is the capital of " + country.getName() + "?",
                        country.getCapital(),
                        null,
                        "capital"
                ));
            }
        }

        // Create flag questions
        for (Country country : countries) {
            if (country.getFlag() != null) {
                questions.add(new QuizQuestion(
                        "Which country does this flag belong to?",
                        country.getName(),
                        country.getFlag(),
                        "flag"
                ));
            }
        }

        Collections.shuffle(questions);
        questionCounter.setText("1/" + questions.size());
    }

    private void showNextQuestion() {
        if (currentQuestion < questions.size() && timeLeftInMillis > 0) {
            QuizQuestion currentQ = questions.get(currentQuestion);
            questionCounter.setText((currentQuestion + 1) + "/" + questions.size());

            if (currentQ.getType().equals("flag")) {
                flagImage.setVisibility(View.VISIBLE);
                Glide.with(this).load(currentQ.getImageUrl()).into(flagImage);
                questionText.setText(currentQ.getQuestionText());
            } else {
                flagImage.setVisibility(View.GONE);
                questionText.setText(currentQ.getQuestionText());
            }

            List<String> options = generateOptions(currentQ);
            option1.setText(options.get(0));
            option2.setText(options.get(1));
            option3.setText(options.get(2));
            option4.setText(options.get(3));

            resetButtonColors();
            setButtonClickListeners(currentQ.getCorrectAnswer());
        } else {
            showResults();
        }
    }

    private List<String> generateOptions(QuizQuestion question) {
        List<String> options = new ArrayList<>();
        options.add(question.getCorrectAnswer());

        Random random = new Random();
        while (options.size() < 4) {
            Country randomCountry = countries.get(random.nextInt(countries.size()));
            String randomOption = question.getType().equals("flag")
                    ? randomCountry.getName()
                    : randomCountry.getCapital();

            if (!options.contains(randomOption)) {
                options.add(randomOption);
            }
        }
        Collections.shuffle(options);
        return options;
    }

    private void resetButtonColors() {
        option1.setBackgroundColor(Color.parseColor("#3949AB"));
        option2.setBackgroundColor(Color.parseColor("#3949AB"));
        option3.setBackgroundColor(Color.parseColor("#3949AB"));
        option4.setBackgroundColor(Color.parseColor("#3949AB"));

        option1.setEnabled(true);
        option2.setEnabled(true);
        option3.setEnabled(true);
        option4.setEnabled(true);
    }

    private void setButtonClickListeners(final String correctAnswer) {
        View.OnClickListener answerListener = v -> {
            MaterialButton selectedButton = (MaterialButton) v;
            boolean isCorrect = selectedButton.getText().toString().equals(correctAnswer);

            // Disable all buttons
            option1.setEnabled(false);
            option2.setEnabled(false);
            option3.setEnabled(false);
            option4.setEnabled(false);

            // Highlight answers
            if (isCorrect) {
                selectedButton.setBackgroundColor(Color.GREEN);
                score++;
            } else {
                selectedButton.setBackgroundColor(Color.RED);
                highlightCorrectAnswer(correctAnswer);
            }

            // Move to next question after delay
            new Handler().postDelayed(() -> {
                currentQuestion++;
                showNextQuestion();
            }, 1500);
        };

        option1.setOnClickListener(answerListener);
        option2.setOnClickListener(answerListener);
        option3.setOnClickListener(answerListener);
        option4.setOnClickListener(answerListener);
    }

    private void highlightCorrectAnswer(String correctAnswer) {
        if (option1.getText().toString().equals(correctAnswer)) {
            option1.setBackgroundColor(Color.GREEN);
        } else if (option2.getText().toString().equals(correctAnswer)) {
            option2.setBackgroundColor(Color.GREEN);
        } else if (option3.getText().toString().equals(correctAnswer)) {
            option3.setBackgroundColor(Color.GREEN);
        } else if (option4.getText().toString().equals(correctAnswer)) {
            option4.setBackgroundColor(Color.GREEN);
        }
    }

    private void showResults() {
        if (timer != null) {
            timer.cancel();
        }

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Time's Up!")
                .setMessage("You answered " + score + " questions correctly!")
                .setPositiveButton("Done", (dialog, which) -> {
                    requireActivity().getSupportFragmentManager().popBackStack();
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

class QuizQuestion {
    private String questionText;
    private String correctAnswer;
    private String imageUrl;
    private String type;

    public QuizQuestion(String questionText, String correctAnswer, String imageUrl, String type) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    public String getQuestionText() { return questionText; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getImageUrl() { return imageUrl; }
    public String getType() { return type; }
}