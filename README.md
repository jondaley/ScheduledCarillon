# Android Scheduled Carillon app

This Android app serves as a simple way to play sound files from an external SD Card (doesn't use internet) and play them on a scheduled basis.  For my use, our church plays songs at 4:10PM every day for 15 minutes, and also on Sundays at 10:10AM or 9:10AM as selected.
You simply put mp3s or any sound file that the Android device understands on the SD Card and it will find them.  You can organize them in directories (see below for the "season").


## Things to do:
- Allow users to configure the times that it plays and for how long
- Look at the file/path names to determine what "season" the files should be used,
  e.g. play Christmas songs at Christmas time, Easter songs on Easter, etc.
  At all other times, play everything else.
- More seasons should be added.
- Seasons need to be defined by dates

## Key Features
- Play audio tracks.
- Display song title, current playback time, and total duration.
- When the cut-off time triggers, it doesn't stop the song immediately, but allows it to finish
  e.g. the ending time isn't totally defined, so you need to account for your longest track (this is
  considered an improvement over our old tape player that stopped in the middle of the song at the
  turn off time and started playing in the middle of the song the next day)
- You can manually stop all playing by hitting the "pause" button and no further songs will play until you hit "play"
- It will only ever play for a maximum of one hour if you hit the manual play button (this avoids 911 being called
  if someone turns on the player and then forgets to turn it off when they leave (ask me how I know...)

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
