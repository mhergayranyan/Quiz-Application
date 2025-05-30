package com.example.campusapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.media.AudioAttributes;
import android.media.SoundPool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CountryFlagsQuizFragment extends Fragment {

    private TextView questionText, resultText, questionCounterText;
    private ImageView flagImage;
    private MaterialButton option1, option2, option3, option4;
    private ProgressBar progressBar;

    private List<QuestionModel> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int correctCount = 0;
//    private MaterialButton restartButton, endButton;

    private SoundPool soundPool;
    private int soundCorrect, soundWrong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();

        soundCorrect = soundPool.load(getContext(), R.raw.correct_sound, 1);
        soundWrong = soundPool.load(getContext(), R.raw.wrong_sound, 1);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_country_flags_quiz, container, false);

        questionText = view.findViewById(R.id.question_text);
        resultText = view.findViewById(R.id.result_text);
        questionCounterText = view.findViewById(R.id.question_counter);
        flagImage = view.findViewById(R.id.flag_image);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);
        progressBar = view.findViewById(R.id.progress_bar);
//        restartButton = view.findViewById(R.id.button_restart);
//        endButton = view.findViewById(R.id.button_end);

//        restartButton.setOnClickListener(v -> {
//            // Reset quiz state and restart
//            currentQuestionIndex = 0;
//            correctCount = 0;
//            NavHostFragment.findNavController(this)
//                    .navigate(R.id.action_countryFlagsQuizFragment_to_quizModesFragment);
//        });
//
//        endButton.setOnClickListener(v -> {
//            // End quiz — you can finish activity or navigate somewhere
//            Intent intent = new Intent(getActivity(), QuizFragment.class);
//            startActivity(intent);
//            requireActivity().finish(); // Optional: close current fragment's parent activity
//        });

        loadQuestionsFromJson();
        displayQuestion();

        return view;
    }

    private void loadQuestionsFromJson() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.country_flags_quiz);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray questionsArray = jsonObject.getJSONArray("questions");

            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject obj = questionsArray.getJSONObject(i);
                String question = obj.getString("question");
                String correctAnswer = obj.getString("correct_answer");
                String imageUrl = obj.getString("image_url").trim(); // trim to remove any extra spaces
                JSONArray optionsArray = obj.getJSONArray("options");
                List<String> options = new ArrayList<>();
                for (int j = 0; j < optionsArray.length(); j++) {
                    options.add(optionsArray.getString(j));
                }

                questionList.add(new QuestionModel(question, correctAnswer, options, imageUrl));
            }

            Collections.shuffle(questionList); // Optional: shuffle question order

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questionList.size()) {


            // Quiz finished
            resultText.setText("You answered " + correctCount + " out of " + questionList.size() + " correctly.");

            resultText.setVisibility(View.VISIBLE);

            // Show restart and end buttons
//            restartButton.setVisibility(View.VISIBLE);
//            endButton.setVisibility(View.VISIBLE);

            // Hide question and options
            questionText.setVisibility(View.GONE);
            flagImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            questionCounterText.setVisibility(View.GONE);
            option1.setVisibility(View.GONE);
            option2.setVisibility(View.GONE);
            option3.setVisibility(View.GONE);
            option4.setVisibility(View.GONE);


            return;
        }

        // Normal question display — show everything
        questionText.setVisibility(View.VISIBLE);
        flagImage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        questionCounterText.setVisibility(View.VISIBLE);
        option1.setVisibility(View.VISIBLE);
        option2.setVisibility(View.VISIBLE);
        option3.setVisibility(View.VISIBLE);
        option4.setVisibility(View.VISIBLE);

        resultText.setVisibility(View.GONE);
//        restartButton.setVisibility(View.GONE);
//        endButton.setVisibility(View.GONE);

        // rest of your existing displayQuestion code here...
        QuestionModel current = questionList.get(currentQuestionIndex);

        questionCounterText.setText("Question " + (currentQuestionIndex + 1) + "/" + questionList.size());
        progressBar.setProgress((int) (((currentQuestionIndex + 1) / (float) questionList.size()) * 100));

        questionText.setText(current.getQuestion());

        Glide.with(this)
                .load(current.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(flagImage);

        List<String> options = new ArrayList<>(current.getOptions());
        Collections.shuffle(options);

        option1.setText(options.get(0));
        option2.setText(options.get(1));
        option3.setText(options.get(2));
        option4.setText(options.get(3));

        enableOptions(true);

        View.OnClickListener listener = v -> {
            MaterialButton selected = (MaterialButton) v;
            checkAnswer(selected, current.getCorrectAnswer());
        };

        option1.setOnClickListener(listener);
        option2.setOnClickListener(listener);
        option3.setOnClickListener(listener);
        option4.setOnClickListener(listener);

        Glide.with(this)
                .load(current.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .override(320, 200)
                .into(flagImage);
    }


    private void checkAnswer(MaterialButton selected, String correctAnswer) {
        String userAnswer = selected.getText().toString();
        boolean isCorrect = userAnswer.equals(correctAnswer);

        if (isCorrect) {
            selected.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            correctCount++;
        } else {
            selected.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            // Optionally highlight the correct one
            for (MaterialButton btn : new MaterialButton[]{option1, option2, option3, option4}) {
                if (btn.getText().toString().equals(correctAnswer)) {
                    btn.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                    break;
                }
            }
        }

        enableOptions(false);

        new Handler().postDelayed(() -> {
            resetOptionColors();
            currentQuestionIndex++;
            displayQuestion();
        }, 1500);

        if (userAnswer.equals(correctAnswer)) {
            playCorrectSound();
            // Correct answer UI update
        } else {
            playWrongSound();
            // Wrong answer UI update
        }
    }

    private void enableOptions(boolean enable) {
        option1.setEnabled(enable);
        option2.setEnabled(enable);
        option3.setEnabled(enable);
        option4.setEnabled(enable);
    }

    private void resetOptionColors() {
        int white = getResources().getColor(android.R.color.white);
        option1.setBackgroundColor(white);
        option2.setBackgroundColor(white);
        option3.setBackgroundColor(white);
        option4.setBackgroundColor(white);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    private void playCorrectSound() {
        if (soundPool != null) {
            soundPool.play(soundCorrect, 1, 1, 0, 0, 1);
        }
    }

    private void playWrongSound() {
        if (soundPool != null) {
            soundPool.play(soundWrong, 1, 1, 0, 0, 1);
        }
    }
}
