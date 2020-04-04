package com.syy.expression;

import android.text.TextUtils;

import com.syy.expression.record.MediaPlayerManager;

import java.io.File;

public class F2AudioManager {

    public String getPlayingUrl() {
        return playingUrl;
    }

    public void setPlayingUrl(String playingUrl) {
        this.playingUrl = playingUrl;
    }

    private String playingUrl;

    private static F2AudioManager instance = new F2AudioManager();

    public static F2AudioManager getInstance() {
        return instance;
    }

    public void play(String localPath, MediaPlayerManager.PlayingListener playListener) {
        MediaPlayerManager instance = MediaPlayerManager.getInstance();

        //优先播放本地
        if (!TextUtils.isEmpty(localPath)) {
            File file = new File(localPath);
            if (file.exists()) {
                instance.play(file.getAbsolutePath(), playListener);
            }
        }
    }

    public void pause() {
        MediaPlayerManager.getInstance().pause();
    }

    public boolean isPlaying() {
        return MediaPlayerManager.getInstance().isPlaying();
    }

    public void release() {
        MediaPlayerManager.getInstance().release();
    }

    private String getFileName(String url) {
        return "1.amr";
    }


}
