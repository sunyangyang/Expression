package com.syy.expression;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.syy.expression.base.MainThread;
import com.syy.expression.http.HttpCallback;
import com.syy.expression.http.HttpUtils;
import com.syy.expression.record.MediaPlayerManager;
import com.syy.expression.record.OnDownSoundTouchListener;
import com.syy.expression.utils.FileUtil;
import com.syy.expression.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements ContentClickCallback {
    private static final int NORMAL = 0;
    private static final int START = 1;
    private static final int LOOP = 2;
    private static final int END = 3;

    private GifImageView imageView;
    private GifDrawable gifStart;
    private GifDrawable gifLoop;
    private GifDrawable gifEnd;
    private GifDrawable gifNormal;
    private GifDrawable gifEndNormal;
    private int status = NORMAL;
    Resources resources;

    private boolean over = true;
    private AnimationListener animationListenerStart;
    private AnimationListener animationListenerLoop;
    private AnimationListener animationListenerEnd;
    private AnimationListener animationListenerNormal;
    private AnimationListener animationListenerEndNormal;

    private ImageView mIvVoice;
    private ImageView mIvVoiceTop;
    private String mPath;
    private long mTotalTimeMillSecond;

    private Integer[] positive = new Integer[]{R.drawable.smile_connect_2_positive, R.drawable.positive, R.drawable.positive_2_smile_connect};
    private Integer[] negative = new Integer[]{R.drawable.smile_connect_2_negative, R.drawable.negative, R.drawable.negative_2_smile_connect};
    private Integer[] tsundere = new Integer[]{R.drawable.smile_connect_2_tsundere, R.drawable.tsundere, R.drawable.tsundere_2_smile_connect};
    private Integer[] doubt = new Integer[]{R.drawable.smile_connect_2_doubt, R.drawable.doubt, R.drawable.doubt_2_smile_connect};
    private Integer[] smile = new Integer[]{R.drawable.smile, R.drawable.smile, R.drawable.smile};

    private List<Integer[]> mExpressions = new ArrayList<>();
    private int mExpressionPosition;

    private ContentAdapter mAdapter;
    private LinearLayoutManager manager;

    private boolean isEnd = false;//防止播放回调和loop回调时间差问题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mExpressions.add(positive);
        mExpressions.add(negative);
        mExpressions.add(tsundere);
        mExpressions.add(doubt);
        mExpressions.add(smile);

        mAdapter = new ContentAdapter(this, this);
        RecyclerView recyclerView = findViewById(R.id.scroll_content);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);

        mIvVoiceTop = findViewById(R.id.iv_voice_top);
        mIvVoice = findViewById(R.id.iv_voice);
        initVoiceBtn();
        imageView = findViewById(R.id.gif_view);
        resources = getResources();
        initNormal();
    }

    private void initStart(int expression) {
        Log.e("XXXXX", "initStart");
        try {
//            if (animationListenerStart == null) {
                animationListenerStart = new AnimationListener() {
                    @Override
                    public void onAnimationCompleted(int loopNumber) {
                        Log.e("XXXXX", "initStart onAnimationCompleted");
                        status = LOOP;
                        initLoop(expression);
                        gifStart.stop();
                        imageView.setImageDrawable(gifLoop);
                        gifLoop.start();
                    }
                };
//            }

            gifStart = new GifDrawable(resources, mExpressions.get(expression)[0]);
            gifStart.addAnimationListener(animationListenerStart);
            gifStart.setLoopCount(1);
        } catch (Exception e) {

        }
    }

    private void initLoop(int expression) {
        Log.e("XXXXX", "initLoop");
        try {
//            if (animationListenerLoop == null) {
                animationListenerLoop = new AnimationListener() {
                    @Override
                    public void onAnimationCompleted(int loopNumber) {
                        Log.e("XXXXX", "initLoop onAnimationCompleted");
                        if (isEnd) {
                            isEnd = false;
                            status = END;
                            initEnd(expression);
                            gifLoop.stop();
                            imageView.setImageDrawable(gifEnd);
                            gifEnd.start();
                        }
                    }
                };
//            }
            gifLoop = new GifDrawable(resources, mExpressions.get(expression)[1]);
            gifLoop.setLoopCount(0);
            gifLoop.addAnimationListener(animationListenerLoop);
        } catch (Exception e) {

        }

    }

    private void initEnd(int expression) {
        Log.e("XXXXX", "initEnd");
        try {
//            if (animationListenerEnd == null) {
                animationListenerEnd = new AnimationListener() {
                    @Override
                    public void onAnimationCompleted(int loopNumber) {
                        Log.e("XXXXX", "initEnd onAnimationCompleted");
                        initEndNormal();
                        status = NORMAL;
                        imageView.setImageDrawable(gifEndNormal);
                        gifEndNormal.start();
                    }
                };
//            }

            gifEnd = new GifDrawable(resources, mExpressions.get(expression)[2]);
            gifEnd.addAnimationListener(animationListenerEnd);
            gifEnd.setLoopCount(1);
        } catch (Exception e) {

        }
    }

    private void initEndNormal() {
        Log.e("XXXXX", "initEndNormal");
        try {
//            if (animationListenerEndNormal == null) {
                animationListenerEndNormal = new AnimationListener() {
                    @Override
                    public void onAnimationCompleted(int loopNumber) {
                        Log.e("XXXXX", "initEndNormal onAnimationCompleted");
                        mIvVoiceTop.setVisibility(View.GONE);
                        remove();
                        initNormal();
                    }
                };
//            }

            gifEndNormal = new GifDrawable(resources, R.drawable.smile_connect);
            gifEndNormal.addAnimationListener(animationListenerEndNormal);
            gifEndNormal.setLoopCount(1);
        } catch (Exception e) {

        }
    }

    private void remove() {
        gifStart.removeAnimationListener(animationListenerStart);
        gifStart.recycle();
        gifStart = null;

        gifLoop.removeAnimationListener(animationListenerLoop);
        gifLoop.recycle();
        gifLoop = null;

        gifEnd.removeAnimationListener(animationListenerEnd);
        gifEnd.recycle();
        gifEnd = null;

        gifEndNormal.removeAnimationListener(animationListenerEndNormal);
        gifEndNormal.recycle();
        gifEndNormal = null;

        status = NORMAL;
        over = true;
        mIvVoice.setEnabled(true);
    }

    private void initNormal() {
        try {
            animationListenerNormal = new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    if (status == START) {
                        if (!F2AudioManager.getInstance().isPlaying()) {
                            F2AudioManager.getInstance().play(mPath, new MediaPlayerManager.PlayingListener() {
                                @Override
                                public void playCallback() {
                                    status = START;
                                }

                                @Override
                                public void onComplete() {
                                    Log.e("XXXXX", "play end");
                                    isEnd = true;
                                }
                            });
                        }
                        initStart(mExpressionPosition);
                        gifNormal.stop();
                        imageView.setImageDrawable(gifStart);
                        gifStart.start();
                    }
                }
            };


            gifNormal = new GifDrawable(resources, R.drawable.smile_connect);
            gifNormal.removeAnimationListener(animationListenerNormal);
            gifNormal.addAnimationListener(animationListenerNormal);
            gifNormal.setLoopCount(0);
            imageView.setImageDrawable(gifNormal);
            gifNormal.start();
        } catch (Exception e) {

        }
    }

    private void initVoiceBtn() {
        OnDownSoundTouchListener touchListener = new OnDownSoundTouchListener(this);
        touchListener.setOnRecordCallBack(new OnDownSoundTouchListener.OnRecordCallBack() {
            @Override
            public void onStartRecordListener(long systemTimeMill) {
                F2AudioManager.getInstance().pause();
                findViewById(R.id.voice_animation_ll).setVisibility(View.VISIBLE);
                findViewById(R.id.animation_view).setVisibility(View.VISIBLE);
                findViewById(R.id.voice_count_tv).setVisibility(View.GONE);
                findViewById(R.id.voice_cancel_ll).setVisibility(View.GONE);
                mIvVoice.setImageDrawable(getDrawable(R.drawable.icon_chat_voice2));
                ((TextView) findViewById(R.id.textView)).setText("松开发送，上滑取消");

            }

            @Override
            public void onRecordSuccessListener(long systemTimeMill, String path, long totalTimeMillSecond) {
                findViewById(R.id.voice_animation_ll).setVisibility(View.GONE);
                findViewById(R.id.voice_cancel_ll).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.textView)).setText("解析中，请稍后......");
                mIvVoice.setEnabled(false);
                mIvVoiceTop.setVisibility(View.VISIBLE);
                HttpUtils.upLoadFile("http://62.234.140.220:9997/file_upload", new HttpCallback() {
                    @Override
                    public void success(Response response) {
                        try {
                            ((TextView) findViewById(R.id.textView)).setText("按住说话");
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            JSONObject result = jsonObject.optJSONObject("result");
                            ExpressionBean expressionBean = new ExpressionBean();
                            expressionBean.expression = result.optInt("expression");
                            expressionBean.content = result.optString("content");
                            expressionBean.isPlay = result.optBoolean("IsPlay");
                            expressionBean.path = path;
                            mPath = path;
                            mExpressionPosition = expressionBean.expression;
                            mAdapter.addContent(expressionBean);
                            String answerString = result.optString("answer");
                            if (!TextUtils.isEmpty(answerString)) {
                                ExpressionBean answer = new ExpressionBean();
                                answer.answer = answerString;
                                answer.isPlay = false;
                                mAdapter.addContent(answer);
                            }
                            mAdapter.notifyDataSetChanged();
                            if (manager.findLastVisibleItemPosition() > 0 && manager.findLastVisibleItemPosition() < mAdapter.getItemCount() - 1) {
                                manager.scrollToPosition(mAdapter.getItemCount() - 1);
                            }

                            over = false;
                            status = START;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failed(Response response) {
                        ((TextView) findViewById(R.id.textView)).setText("按住说话");
                        ToastUtils.showText(MainActivity.this, "解析失败");
                    }

                    @Override
                    public void failed() {
                        ((TextView) findViewById(R.id.textView)).setText("按住说话");
                        ToastUtils.showText(MainActivity.this, "网络请求失败");
                    }
                }, new File(path));
                mIvVoice.setImageDrawable(getDrawable(R.drawable.icon_chat_voice1));
            }

            @Override
            public void onRecordCancelListener(long systemTimeMill) {

                findViewById(R.id.voice_animation_ll).setVisibility(View.GONE);
                findViewById(R.id.voice_cancel_ll).setVisibility(View.GONE);

                mIvVoice.setImageDrawable(getDrawable(R.drawable.icon_chat_voice1));
                ((TextView) findViewById(R.id.textView)).setText("按住说话");

            }

            @Override
            public void onMoveListener(boolean isCancel) {

                findViewById(R.id.voice_animation_ll).setVisibility(isCancel ? View.GONE : View.VISIBLE);
                findViewById(R.id.voice_cancel_ll).setVisibility(isCancel ? View.VISIBLE : View.GONE);

                mIvVoice.setImageDrawable(getDrawable(isCancel ? R.drawable.icon_chat_voice1 : R.drawable.icon_chat_voice2));
                ((TextView) findViewById(R.id.textView)).setText(isCancel ? "松开手指，取消发送" : "松开发送，上滑取消");

            }

            @Override
            public void onCountDownListener(long second) {
                findViewById(R.id.animation_view).setVisibility(View.GONE);
                findViewById(R.id.voice_count_tv).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.voice_count_tv)).setText(String.valueOf(second));

            }
        });
        touchListener.setParentPath(FileUtil.getFileExternalPath());
        mIvVoice.setOnTouchListener(touchListener);
    }

    @Override
    public void onContentClick(ExpressionBean expressionBean) {
        if (over && expressionBean.isPlay) {
            mPath = expressionBean.path;
            mExpressionPosition = expressionBean.expression;
            over = false;
            status = START;
            mIvVoiceTop.setVisibility(View.VISIBLE);
        } else {
            ToastUtils.showText(MainActivity.this, "请稍等片刻");
        }
    }
}
