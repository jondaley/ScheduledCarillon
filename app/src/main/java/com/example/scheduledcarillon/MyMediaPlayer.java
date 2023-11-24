package com.example.scheduledcarillon;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.widget.SeekBar;

import java.io.IOException;

public class MyMediaPlayer{
    static MediaPlayer instance;

    public static MediaPlayer getInstance(){
        if(instance == null)
            instance = new MediaPlayer();
        return instance;
    }

    public static int currentIndex = -1;

    public static boolean play(AudioModel currentSong, SeekBar seekBar){
        instance.reset();
        try{
            instance.setDataSource(currentSong.getPath());
            instance.prepare();
            instance.start();
            seekBar.setProgress(0);
            seekBar.setMax(instance.getDuration());
            return true;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }
}