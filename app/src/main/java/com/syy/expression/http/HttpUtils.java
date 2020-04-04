package com.syy.expression.http;
import com.syy.expression.base.MainThread;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {
    private static OkHttpClient sInstance;

    private static OkHttpClient getInstance() {
        if (sInstance != null) {
            synchronized (HttpUtils.class) {
                sInstance = null;
            }
        }
        if (sInstance == null) {
            synchronized (HttpUtils.class) {
                if (sInstance == null) {
                    sInstance = new OkHttpClient();
                }
            }
        }
        return sInstance;
    }

    public static void get(String url, HttpCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){//回调的方法执行在子线程。
                    MainThread.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.success(response);
                        }
                    });

                } else {
                    MainThread.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.failed(response);
                        }
                    });
                }
            }
        });
    }

    public static void post(String url, HttpCallback callback, RequestBody requestBody) {
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){//回调的方法执行在子线程。
                    MainThread.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.success(response);
                        }
                    });

                } else {
                    MainThread.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.failed(response);
                        }
                    });
                }
            }
        });
    }

    private static final MediaType FROM_DATA = MediaType.parse("multipart/form-data");
    public static void upLoadFile(String url, HttpCallback callback, File file) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody body = new MultipartBody.Builder()
                .setType(FROM_DATA)
                .addFormDataPart("file",file.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MainThread.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.failed();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){//回调的方法执行在子线程。
                    MainThread.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.success(response);
                        }
                    });

                } else {
                    MainThread.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.failed(response);
                        }
                    });
                }
            }
        });
    }

}
