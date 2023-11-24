package com.example.scheduledcarillon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    ArrayList<AudioModel> songsList = new ArrayList<>();
    TextView titleTv,currentTimeTv,totalTimeTv,seasonTv, timerTv;
    ImageView pauseBtn;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    Seasons seasons = new Seasons();
    boolean isPlaying = false;
    boolean isDisabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkPermission()){
            requestPermission();
            return;
        }

        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        pauseBtn = findViewById(R.id.pause_play);
        seasonTv = findViewById(R.id.season_title);
        timerTv = findViewById(R.id.timers);

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        seasonTv.setText(seasons.getSeasonName(seasons.getCurrentSeason()));

        String selection = MediaStore.Audio.Media.IS_MUSIC +" != 0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, selection,null,null);

        while(cursor.moveToNext()){
            AudioModel songData = new AudioModel(
                    cursor.getString(1),
                    cursor.getString(0),
                    cursor.getString(2),
                    Seasons.checkFilename((cursor.getString((1)))));

            if(new File(songData.getPath()).exists())
                songsList.add(songData);
        }

        if(songsList.size() == 0){
            titleTv.setText("No songs found on SD Card");
        }
        else{
            Alarm.setActivity(this);
            Alarm.scheduleDailyAlarm();
            Alarm.scheduleSundayAlarm();
            setResourcesWithMusic(true);
            pauseBtn.setOnClickListener(v->pause_play());
        }

       MainActivity.this.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                if(mediaPlayer != null && currentSong != null)
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));
                else
                    currentTimeTv.setText("00:00");
                new Handler().postDelayed(this,100);
            }
        });
    }

    boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE))
            Toast.makeText(MainActivity.this,"Read permission to the external storage is required, please allow it in the settings.",Toast.LENGTH_SHORT).show();
        else
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: do anything?
    }


    void setResourcesWithMusic(boolean bFirstTime){
        if(isDisabled || bFirstTime) {
            currentSong = null;
        }
        else {
            Random rand = new Random();
            currentSong = songsList.get(rand.nextInt(songsList.size()));
        }

        if(bFirstTime){
            timerTv.setText("Next play time: " + Alarm.getNextScheduledPlayTime());
            titleTv.setText("No song selected yet");
            totalTimeTv.setText("00:00");
        }
        else if(currentSong == null){
            timerTv.setText("Music paused");
            titleTv.setText("No song selected yet");
            totalTimeTv.setText("00:00");
        }
        else{
            timerTv.setText("Next play time: " + Alarm.getNextScheduledPlayTime());
            titleTv.setText(currentSong.getTitle());
            totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));
        }
        seasonTv.setText("Season: " + seasons.getSeasonName(seasons.getCurrentSeason()));
    }


    public void playMusic(){
        if(isDisabled || currentSong == null)
            return;

        isPlaying = MyMediaPlayer.play(currentSong);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer){
                if(isPlaying)
                    playNextSong();
            }
        });
    }

    private void playNextSong(){
        mediaPlayer.reset();
        setResourcesWithMusic(false);
        playMusic();
    }

    private void pause_play(){
        isDisabled = !isDisabled;
        if(isDisabled) {
            mediaPlayer.stop();
            pauseBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
            setResourcesWithMusic(false);
        }
        else {
            pauseBtn.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
        }
    }

    public void stop(boolean bImmediate){
        // stop playing when the song finishes
        isPlaying = false;

        // stop playing right now
        if(bImmediate)
            mediaPlayer.stop();
    }

    @SuppressLint("DefaultLocale")
    public static String convertToMMSS(String duration){
        long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}
