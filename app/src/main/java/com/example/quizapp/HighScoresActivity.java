package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoresActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HighScoreAdapter adapter;
    private List<ResulteActivity.HighScore> highScoreList = new ArrayList<>();
    private DatabaseReference highScoresRef;
    private String selectedCategory;
    private TextView textViewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        selectedCategory = getIntent().getStringExtra("category");

        recyclerView = findViewById(R.id.recyclerView);
        textViewCategory = findViewById(R.id.textViewCategory);
        textViewCategory.setText(selectedCategory); // Kategoriyi ayarlayın

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HighScoreAdapter(highScoreList);
        recyclerView.setAdapter(adapter);

        highScoresRef = FirebaseDatabase.getInstance().getReference("highscores");

        fetchHighScores();
    }

    private void fetchHighScores() {
        highScoresRef.orderByChild("category").equalTo(selectedCategory)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        highScoreList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            ResulteActivity.HighScore highScore = postSnapshot.getValue(ResulteActivity.HighScore.class);
                            if (highScore != null) {
                                highScoreList.add(highScore);
                            }
                        }
                        // Skorlara göre sıralama yap ve ters çevir
                        Collections.sort(highScoreList, (hs1, hs2) -> Integer.compare(hs2.score, hs1.score));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle possible errors.
                    }
                });
    }
}
