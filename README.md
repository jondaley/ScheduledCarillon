# Android Scheduled Carillon

This Android app serves as a simple way to play sound files from an external SD Card (doesn't use internet) and play them on a scheduled basis.  For my use, our church plays songs at 4:00PM every day for 15 minutes, and also on Sundays at 10:10AM.
You simply put mp3s or any sound file that the Android device understands on the SD Card and it will find them.  You can organize them in directories (see below for the "season", which will use the directory structure, but that isn't working yet).


## Things to do:
- Allow users to configure the times that it plays and for how long
- Our church has different meeting times in the summer, so the 10:10 needs to be changed to 9:10, so 
- Look at the file/path names to determine what "season" the files should be used,
  e.g. play Christmas songs at Christmas time, Easter songs on Easter, etc.
  At all other times, play everything else.
- More seasons should be added.
- Seasons need to be defined by dates
- I assume daylight saving time will work correctly, since the Android device will set itself automatically
  It might do the first day wrong due to the timer being already set?

## Key Features
- Play audio tracks.
- Display song title, current playback time, and total duration.
- When the cut-off time triggers, it doesn't stop the song immediately, but allows it to finish
  e.g. the ending time isn't totally defined, so you need to account for your longest track
- You can manually stop all playing by hitting the "pause" button and no further songs will play until you hit "play"

## Technologies and Components
- Android Studio
- Java
- MediaPlayer for audio playback
- TextView for displaying song information
- Buttons for controlling playback
- ImageView for displaying a music icon


Let me know (via github) if you have any suggestions or comments.  I've been meaning to write this code for over a decade,
and I finally got it working today.

Thanks to https://github.com/bimalkaf/Android_Basic_MusicApp, I got a good start on writing my first Android app!
