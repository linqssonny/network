package com.sonnyjack.library.network;


import com.sonnyjack.library.network.bean.BaseHttpParams;
import com.sonnyjack.library.network.interfaces.IHttpCallBack;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author SonnyJack
 * @data 2018/4/17 21:58
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description: the interceptor for request
 */
class ResponseInterceptor implements Interceptor {

    private BaseHttpParams mBaseHttpParams;
    private IHttpCallBack mIHttpCallBack;

    public ResponseInterceptor(BaseHttpParams httpParams, IHttpCallBack httpCallBack) {
        this.mBaseHttpParams = httpParams;
        this.mIHttpCallBack = httpCallBack;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //拦截
        Response originalResponse = chain.proceed(chain.request());
        //包装响应体并返回
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body(), new ProgressListener(mBaseHttpParams, mIHttpCallBack)))
                .build();
    }
}
