package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String selectedCategory = "";
    private ImageButton btnGeography, btnScience, btnSports, btnCinema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGeography = findViewById(R.id.btn_geography);
        btnScience = findViewById(R.id.btn_science);
        btnSports = findViewById(R.id.btn_sports);
        btnCinema = findViewById(R.id.btn_cinema);

        btnGeography.setOnClickListener(view -> selectCategory("geography", view));
        btnScience.setOnClickListener(view -> selectCategory("science", view));
        btnSports.setOnClickListener(view -> selectCategory("sports", view));
        btnCinema.setOnClickListener(view -> selectCategory("cinema", view));

        findViewById(R.id.btn_play).setOnClickListener(view -> {
            if (!selectedCategory.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                intent.putExtra("category", selectedCategory);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Lütfen bir kategori seçin", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_exit).setOnClickListener(view -> {
            finish();
            System.exit(0);
        });
    }

    private void selectCategory(String category, View selectedView) {
        selectedCategory = category;
        resetButtonBackgrounds();
        selectedView.setBackgroundResource(R.drawable.background_btn_selected);
    }

    private void resetButtonBackgrounds() {
        btnGeography.setBackgroundResource(R.drawable.background_btn);
        btnScience.setBackgroundResource(R.drawable.background_btn);
        btnSports.setBackgroundResource(R.drawable.background_btn);
        btnCinema.setBackgroundResource(R.drawable.background_btn);
    }
}
