package com.example.scheduledcarillon;

public class Seasons {

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

    public String getSeasonName(Seasons.name name){
        switch(name){
            case CHRISTMAS:
                return "Christmas";
            case EASTER:
                return "Easter";
            case GENERAL:
                return "Regular";
            default:
                return "Unknown season";
        }
    }
}
