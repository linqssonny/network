package com.sonnyjack.example.network;

import android.widget.Toast;

import com.sonnyjack.library.network.http.IHttpCallBack;

/**
 * @author SonnyJack
 * @data 2018/4/18 13:59
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description:  http callback
 */
public abstract class HttpCallBack extends IHttpCallBack {

    protected HttpParams getHttpParams() {
        HttpParams httpParams = (HttpParams) getBaseHttpParams();
        return httpParams;
    }

    @Override
    public final void before() {
        if (getHttpParams().isLoading()) {
            //这里可以显示等待框
            Toast.makeText(getHttpParams().getContext(), "onBefore 被执行了...", Toast.LENGTH_SHORT).show();
        }
        //还可以 what you want to do
        onBefore();

        //you  can  call  the method cancel request
        //cancel();
    }

    @Override
    public final void progress(long bytesRead, long contentLength, boolean finish) {
        //这里是下载、上传进度回调
        onProgress(bytesRead, contentLength, finish);
    }

    @Override
    public final void after() {
        //请求结束后回调
        if (getHttpParams().isLoading()) {
            //这里可以关闭等待框
            Toast.makeText(getHttpParams().getContext(), "onAfter 被执行了...", Toast.LENGTH_SHORT).show();
        }
        onAfter();
    }

    @Override
    public final void fail(int error, String message) {
        if (getHttpParams().isShowErrorMessage()) {
            //这里可以统一toast 错误信息
            Toast.makeText(getHttpParams().getContext(), "弹出错误信息啦：" + message, Toast.LENGTH_SHORT).show();
        }
        onFail(error, message);
    }

    @Override
    public final void success(String body) {
        String result = body;
        if (getHttpParams().isAnalysisResult()) {
            //这里可以统一进行解析一层返回的数据并且可以用fastjson、gson等转为对象
            result = body;
        }
        onSuccess(result);
    }

    public void onBefore() {

    }

    public void onProgress(long bytesRead, long contentLength, boolean finish) {

    }

    public void onFail(int error, String message) {

    }

    public abstract void onSuccess(String body);

    public void onAfter() {

    }
}
