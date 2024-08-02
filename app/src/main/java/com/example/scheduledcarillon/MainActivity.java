package com.example.scheduledcarillon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    ArrayList<AudioModel> songsList = new ArrayList<>();
    TextView titleTv,currentTimeTv,totalTimeTv,seasonTv,timerTv,songSeasonTv;
    ImageView scheduleBtn;
    ImageView playBtn;
    Switch morningSwitch;

    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    Seasons seasons = new Seasons();
    boolean isPlaying = false, isDisabled = false, isNineThirty = false;

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
        scheduleBtn = findViewById(R.id.pause_schedule_play);
        playBtn = findViewById(R.id.play_immediately);
        morningSwitch = findViewById(R.id.play_nine_thiry);
        seasonTv = findViewById(R.id.season_title);
        songSeasonTv = findViewById(R.id.song_season_title);
        timerTv = findViewById(R.id.timers);

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        seasonTv.setText(seasons.getCurrentSeason().getName());

        String selection = MediaStore.Audio.Media.IS_MUSIC +" != 0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, selection,null,null);

        while(cursor.moveToNext()){
            AudioModel songData = new AudioModel(
                    cursor.getString(1),
                    (cursor.getString(0) == null ? "Unknown title" : cursor.getString(0)),
                    (cursor.getString(2) == null ? "0" : cursor.getString(2)),
                    Seasons.checkFilename(cursor.getString(1))
                    );

            if(new File(songData.getPath()).exists() && songData.duration != "0")
                songsList.add(songData);
        }

        if(songsList.size() == 0){
            titleTv.setText("No songs found on SD Card");
        }
        else{
            Log.i(Globals.LOG_TAG, "Loaded " + songsList.size() + " songs");
            Alarm.setActivity(this);
            Alarm.scheduleDailyAlarm();
            Alarm.scheduleSundayAlarm();
            setResourcesWithMusic(true);
            scheduleBtn.setOnClickListener(v-> pause_schedule_play());
            playBtn.setOnClickListener(v-> immediate_play());
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

    public boolean isNineThirtyChecked(){
        return morningSwitch.isChecked();
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
            int songId = rand.nextInt(songsList.size());
            Log.i(Globals.LOG_TAG, "Song id is " + songId);
            currentSong = songsList.get(songId);
        }

        if(bFirstTime){
            timerTv.setText("Next play time: " + Alarm.getNextScheduledPlayTime());
            titleTv.setText("No song selected yet");
            totalTimeTv.setText("00:00");
            songSeasonTv.setText("");

        }
        else if(currentSong == null){
            timerTv.setText("Music paused");
            titleTv.setText("No song selected yet");
            totalTimeTv.setText("00:00");
            songSeasonTv.setText("");
        }
        else{
            timerTv.setText("Next play time: " + Alarm.getNextScheduledPlayTime());
            titleTv.setText(currentSong.getTitle());
            totalTimeTv.setText( convertToMMSS(currentSong.getDuration()));
            songSeasonTv.setText("Song Season: " + currentSong.getSeason().getName());
        }
        seasonTv.setText("Season: " + seasons.getCurrentSeason().getName());
    }


    public void playMusic(){
        if(isDisabled || currentSong == null)
            return;

        isPlaying = MyMediaPlayer.play(currentSong);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer){
                if(isPlaying && !isDisabled)
                    playNextSong();
            }
        });
    }

    private void playNextSong(){
        mediaPlayer.reset();
        setResourcesWithMusic(false);
        playMusic();
    }

    private void pause_schedule_play(){
        isDisabled = !isDisabled;
        if(isDisabled) {
            mediaPlayer.stop();
            scheduleBtn.setImageResource(R.drawable.ic_baseline_schedule_circle_outline_24);
        }
        else {
            scheduleBtn.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
        }
        setResourcesWithMusic(false);
    }

    private void immediate_play(){
        isDisabled = false;
        mediaPlayer.stop();
        scheduleBtn.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
        setResourcesWithMusic(false);
        playMusic();
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
        try {
            long millis = Long.parseLong(duration);
            return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        }
        catch(NumberFormatException e){
            Log.e(Globals.LOG_TAG, "Bad number format: " + duration);
            return "error";

        }
    }
}
