package com.example.scheduledcarillon;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnSeekCompleteListener;

public class MyMediaPlayer {
    static MediaPlayer instance;

    public static MediaPlayer getInstance(){
        if(instance == null){
            instance = new MediaPlayer();
        }
        return instance;
    }

    public static int currentIndex = 0;

}
