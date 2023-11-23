package com.example.scheduledcarillon;

public final class  Seasons {

    public enum name {
        CHRISTMAS,
        EASTER,
        GENERAL
    }

    protected name currentSeason;

    public Seasons() {
        // TODO: set currentSeason based on current date
        currentSeason = name.GENERAL;
    }

    public name getCurrentSeason() {
        return currentSeason;
    }
}
