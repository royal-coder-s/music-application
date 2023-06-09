package com.example.musicapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;

public class SongSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_selection_activity);

        Button song1Button = findViewById(R.id.song1Button);
        Button song2Button = findViewById(R.id.song2Button);
        Button song3Button = findViewById(R.id.song3Button);

        song1Button.setOnClickListener(v -> {
            Intent intent = new Intent(SongSelectionActivity.this, MainActivity.class);
            intent.putExtra("songIndex", 0);
            startActivity(intent);
        });

        song2Button.setOnClickListener(v -> {
            // Launch MainActivity
            Intent intent = new Intent(SongSelectionActivity.this, MainActivity.class);
            intent.putExtra("songIndex", 1);
            startActivity(intent);
        });

        song3Button.setOnClickListener(v -> {
            // Launch MainActivity
            Intent intent = new Intent(SongSelectionActivity.this, MainActivity.class);
            intent.putExtra("songIndex", 3);
            startActivity(intent);
        });
    }
}
