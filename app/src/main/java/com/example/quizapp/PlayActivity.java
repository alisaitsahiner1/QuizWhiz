package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.quizapp.databinding.ActivityPlayBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private List<Question> questionList = new ArrayList<>();
    private TextView cpt_question, text_question;
    private String selectedCategory;
    private int currentQuestion = 0;
    private int scorePlayer = 0;
    private boolean isClickBtn = false;
    private String valueChoose = "";
    private View btn_click;
    private String correctAnswerKey = "";
    private ActivityPlayBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Intent ile gelen kategori bilgisini al
        Intent intent = getIntent();
        selectedCategory = intent.getStringExtra("category");

        cpt_question = binding.cptQuestion;
        text_question = binding.textQuestion;

        binding.imageBack.setOnClickListener(a -> finish());

        mDatabase = FirebaseDatabase.getInstance().getReference("questions").child(selectedCategory);

        loadQuizFragment();
        fetchQuestions();

        binding.btnNext.setOnClickListener(view -> {
            if (isClickBtn) {
                isClickBtn = false;
                if (!valueChoose.equals(correctAnswerKey)) {
                    Toast.makeText(PlayActivity.this, "Yanlış", Toast.LENGTH_LONG).show();
                    btn_click.setBackgroundResource(R.drawable.background_btn_erreur);
                } else {
                    Toast.makeText(PlayActivity.this, "Doğru", Toast.LENGTH_LONG).show();
                    btn_click.setBackgroundResource(R.drawable.background_btn_correct);
                    scorePlayer++;
                }
                new Handler().postDelayed(() -> {
                    if (currentQuestion != questionList.size() - 1) {
                        currentQuestion++;
                        fillData();
                        valueChoose = "";
                        resetButtonColors();
                    } else {
                        int scoreOutOf100 = (scorePlayer * 100) / questionList.size();
                        Intent resultIntent = new Intent(PlayActivity.this, ResulteActivity.class);
                        resultIntent.putExtra("Result", scoreOutOf100);
                        resultIntent.putExtra("category", selectedCategory);
                        startActivity(resultIntent);
                        finish();
                    }
                }, 2000);
            } else {
                Toast.makeText(PlayActivity.this, "Bir seçim yapmalısınız", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadQuizFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        QuizFragment quizFragment = new QuizFragment();
        fragmentTransaction.replace(R.id.fragment_container, quizFragment);
        fragmentTransaction.commit();
    }

    private void fetchQuestions() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questionList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Question question = postSnapshot.getValue(Question.class);
                    if (question != null) {
                        questionList.add(question);
                    }
                }
                if (!questionList.isEmpty()) {
                    fillData();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PlayActivity.this, "Veri yüklenemedi: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fillData() {
        cpt_question.setText((currentQuestion + 1) + "/" + questionList.size());
        text_question.setText(questionList.get(currentQuestion).getQuestion());
        QuizFragment fragment = (QuizFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.fillData(questionList.get(currentQuestion));
        }
        correctAnswerKey = questionList.get(currentQuestion).getCorrect();
    }

    public void updateValueChoose(String value, View button) {
        valueChoose = value;
        btn_click = button;
        isClickBtn = true;
    }

    private void resetButtonColors() {
        QuizFragment fragment = (QuizFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.resetButtonColors();
        }
    }

    public static class Question {
        private String question;
        private Map<String, String> choices;
        private String correct;

        public Question() {
            // Default constructor required for calls to DataSnapshot.getValue(Question.class)
        }

        public Question(String question, Map<String, String> choices, String correct) {
            this.question = question;
            this.choices = choices;
            this.correct = correct;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public Map<String, String> getChoices() {
            return choices;
        }

        public void setChoices(Map<String, String> choices) {
            this.choices = choices;
        }

        public String getCorrect() {
            return correct;
        }

        public void setCorrect(String correct) {
            this.correct = correct;
        }
    }
}
