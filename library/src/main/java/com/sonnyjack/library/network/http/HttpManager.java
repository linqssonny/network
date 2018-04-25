package com.sonnyjack.library.network.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sonnyjack.library.network.bean.BaseHttpParams;
import com.sonnyjack.library.network.constant.HttpCode;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author SonnyJack
 * @data 2018/4/17 21:58
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description: the http utils for network
 */
public class HttpManager {

    private static class HttpUtilsHolder {
        private static HttpManager instance = new HttpManager();
    }

    public static HttpManager getInstance() {
        return HttpUtilsHolder.instance;
    }

    private SonnyJackHttpClient mSonnyJackHttpClient = null;


    private Handler mHandler;

    private HttpManager() {
        mHandler = new Handler(Looper.getMainLooper());
        init();
    }

    private void init() {
        init(0, 0);
    }

    /**
     * 初始化
     *
     * @param connectionTime 连接时间  小于或等于0为默认值30秒
     * @param downloadTime   下载上传时间  小于或等于0为默认值30分钟
     */
    private void init(long connectionTime, long downloadTime) {
        if (null == mSonnyJackHttpClient) {
            mSonnyJackHttpClient = new SonnyJackHttpClient(connectionTime, downloadTime);
        }
    }

    private OkHttpClient getHttpClient() {
        return getSonnyJackHttpClient().getOkHttpClient();
    }

    private SonnyJackHttpClient getSonnyJackHttpClient() {
        if (null == mSonnyJackHttpClient) {
            init();
        }
        return mSonnyJackHttpClient;
    }

    /***
     * post  同步请求
     *
     * @param httpParams
     * @param httpCallBack
     * @return
     */
    public void post(BaseHttpParams httpParams, IHttpCallBack httpCallBack) {
        HttpRequestBody httpRequestBody = buildRequestBody(getSonnyJackHttpClient(), httpParams, httpCallBack);
        SonnyJackCallBack.sendBeforeCallBack(httpCallBack);
        Request request = RequestHelper.createPostRequest(httpRequestBody);
        execute(request, httpCallBack);
    }

    /***
     * post  异步请求
     *
     * @param httpParams
     * @param httpCallBack
     */
    public void postAsync(BaseHttpParams httpParams, IHttpCallBack httpCallBack) {
        HttpRequestBody httpRequestBody = buildRequestBody(getSonnyJackHttpClient(), httpParams, httpCallBack);
        SonnyJackCallBack.sendBeforeCallBack(httpCallBack);
        Request request = RequestHelper.createPostRequest(httpRequestBody);
        getHttpClient().newCall(request).enqueue(new SonnyJackCallBack(SonnyJackCallBack.ACTION_POST, httpCallBack));
    }


    /***
     * get  同步请求
     *
     * @param httpParams
     * @param httpCallBack
     * @return
     */
    public void get(final BaseHttpParams httpParams, final IHttpCallBack httpCallBack) {
        HttpRequestBody httpRequestBody = buildRequestBody(getSonnyJackHttpClient(), httpParams, httpCallBack);
        SonnyJackCallBack.sendBeforeCallBack(httpCallBack);
        Request request = RequestHelper.createGetRequest(httpRequestBody);
        execute(request, httpCallBack);
    }

    /***
     * get  异步请求
     *
     * @param httpParams
     * @param httpCallBack
     */
    public void getAsync(BaseHttpParams httpParams, IHttpCallBack httpCallBack) {
        HttpRequestBody httpRequestBody = buildRequestBody(getSonnyJackHttpClient(), httpParams, httpCallBack);
        SonnyJackCallBack.sendBeforeCallBack(httpCallBack);
        Request request = RequestHelper.createGetRequest(httpRequestBody);
        getHttpClient().newCall(request).enqueue(new SonnyJackCallBack(SonnyJackCallBack.ACTION_GET, httpCallBack));
    }

