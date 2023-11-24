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

    public static name checkFilename(String filename){
        if(filename.contains("christmas/"))
            return name.CHRISTMAS;
        if(filename.contains("easter/"))
            return name.EASTER;
        return name.GENERAL;
    }
}
