package com.sonnyjack.example.network;

import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.sonnyjack.library.network.http.IHttpCallBack;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author SonnyJack
 * @data 2018/4/18 13:59
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description:  http callback
 */
public abstract class HttpObjectCallBack<T> extends IHttpCallBack {

    private Type mClassType;

    public HttpObjectCallBack() {
        mClassType = getSuperclassTypeParameter(getClass());
    }

    private Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        return parameterizedType.getActualTypeArguments()[0];
    }

    protected HttpParams getMyHttpParams() {
        HttpParams httpParams = (HttpParams) this.getHttpParams();
        return httpParams;
    }

    @Override
    public final void before() {
        if (getMyHttpParams().isLoading()) {
            //这里可以显示等待框
            Toast.makeText(getMyHttpParams().getContext(), "onBefore 被执行了...", Toast.LENGTH_SHORT).show();
        }
        //还可以 what you want to do
        onBefore();
    }

    @Override
    public final void progress(long bytesRead, long contentLength, boolean finish) {
        //这里是下载、上传进度回调
        onProgress(bytesRead, contentLength, finish);
    }

    @Override
    public final void after() {
        //请求结束后回调
        if (getMyHttpParams().isLoading()) {
            //这里可以关闭等待框
            Toast.makeText(getMyHttpParams().getContext(), "onAfter 被执行了...", Toast.LENGTH_SHORT).show();
        }
        onAfter();
    }

    @Override
    public final void fail(int error, String message) {
        if (getMyHttpParams().isShowErrorMessage()) {
            //这里可以统一toast 错误信息
            Toast.makeText(getMyHttpParams().getContext(), "弹出错误信息啦：" + message, Toast.LENGTH_SHORT).show();
        }
        onFail(error, message);
    }

    @Override
    public final void success(String body) {
        String result = body;
        if (getMyHttpParams().isAnalysisResult()) {
            //这里可以统一进行解析一层返回的数据
            result = body;
        }
        //这里可以用fastjson、gson等转为对象
        if (mClassType == String.class) {
            onSuccess((T) result);
        } else {
            T obj = JSON.parseObject(body, mClassType);
            onSuccess(obj);
        }
    }

    public void onBefore() {

    }

    public void onProgress(long bytesRead, long contentLength, boolean finish) {

    }

    public void onFail(int error, String message) {

    }

    public abstract void onSuccess(T t);

    public void onAfter() {

    }
}
