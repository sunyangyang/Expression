package com.syy.expression.http;

import okhttp3.Response;

public interface HttpCallback {
    void success(Response response);
    void failed(Response response);
    void failed();
}
