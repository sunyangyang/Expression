package com.syy.expression.record;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;

import com.syy.expression.base.BaseApplication;
import com.syy.expression.utils.ToastUtils;

import java.io.IOException;

public class MediaPlayerManager {

    private static MediaPlayerManager instance = new MediaPlayerManager();

    public static MediaPlayerManager getInstance() {
        return instance;
    }

    private MediaPlayer mediaPlayer;

    public void play(String path, PlayingListener playingListener) {

        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(false);

        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();;
                    if (playingListener != null) {
                        playingListener.playCallback();;
                    }
                }
            });
            mediaPlayer.setOnErrorListener(onErrorListener);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (playingListener != null) {
                        playingListener.onComplete();;
                    }
                }
            });
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }


    public boolean isPlaying(){
        return mediaPlayer!=null&& mediaPlayer.isPlaying();
    }

    private OnErrorListener onErrorListener = (mp, what, extra) -> {
        ToastUtils.showText(BaseApplication.getApplication(), "文件播放异常");
        return false;
    };

    private MediaPlayer.OnPreparedListener onCompletionListener = mp -> mp.start();

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public interface PlayingListener  {
        void playCallback();
        void  onComplete();
    }
}
