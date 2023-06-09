package com.example.musicapplication;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageButton playButton, previousButton, nextButton, shuffleButton, repeatButton;
    private SeekBar seekBar;
    private TextView songTitle, artist;
    private ImageView albumCoverImageView;
    private TextView currentTimeTextView, totalTimeTextView;
    private Handler handler;
    private Runnable runnable;
    private List<Integer> songList;
    private List<Drawable> albumCovers;
    private int currentIndex = 0;
    private boolean isShuffleEnabled = false;
    private boolean isRepeatEnabled = false;
    private boolean isSeeking = false;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = findViewById(R.id.playButton);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        shuffleButton = findViewById(R.id.shuffleButton);
        repeatButton = findViewById(R.id.repeatButton);
        seekBar = findViewById(R.id.seekBar);
        albumCoverImageView = findViewById(R.id.albumCover);
        songTitle = findViewById(R.id.songTitle);
        artist = findViewById(R.id.artist);
        currentTimeTextView = findViewById(R.id.currentTimeTextView);
        totalTimeTextView = findViewById(R.id.totalTimeTextView);

        songList = new ArrayList<>();
        songList.add(R.raw.rings);
        songList.add(R.raw.attention);
        songList.add(R.raw.dance);

        albumCovers = new ArrayList<>();
        albumCovers.add(getResources().getDrawable(R.drawable.ariana));
        albumCovers.add(getResources().getDrawable(R.drawable.charlie));
        albumCovers.add(getResources().getDrawable(R.drawable.tones));

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> {
            if (isRepeatEnabled) {
                playSong(currentIndex);
            } else if (isShuffleEnabled) {
                playRandomSong();
            } else {
                playNextSong();
            }
        });

        seekBar.setMax(0);
        handler = new Handler();

        playButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                pauseSong();
            } else {
                playSong(currentIndex);
            }
        });

        previousButton.setOnClickListener(v -> playPreviousSong());

        nextButton.setOnClickListener(v -> {
            if (isShuffleEnabled) {
                playRandomSong();
            } else {
                playNextSong();
            }
        });

        shuffleButton.setOnClickListener(v -> toggleShuffle());

        repeatButton.setOnClickListener(v -> toggleRepeat());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    updateCurrentTime();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseSong();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);

                if (mediaPlayer.isPlaying()) {
                    playSong(currentIndex);
                } else {
                    updateCurrentTime();
                }
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("songIndex")) {
            int songIndex = intent.getIntExtra("songIndex", 0);
            playSong(songIndex);
        } else {
            playSong(currentIndex);
        }

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void playSong(int songIndex) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        mediaPlayer = MediaPlayer.create(this, songList.get(songIndex));

        seekBar.setMax(mediaPlayer.getDuration());

        mediaPlayer.start();
        playButton.setImageResource(R.drawable.pause);

        albumCoverImageView.setImageDrawable(albumCovers.get(songIndex));

        updateSongDetails(songIndex);
        updateProgress();
    }

    private void pauseSong() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playButton.setImageResource(R.drawable.play);
        }
    }

    private void playPreviousSong() {
        if (isShuffleEnabled) {
            playRandomSong();
        } else {
            currentIndex = (currentIndex - 1 + songList.size()) % songList.size();
            playSong(currentIndex);
        }
    }

    private void playNextSong() {
        if (isShuffleEnabled) {
            playRandomSong();
        } else {
            currentIndex = (currentIndex + 1) % songList.size();
            playSong(currentIndex);
        }
    }

    private void playRandomSong() {
        Random random = new Random();
        currentIndex = random.nextInt(songList.size());
        playSong(currentIndex);
    }

    private void toggleShuffle() {
        isShuffleEnabled = !isShuffleEnabled;
        if (isShuffleEnabled) {
            shuffleButton.setImageResource(R.drawable.shuffle_on);
            Toast.makeText(this, "Shuffle mode enabled", Toast.LENGTH_SHORT).show();
        } else {
            shuffleButton.setImageResource(R.drawable.shuffle);
            Toast.makeText(this, "Shuffle mode disabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleRepeat() {
        isRepeatEnabled = !isRepeatEnabled;
        if (isRepeatEnabled) {
            repeatButton.setImageResource(R.drawable.repeat_on);
            Toast.makeText(this, "Repeat mode enabled", Toast.LENGTH_SHORT).show();
        } else {
            repeatButton.setImageResource(R.drawable.repeat);
            Toast.makeText(this, "Repeat mode disabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProgress() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        handler.postDelayed(runnable = () -> {
            if (mediaPlayer.isPlaying()) {
                updateProgress();
                updateCurrentTime();
            }
        }, 1000);
    }

    private void updateCurrentTime() {
        int currentTime = mediaPlayer.getCurrentPosition();
        int minutes = currentTime / 1000 / 60;
        int seconds = (currentTime / 1000) % 60;
        String currentTimeString = String.format("%02d:%02d", minutes, seconds);
        currentTimeTextView.setText(currentTimeString);

        if (mediaPlayer.getDuration() > 0) {
            int totalTime = mediaPlayer.getDuration();
            minutes = totalTime / 1000 / 60;
            seconds = (totalTime / 1000) % 60;
            String totalTimeString = String.format("%02d:%02d", minutes, seconds);
            totalTimeTextView.setText(totalTimeString);
        }
    }

    private void updateSongDetails(int songIndex) {
        Song song = getSong(songIndex);
        if (song != null) {
            songTitle.setText(song.getTitle());
            artist.setText(song.getArtist());
            totalTimeTextView.setText(song.getDuration());
        }
    }

    private Song getSong(int songIndex) {
        if (songIndex >= 0 && songIndex < songList.size()) {
            int songResource = songList.get(songIndex);
            Drawable albumCover = albumCovers.get(songIndex);

            String title;
            String artist;
            String duration;

            switch (songResource) {
                case R.raw.rings:
                    title = "7 Rings";
                    artist = "Ariana Grande";
                    duration = "03:04";
                    break;
                case R.raw.dance:
                    title = "Dance Monkey";
                    artist = "Tones and I";
                    duration = "03:56";
                    break;
                case R.raw.attention:
                    title = "Attention";
                    artist = "Charlie Puth";
                    duration = "03:32";
                    break;
                default:
                    title = "Unknown";
                    artist = "Unknown";
                    duration = "00:00";
                    break;
            }
            return new Song(title, artist, duration);
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this, SongSelectionActivity.class);
        startActivity(intent);
    }

}
