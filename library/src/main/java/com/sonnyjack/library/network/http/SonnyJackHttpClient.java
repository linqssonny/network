package com.sonnyjack.library.network.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author SonnyJack
 * @data 2018/4/25 10:26
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description:
 */

public class SonnyJackHttpClient {

    private OkHttpClient mOkHttpClient;

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    private long mConnectionTime = 30;//默认http请求时间30秒
    private long mDownloadTime = 30 * 60;//默认下载超时30分钟

    public SonnyJackHttpClient() {
        this(30, 30 * 60);
    }

    public SonnyJackHttpClient(long connectionTime, long downloadTime) {
        setConnectionTimeAndDownloadTime(connectionTime, downloadTime);
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(mConnectionTime, TimeUnit.SECONDS)
                .readTimeout(mConnectionTime, TimeUnit.SECONDS)
                .writeTimeout(mConnectionTime, TimeUnit.SECONDS).build();
    }

    private SonnyJackHttpClient(OkHttpClient okHttpClient, long connectionTime, long downloadTime) {
        setConnectionTimeAndDownloadTime(connectionTime, downloadTime);
        mOkHttpClient = okHttpClient;
    }

    private void setConnectionTimeAndDownloadTime(long connectionTime, long downloadTime) {
        if (connectionTime > 0) {
            mConnectionTime = connectionTime;
        }
        if (downloadTime > 0) {
            mDownloadTime = downloadTime;
        }
    }

    public SonnyJackHttpClient clone(IHttpCallBack httpCallBack) {
        OkHttpClient okHttpClient = mOkHttpClient.newBuilder()
                .readTimeout(mDownloadTime, TimeUnit.SECONDS)
                .writeTimeout(mDownloadTime, TimeUnit.SECONDS)
                .addInterceptor(new ResponseInterceptor(httpCallBack)).build();
        SonnyJackHttpClient sonnyJackHttpClient = new SonnyJackHttpClient(okHttpClient, mConnectionTime, mDownloadTime);
        return sonnyJackHttpClient;
    }
}
