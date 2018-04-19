package com.sonnyjack.example.network;

import android.widget.Toast;

import com.sonnyjack.library.network.bean.BaseHttpParams;
import com.sonnyjack.library.network.interfaces.IHttpCallBack;

/**
 * @author SonnyJack
 * @data 2018/4/18 13:59
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description:  http callback
 */
public abstract class HttpCallBack implements IHttpCallBack {

    @Override
    public final void onBefore(BaseHttpParams httpParams) {
        if (httpParams.isLoading()) {
            //这里可以显示等待框
            Toast.makeText(httpParams.getContext(), "onBefore 被执行了...", Toast.LENGTH_SHORT).show();
        }
        //还可以 what you want to do
        onBefore((HttpParams) httpParams);
    }

    @Override
    public final void onProgress(BaseHttpParams httpParams, long bytesRead, long contentLength, boolean finish) {
        //这里是下载、上传进度回调
        onProgress((HttpParams) httpParams, bytesRead, contentLength, finish);
    }

    @Override
    public final void onAfter(BaseHttpParams httpParams) {
        //请求结束后回调
        if (httpParams.isLoading()) {
            //这里可以关闭等待框
            Toast.makeText(httpParams.getContext(), "onAfter 被执行了...", Toast.LENGTH_SHORT).show();
        }
        onAfter((HttpParams) httpParams);
    }

    @Override
    public final void onFail(BaseHttpParams httpParams, int error, String message) {
        HttpParams params = (HttpParams) httpParams;
        if (params.isShowErrorMessage()) {
            //这里可以统一toast 错误信息
            Toast.makeText(httpParams.getContext(), "弹出错误信息啦：" + message, Toast.LENGTH_SHORT).show();
        }
        onFail(params, error, message);
    }

    @Override
    public final void onSuccess(BaseHttpParams httpParams, String body) {
        HttpParams params = (HttpParams) httpParams;
        String result = body;
        if (params.isAnalysisResult()) {
            //这里可以统一进行解析一层返回的数据并且可以用fastjson、gson等转为对象
            result = body;
        }
        onSuccess(params, result);
    }

    public void onBefore(HttpParams httpParams) {

    }

    public void onProgress(HttpParams httpParams, long bytesRead, long contentLength, boolean finish) {

    }

    public void onFail(HttpParams httpParams, int error, String message) {

    }

    public abstract void onSuccess(HttpParams httpParams, String body);

    public void onAfter(HttpParams httpParams) {

    }
}
