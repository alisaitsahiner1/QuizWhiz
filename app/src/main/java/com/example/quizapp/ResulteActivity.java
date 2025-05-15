package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResulteActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editTextName, editTextSurname;
    private Button btnSubmit, btnRestart, btnHighScores;
    private int score;
    private String selectedCategory;

    private static final String CHANNEL_ID = "quiz_high_score_channel";
    private DatabaseReference highScoresRef;
    private static final int NOTIFICATION_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resulte);

        textView = findViewById(R.id.textView);
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        btnSubmit = findViewById(R.id.btn_submit);
        btnRestart = findViewById(R.id.btn_restart);
        btnHighScores = findViewById(R.id.btn_highscores);

        score = getIntent().getIntExtra("Result", 0);
        selectedCategory = getIntent().getStringExtra("category");

        textView.setText("Skor : " + score);

        btnRestart.setOnClickListener(restart -> {
            Intent intent = new Intent(ResulteActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnSubmit.setOnClickListener(view -> {
            showConfirmationDialog();
        });

        btnHighScores.setOnClickListener(view -> {
            Intent intent = new Intent(ResulteActivity.this, HighScoresActivity.class);
            intent.putExtra("category", selectedCategory);  // Kategoriyi HighScoresActivity'ye iletin
            startActivity(intent);
        });

        highScoresRef = FirebaseDatabase.getInstance().getReference("highscores");

        createNotificationChannel();

        // İzin kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }

        // Mevcut en yüksek skoru oku ve göster
        SharedPreferences sharedPref = getSharedPreferences("com.example.quizapp.PREFERENCE_FILE_KEY", MODE_PRIVATE);
        int highScore = sharedPref.getInt("high_score_" + selectedCategory, 0); // Varsayılan değer 0
        if (score > highScore) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("high_score_" + selectedCategory, score);
            editor.apply();
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kaydet")
                .setMessage("Kaydetmek istediğinize emin misiniz?")
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkAndSaveHighScore();
                    }
                })
                .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void checkAndSaveHighScore() {
        highScoresRef.orderByChild("category").equalTo(selectedCategory)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isNewHighScore = true;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ResulteActivity.HighScore highScore = snapshot.getValue(ResulteActivity.HighScore.class);
                            if (highScore != null && highScore.score >= score) {
                                isNewHighScore = false;
                                break;
                            }
                        }

                        saveHighScore();

                        if (isNewHighScore) {
                            sendHighScoreNotification();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ResulteActivity.this, "Veri yüklenemedi: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveHighScore() {
        String name = editTextName.getText().toString();
        String surname = editTextSurname.getText().toString();
        HighScore highScore = new HighScore(name, surname, score, selectedCategory);

        highScoresRef.push().setValue(highScore).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Skor kaydedildi", Toast.LENGTH_SHORT).show();
                // EditText'leri temizle
                editTextName.setText("");
                editTextSurname.setText("");
                // Submit butonunu devre dışı bırak
                btnSubmit.setEnabled(false);
            } else {
                Toast.makeText(this, "Skor kaydedilemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendHighScoreNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)  // Bildirim simgesi burada ayarlandı.
                        .setContentTitle("Tebrikler!")
                        .setContentText("Tebrikler, " + selectedCategory + " dalında en yüksek skoru kazandınız!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(1, builder.build());
            } else {
                Toast.makeText(this, "Bildirim izni verilmedi, bildirim gönderilemedi.", Toast.LENGTH_SHORT).show();
            }
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)  // Bildirim simgesi burada ayarlandı.
                    .setContentTitle("Tebrikler!")
                    .setContentText("Tebrikler, " + selectedCategory + " dalında en yüksek skoru kazandınız!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "High Score Channel";
            String description = "Channel for high score notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bildirim izni verildi.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bildirim izni reddedildi.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class HighScore {
        public String name;
        public String surname;
        public int score;
        public String category;

        public HighScore() {
            // Default constructor required for calls to DataSnapshot.getValue(HighScore.class)
        }

        public HighScore(String name, String surname, int score, String category) {
            this.name = name;
            this.surname = surname;
            this.score = score;
            this.category = category;
        }
    }
}
