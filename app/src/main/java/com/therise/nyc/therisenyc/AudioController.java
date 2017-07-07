package com.therise.nyc.therisenyc;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * AudioController: Container for a media player
 * We don't have to worry about null states when we access this. All checks are done within here.
 */

class AudioController {

    private MediaPlayer mediaPlayer; // AudioController passes samples to mediaPlayer
    protected Context context;

    private volatile boolean paused;

    AudioController(Context context){
        this.context = context;
        mediaPlayer = null; // initialize to null, before we play anything
        paused = false;
    }

    // Stop player, release, make null
    public void stop(){
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Play specific sample
    void playSample(int sample) {

        // Make sure media player is stopped
        stop();

        mediaPlayer = MediaPlayer.create(context, sample);

        // ensure that when the mediaPlayer is complete, it is set to null
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            public void onCompletion (MediaPlayer mp){
                stop();
            }
        });

        mediaPlayer.start();
    }

    boolean isStopped(){
        return mediaPlayer == null;
    }

    // Resume after pausing
    void resume(){
        if (mediaPlayer != null) {
            mediaPlayer.start();
            paused = false;
        }
    }

    void pause(){
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            paused = true;
        }
    }

    boolean isMediaPaused(){
        return mediaPlayer != null && paused;
    }

}
