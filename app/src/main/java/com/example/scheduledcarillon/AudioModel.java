package com.example.scheduledcarillon;

import java.io.Serializable;

public class AudioModel implements Serializable {
    String path;
    String title;
    String duration;
    Seasons.name season;

    public AudioModel(String path, String title, String duration, Seasons.name season) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.season = season;
    }

    public Seasons.name getSeason() { return season; }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }
}
