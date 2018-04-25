package com.sonnyjack.library.network.http;


import android.content.Context;
import android.os.Handler;

import com.sonnyjack.library.network.bean.BaseHttpParams;

/**
 * @author SonnyJack
 * @data 2018/4/17 21:58
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description: the base callback for network
 */
public abstract class IHttpCallBack {

    private HttpRequestBody mHttpRequestBody;

    public void setHttpRequestBody(HttpRequestBody httpRequestBody) {
        mHttpRequestBody = httpRequestBody;
    }

    public BaseHttpParams getBaseHttpParams() {
        return mHttpRequestBody.getHttpParams();
    }

    public Handler getHandler() {
        return mHttpRequestBody.getHandler();
    }

    public SonnyJackHttpClient getSonnyJackHttpClient() {
        return mHttpRequestBody.getHttpClient();
    }

    public Context getContext() {
        return getBaseHttpParams().getContext();
    }

    public void cancel() {
        Object tag = getBaseHttpParams().getTag();
        if (null == tag) {
            return;
        }
        HttpManager.getInstance().cancel(getSonnyJackHttpClient(), tag);
    }

    public abstract void before();

    public abstract void progress(long bytesRead, long contentLength, boolean finish);

    public abstract void after();

    public abstract void fail(int error, String message);

    public abstract void success(String body);
}
