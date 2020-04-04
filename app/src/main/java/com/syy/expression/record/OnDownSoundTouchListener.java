package com.syy.expression.record;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.syy.expression.base.PermissionCommon;
import com.syy.expression.permission.PermissionAvoidManager;
import com.syy.expression.utils.ToastUtils;

import java.io.File;
import java.io.IOException;

public class OnDownSoundTouchListener implements View.OnTouchListener {

    private Context context;

    public OnDownSoundTouchListener(Context context) {
        this.context = context;
    }

    private OnRecordCallBack onRecordCallBack;

    public void setOnRecordCallBack(OnRecordCallBack onRecordCallBack) {
        this.onRecordCallBack = onRecordCallBack;
    }

    private String parentPath;

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    /**
     * 按下的Y坐标
     */
    private float downY;

    private View touchView;
    private MotionEvent motionEvent;

    private boolean isOnceOperate;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == null || event == null) return false;
        touchView = v;

        final float maxMoveHeight = v.getHeight() * 1f;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                motionEvent = event;
                isOnceOperate = true;
                downY = event.getRawY();
                startRecord(v);
                break;
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(countDown);
                resetCountDuration();
                if (isOnceOperate) {
                    if (mHasPermission) {
                        stopRecorder();
                    }
                    isOnceOperate = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                motionEvent = event;
                if (isOnceOperate&&mHasPermission) {
                    if (onRecordCallBack != null) {
                        onRecordCallBack.onMoveListener(isCancel(event, maxMoveHeight));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                handler.removeCallbacks(countDown);
                resetCountDuration();
                if (isOnceOperate) {
                    if (mHasPermission) {
                        if (isCancel(event, maxMoveHeight)) {
                            stopRecorder();
                        } else {
                            sendSoundMessage();
                        }
                    }
                    isOnceOperate = false;
                }
                break;
            default:
                break;
        }
        return false;
    }

    private void resetCountDuration(){
        duration= 16;
    }

    private boolean isCancel(MotionEvent event, float maxMoveHeight) {
        float upHeight = downY - event.getRawY();
        return Math.abs(upHeight) > maxMoveHeight;
    }


    private boolean mHasPermission = false;


    //开始录音
    private void startRecord(View v) {
        if (v.getContext() instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) v.getContext();
            String[] permission = {Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //有权限直接录音
            if (PermissionCommon.checkAllSelfPermissions(context, permission)) {
                try {
                    mHasPermission = true;
                    startRecorder();
                } catch (Exception e) {
                    mHasPermission = false;
                    handlerNoneRecodePermission();
                }
            } else {//没有权限请求权限
                PermissionAvoidManager permissionAvoidManager = new PermissionAvoidManager(activity);
                permissionAvoidManager.requestPermission(permission, (hasPermission, shouldShowAllRequestPermissionRationale) -> {
                    if (!hasPermission) {
                        ToastUtils.showText(context, "您已拒绝语音相关权限，此功能将无法使用");
                    }
                });
            }

        }
    }


    private void sendSoundMessage() {
        //停止录音
        if (audioRecorder != null) {
            audioRecorder.stop();
        }

        //判断文件是否存在
        if (mRecorderFile == null || !mRecorderFile.exists() || mRecorderFile.length() <= 0) {
            ToastUtils.showText(context, "语音录制失败");
            if (onRecordCallBack != null) {
                onRecordCallBack.onRecordCancelListener(recordTimeMillSecond);
            }
            //删除文件
            if (mRecorderFile != null) {
                mRecorderFile.delete();
            }
            return;
        }

        //录制时间太短
        if (System.currentTimeMillis() - recordTimeMillSecond < 1000) {
            ToastUtils.showText(context, "语音录制时间太短");
            if (onRecordCallBack != null) {
                onRecordCallBack.onRecordCancelListener(recordTimeMillSecond);
            }
            return;
        }

        //成功
        if (onRecordCallBack != null) {
            long totalMillSecond = System.currentTimeMillis() - recordTimeMillSecond;
            onRecordCallBack.onRecordSuccessListener(recordTimeMillSecond, mRecorderFile.getAbsolutePath(), totalMillSecond);
        }
    }


    private File mRecorderFile;

    private SoundRecorder audioRecorder;


    private void startRecorder() throws IOException, NullPointerException {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        if (audioRecorder == null) {
            audioRecorder = new SoundRecorder();
        }

        mRecorderFile = new File(parentPath, audioRecorder.getFileName());
        if (!mRecorderFile.getParentFile().exists()) {
            mRecorderFile.getParentFile().mkdirs();
        }
        try {
            mRecorderFile.createNewFile();
        } catch (Exception e) {
            throw new NullPointerException("创建录音文件失败");
        }

        audioRecorder.start(mRecorderFile.getPath());

        handler.postDelayed(countDown,1000);

        if (onRecordCallBack != null) {
            recordTimeMillSecond = System.currentTimeMillis();
            onRecordCallBack.onStartRecordListener(recordTimeMillSecond);
        }
    }


    private int duration = 16;

    private Runnable countDown=new Runnable() {
        @Override
        public void run() {

            duration--;


            if (duration<=10) {
                if (onRecordCallBack != null) {
                    onRecordCallBack.onCountDownListener(duration);
                }
            }

            if (duration==0) {
                MotionEvent cancel = MotionEvent.obtain(motionEvent);
                cancel.setAction(MotionEvent.ACTION_UP);
                onTouch(touchView, cancel);
                return;
            }

            handler.postDelayed(countDown,1000);
        }
    };

    private final android.os.Handler handler = new android.os.Handler();



    //录制时间
    private long recordTimeMillSecond;


    private void stopRecorder() {
        if (onRecordCallBack != null) {
            onRecordCallBack.onRecordCancelListener(recordTimeMillSecond);
        }
        if (audioRecorder != null) {
            audioRecorder.stop();
        }
        if (mRecorderFile != null) {
            mRecorderFile.delete();
        }
    }


    public interface OnRecordCallBack {

        void onStartRecordListener(long systemTimeMill);

        void onRecordSuccessListener(long systemTimeMill, String path, long totalTimeMillSecond);

        void onRecordCancelListener(long systemTimeMill);

        void onMoveListener(boolean isCancel);

        void  onCountDownListener(long second);


    }

    /**
     * 处理没有录音权限的情况
     */
    private void handlerNoneRecodePermission() {
        ToastUtils.showText(context, "您已拒绝语音相关权限，此功能将无法使用");
        stopRecorder();
    }
}
