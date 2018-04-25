package com.sonnyjack.library.network.http;

import android.os.Handler;

import com.sonnyjack.library.network.bean.BaseHttpParams;

/**
 * @author SonnyJack
 * @data 2018/4/25 10:34
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description:
 */

class HttpRequestBody {

    private Handler handler;

    private SonnyJackHttpClient httpClient;

    private BaseHttpParams httpParams;

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public SonnyJackHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(SonnyJackHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public BaseHttpParams getHttpParams() {
        return httpParams;
    }

    public void setHttpParams(BaseHttpParams httpParams) {
        this.httpParams = httpParams;
    }
}
