package com.example.firebase_chat_demo.utils

import android.media.MediaPlayer
import java.io.IOException

class MediaPlayerUtils {

    private var mMediaPlayer: MediaPlayer? = null

    init {
        mMediaPlayer = MediaPlayer()
    }

    fun onPlay(url: String) {
        mMediaPlayer!!.reset()
        try {
            mMediaPlayer!!.setDataSource(url)
            mMediaPlayer!!.prepare()
            mMediaPlayer!!.start()
        } catch (e: IOException) {
            e.stackTrace
        }
    }

    fun onPause() {
        if (mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.pause()
        } else {
            mMediaPlayer!!.start()
        }
    }

    fun isPlaying(): Boolean {
        return  mMediaPlayer != null && mMediaPlayer!!.isPlaying
    }
}