    private void execute(Request request, IHttpCallBack httpCallBack) {
        Response response = null;
        String body = null;
        String error = null;
        try {
            response = getHttpClient().newCall(request).execute();
            body = response.body().string();
        } catch (IOException e) {
            error = e.getMessage();
        }
        SonnyJackCallBack.executeCallback(response, body, error, httpCallBack);
    }

    /************************************************************************************
     * 下载(start)
     ************************************************************************************/
    public void download(BaseHttpParams httpParams, IHttpCallBack httpCallBack) {
        SonnyJackHttpClient sonnyJackHttpClient = getSonnyJackHttpClient().clone(httpCallBack);
        HttpRequestBody httpRequestBody = buildRequestBody(getSonnyJackHttpClient(), httpParams, httpCallBack);
        if (TextUtils.isEmpty(httpParams.getSaveFilePath())) {
            httpParams.setSaveFilePath(Utils.getRootFolderAbsolutePath());
        }
        if (TextUtils.isEmpty(httpParams.getSaveFileName())) {
            httpParams.setSaveFileName(String.valueOf(System.currentTimeMillis()));
        }
        Request request = RequestHelper.createDownloadRequest(httpRequestBody);
        SonnyJackCallBack.sendBeforeCallBack(httpCallBack);
        sonnyJackHttpClient.getOkHttpClient().newCall(request).enqueue(new SonnyJackCallBack(SonnyJackCallBack.ACTION_DOWNLOAD, httpRequestBody, httpCallBack));
    }

    /************************************************************************************
     * 下载(end)
     ************************************************************************************/


    /************************************************************************************
     * 上传(start)
     ***********************************************************************************/

    public void upload(final BaseHttpParams httpParams, final IHttpCallBack httpCallBack) {
        SonnyJackHttpClient sonnyJackHttpClient = getSonnyJackHttpClient().clone(httpCallBack);
        HttpRequestBody httpRequestBody = buildRequestBody(getSonnyJackHttpClient(), httpParams, httpCallBack);
        Request request = RequestHelper.createUploadRequest(httpRequestBody, httpCallBack);
        SonnyJackCallBack.sendBeforeCallBack(httpCallBack);
        sonnyJackHttpClient.getOkHttpClient().newCall(request).enqueue(new SonnyJackCallBack(SonnyJackCallBack.ACTION_UPLOAD, httpCallBack));
    }

    /************************************************************************************
     * 上传(end)
     ***********************************************************************************/

    /***
     * 构建请求body，包括HttpClient、Handler、BaseHttpParams
     * 该请求body，会存储在 IHttpCallBack
     * 用户可在继承IHttpCallBack的子类中获得该对象
     * @param httpParams
     */
    private HttpRequestBody buildRequestBody(SonnyJackHttpClient sonnyJackHttpClient, BaseHttpParams httpParams, IHttpCallBack httpCallBack) {
        if (null == httpParams) {
            throw new NullPointerException("httpParams is not null");
        }
        if (TextUtils.isEmpty(httpParams.getHttpUrl())) {
            throw new NullPointerException("httpUrl is not null");
        }
        HttpRequestBody httpRequestBody = new HttpRequestBody();
        httpRequestBody.setHandler(mHandler);
        httpRequestBody.setHttpParams(httpParams);
        httpRequestBody.setHttpClient(sonnyJackHttpClient);
        if (null != httpCallBack) {
            httpCallBack.setHttpRequestBody(httpRequestBody);
        }
        return httpRequestBody;
    }

    /***
     * 取消请求  by  tag
     * @param sonnyJackHttpClient
     * @param tag
     */
    public void cancel(SonnyJackHttpClient sonnyJackHttpClient, Object tag) {
        if (null == tag) {
            return;
        }
        for (Call call : sonnyJackHttpClient.getOkHttpClient().dispatcher().queuedCalls()) {
            if (null == call || !call.request().tag().equals(tag)) {
                continue;
            }
            call.cancel();
        }
        for (Call call : sonnyJackHttpClient.getOkHttpClient().dispatcher().runningCalls()) {
            if (null == call || !call.request().tag().equals(tag)) {
                continue;
            }
            call.cancel();
        }
    }
}
