package com.example.firebase_chat_demo.utils

import android.media.MediaPlayer
import java.io.IOException

class MediaPlayerUtils {

    fun onPlay(mediaPlayer: MediaPlayer, url: String) {
        mediaPlayer.reset()
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            e.stackTrace
        }
    }

    fun onPause(mediaPlayer: MediaPlayer) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
    }
}