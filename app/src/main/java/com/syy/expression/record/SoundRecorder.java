package com.syy.expression.record;

import android.media.MediaRecorder;
import android.text.TextUtils;

import java.io.IOException;

public class SoundRecorder {

    private MediaRecorder mediaRecorder;

    public void start(String filePath) throws IOException {
        if (TextUtils.isEmpty(filePath)) {
            throw new NullPointerException("filePath is null");
        }
        stop();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                if (mr != null) {
                    try {
                        mr.reset();
                    } catch (Exception e) {
                    }
                }
            }
        });
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.prepare();
        mediaRecorder.start();
    }

    public void stop() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.setOnErrorListener(null);
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
            } catch (Exception e) {
            }
        }
        mediaRecorder = null;
    }


    public String getFileName() {
        return System.currentTimeMillis() + ".amr";
    }

}
