package com.sonnyjack.library.network;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sonnyjack.library.network.bean.BaseHttpParams;
import com.sonnyjack.library.network.constant.HttpCode;
import com.sonnyjack.library.network.interfaces.IHttpCallBack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author SonnyJack
 * @data 2018/4/17 21:58
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description: the http utils for network
 */
public class HttpUtils {

    private static class HttpUtilsHolder {
        private static HttpUtils instance = new HttpUtils();
    }

    public static HttpUtils getInstance() {
        return HttpUtilsHolder.instance;
    }

    private OkHttpClient mOkHttpClient = null;
    private long DEFAULT_TIME = 30;//默认http请求时间30秒
    private long DEFAULT_DOWNLOAD_TIME = 30 * 60;//默认下载超时30分钟

    private Handler mHandler;

    private HttpUtils() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIME, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIME, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIME, TimeUnit.SECONDS).build();

        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 设置时间
     *
     * @param connectionTime 连接时间(读取时间)
     * @param downloadTime   下载最大时长
     */
    public void setHttpConnectionTimeAndDownloadTime(long connectionTime, long downloadTime) {
        DEFAULT_TIME = connectionTime;
        DEFAULT_DOWNLOAD_TIME = downloadTime;
    }

    /***
     * post  同步请求
     *
     * @param httpParams
     * @param httpCallBack
     * @return
     */
    public String post(final BaseHttpParams httpParams, final IHttpCallBack httpCallBack) {
        checkParams(httpParams);
        sendOnBeforeCallBack(httpParams, httpCallBack);
        Request request = createPostRequest(httpParams);
        Response response = null;
        String body = null;
        String error = null;
        try {
            response = mOkHttpClient.newCall(request).execute();
            body = response.body().string();
        } catch (Exception e) {
            error = e.getMessage();
        }
        if (null == response || TextUtils.isEmpty(body)) {
            sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, error);
        } else if (!response.isSuccessful()) {
            sendFailCallBack(httpParams, httpCallBack, response.code(), response.message());
        } else {
            sendSuccessCallBack(httpParams, httpCallBack, body);
        }
        sendOnAfterCallBack(httpParams, httpCallBack);
        return body;
    }

    /***
     * post  异步请求
     *
     * @param httpParams
     * @param httpCallBack
     */
    public void postAsync(final BaseHttpParams httpParams, final IHttpCallBack httpCallBack) {
        checkParams(httpParams);
        sendOnBeforeCallBack(httpParams, httpCallBack);
        Request request = createPostRequest(httpParams);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (call.isCanceled()) {
                    sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_CANCEL, "request is cancel");
                } else {
                    sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, e.getMessage());
                }
                sendOnAfterCallBack(httpParams, httpCallBack);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string();
                        sendSuccessCallBack(httpParams, httpCallBack, body);
                    } catch (Exception e) {
                        sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, e.getMessage());
                    }
                } else {
                    sendFailCallBack(httpParams, httpCallBack, response.code(), response.message());
                }
                sendOnAfterCallBack(httpParams, httpCallBack);
            }
        });
    }

    /***
     * 创建post参数
     *
     * @param httpParams
     * @return
     */
    private Request createPostRequest(BaseHttpParams httpParams) {
        FormBody.Builder builder = new FormBody.Builder();
        if (null != httpParams.getParam() && httpParams.getParam().size() > 0) {
            for (Map.Entry<String, Object> map : httpParams.getParam().entrySet()) {
                if (null == map || null == map.getKey()) {
                    continue;
                }
                String value = null == map.getValue() ? "" : map.getValue().toString();
                builder.add(map.getKey(), value);
            }
        }
        FormBody formBody = builder.build();
        Request.Builder b = new Request.Builder();
        b.url(httpParams.getHttpUrl());
        if (null != httpParams.getTag()) {
            b.tag(httpParams.getTag());
        }
        b.post(formBody);
        addHeaders(b, httpParams);
        return b.build();
    }

    /***
     * get  同步请求
     *
     * @param httpParams
     * @param httpCallBack
     * @return
     */
    public String get(final BaseHttpParams httpParams, final IHttpCallBack httpCallBack) {
        checkParams(httpParams);
        sendOnBeforeCallBack(httpParams, httpCallBack);
        Request request = createGetRequest(httpParams);
        Response response = null;
        String body = null;
        String error = null;
        try {
            response = mOkHttpClient.newCall(request).execute();
            body = response.body().string();
        } catch (IOException e) {
            error = e.getMessage();
        }
        if (null == response || TextUtils.isEmpty(body)) {
            sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, error);
        } else if (!response.isSuccessful()) {
            sendFailCallBack(httpParams, httpCallBack, response.code(), response.message());
        } else {
            sendSuccessCallBack(httpParams, httpCallBack, body);
        }
        sendOnAfterCallBack(httpParams, httpCallBack);
        return body;
    }

    /***
     * get  异步请求
     *
     * @param httpParams
     * @param httpCallBack
     */
    public void getAsync(final BaseHttpParams httpParams, final IHttpCallBack httpCallBack) {
        checkParams(httpParams);
        sendOnBeforeCallBack(httpParams, httpCallBack);
        Request request = createGetRequest(httpParams);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (call.isCanceled()) {
                    sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_CANCEL, "request is cancel");
                } else {
                    sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, e.getMessage());
                }
                sendOnAfterCallBack(httpParams, httpCallBack);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string();
                        sendSuccessCallBack(httpParams, httpCallBack, body);
                    } catch (Exception e) {
                        sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, e.getMessage());
                    }
                } else {
                    sendFailCallBack(httpParams, httpCallBack, response.code(), response.message());
                }
                sendOnAfterCallBack(httpParams, httpCallBack);
            }
        });
    }

    /***
     * get
     *
     * @param httpParams
     * @return
     */
    private Request createGetRequest(BaseHttpParams httpParams) {
        StringBuffer stringBuffer = new StringBuffer(httpParams.getHttpUrl());
        if (null != httpParams.getParam() && httpParams.getParam().size() > 0) {
            for (Map.Entry<String, Object> map : httpParams.getParam().entrySet()) {
                int index = stringBuffer.indexOf("?");
                if (index > -1) {
                    stringBuffer.append("&");
                } else {
                    stringBuffer.append("?");
                }
                String value = null == map.getValue() ? "" : map.getValue().toString();
                stringBuffer.append(map.getKey()).append("=").append(value);
            }
        }
        Request.Builder b = new Request.Builder();
        b.url(stringBuffer.toString());
        if (null != httpParams.getTag()) {
            b.tag(httpParams.getTag());
        }
        addHeaders(b, httpParams);
        return b.build();
    }

    /************************************************************************************
     * 下载(start)
     ************************************************************************************/
    public void download(final BaseHttpParams httpParams, final IHttpCallBack httpCallBack) {
        checkParams(httpParams);
        checkSaveFile(httpParams);
        Request request = createDownloadRequest(httpParams);
        OkHttpClient okHttpClient = mOkHttpClient.newBuilder()
                .readTimeout(DEFAULT_DOWNLOAD_TIME, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_DOWNLOAD_TIME, TimeUnit.SECONDS)
                .addInterceptor(new ResponseInterceptor(httpParams, httpCallBack)).build();
        sendOnBeforeCallBack(httpParams, httpCallBack);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (call.isCanceled()) {
                    sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_CANCEL, "request is cancel");
                } else {
                    sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, e.getMessage());
                }
                sendOnAfterCallBack(httpParams, httpCallBack);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    InputStream is = null;
                    try {
                        //获取返回体（流的形式）
                        is = response.body().byteStream();
                        boolean success = Utils.saveFile(is, httpParams.getSaveFilePath(), httpParams.getSaveFileName());
                        if (success) {
                            String filePath = new File(httpParams.getSaveFilePath(), httpParams.getSaveFileName()).getAbsolutePath();
                            sendSuccessCallBack(httpParams, httpCallBack, filePath);
                        } else {
                            sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, "save file fail");
                        }
                    } catch (Exception e) {
                        sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, e.getMessage());
                    } finally {
                        Utils.closeInputStream(is);
                    }
                } else {
                    sendFailCallBack(httpParams, httpCallBack, response.code(), response.message());
                }
                sendOnAfterCallBack(httpParams, httpCallBack);
            }
        });
    }

    private void checkSaveFile(BaseHttpParams httpParams) {
        if (TextUtils.isEmpty(httpParams.getSaveFilePath())) {
            httpParams.setSaveFilePath(Utils.getRootFolderAbsolutePath());
        }
        if (TextUtils.isEmpty(httpParams.getSaveFileName())) {
            httpParams.setSaveFileName(String.valueOf(System.currentTimeMillis()));
        }
    }

    private Request createDownloadRequest(BaseHttpParams httpParams) {
        Request.Builder builder = new Request.Builder();
        builder.url(httpParams.getHttpUrl());
        if (null != httpParams.getTag()) {
            builder.tag(httpParams.getTag());
        }
        addHeaders(builder, httpParams);
        return builder.build();
    }

    /************************************************************************************
     * 下载(end)
     ************************************************************************************/


    /************************************************************************************
     * 上传(start)
     ***********************************************************************************/

    public void upload(final BaseHttpParams httpParams, final IHttpCallBack httpCallBack) {
        checkParams(httpParams);
        Request request = createUploadRequest(httpParams, httpCallBack);
        OkHttpClient okHttpClient = mOkHttpClient.newBuilder()
                .writeTimeout(DEFAULT_DOWNLOAD_TIME, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_DOWNLOAD_TIME, TimeUnit.SECONDS)
                .build();
        sendOnBeforeCallBack(httpParams, httpCallBack);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (call.isCanceled()) {
                    sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_CANCEL, "request is cancel");
                } else {
                    sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, e.getMessage());
                }
                sendOnAfterCallBack(httpParams, httpCallBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string();
                        sendSuccessCallBack(httpParams, httpCallBack, body);
                    } catch (Exception e) {
                        sendFailCallBack(httpParams, httpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, e.getMessage());
                    }
                } else {
                    sendFailCallBack(httpParams, httpCallBack, response.code(), response.message());
                }
                sendOnAfterCallBack(httpParams, httpCallBack);
            }
        });
    }

    private Request createUploadRequest(BaseHttpParams httpParams, IHttpCallBack httpCallBack) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (null != httpParams.getParam() && httpParams.getParam().size() > 0) {
            File file = null;
            for (Map.Entry<String, Object> map : httpParams.getParam().entrySet()) {
                if (null == map) {
                    continue;
                }
                if (map.getValue() instanceof File) {
                    //添加文件
                    file = (File) map.getValue();
                    if (null == file || !file.exists()) {
                        continue;
                    }
                    String fileName = file.getName();
                    builder.addFormDataPart(map.getKey(), fileName, RequestBody.create(MediaType.parse(Utils.guessMimeType(fileName)), file));
                } else {
                    //添加普通参数
                    String value = null == map.getValue() ? "" : map.getValue().toString();
                    builder.addFormDataPart(map.getKey(), value);
                }
            }
        }
        RequestBody requestBody = builder.build();
        Request.Builder b = new Request.Builder();
        b.url(httpParams.getHttpUrl());
        if (null != httpParams.getTag()) {
            b.tag(httpParams.getTag());
        }
        b.post(new ProgressRequestBody(requestBody, new ProgressListener(httpParams, httpCallBack)));
        addHeaders(b, httpParams);
        return b.build();
    }

    /************************************************************************************
     * 上传(end)
     ***********************************************************************************/

    /***
     * 请求前检查参数
     *
     * @param httpParams
     */
    private void checkParams(BaseHttpParams httpParams) {
        if (null == httpParams) {
            throw new NullPointerException("httpParams is not null");
        }
        if (TextUtils.isEmpty(httpParams.getHttpUrl())) {
            throw new NullPointerException("httpUrl is not null");
        }
        httpParams.setHandler(mHandler);
    }

    /**
     * 添加Header
     *
     * @param builder
     * @param httpParams
     */
    private void addHeaders(Request.Builder builder, BaseHttpParams httpParams) {
        if (null == builder) {
            return;
        }
        if (null == httpParams) {
            return;
        }
        if (null == httpParams.getHeaders() || httpParams.getHeaders().isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : httpParams.getHeaders().entrySet()) {
            if (null == entry) {
                continue;
            }
            String headerName = entry.getKey();
            if (TextUtils.isEmpty(headerName)) {
                continue;
            }
            String headerValue = (null == entry.getValue() ? "" : entry.getValue().toString());
            builder.addHeader(headerName, headerValue);
        }
    }

    /***
     * 请求前调用
     *
     * @param httpParams
     * @param httpCallBack
     */
    private void sendOnBeforeCallBack(final BaseHttpParams httpParams, final IHttpCallBack httpCallBack) {
        if (null != httpCallBack) {
            mHandler.post(() -> httpCallBack.onBefore(httpParams));
        }
    }

    /***
     * 请求结束后调用
     *
     * @param httpParams
     * @param httpCallBack
     */
    private void sendOnAfterCallBack(final BaseHttpParams httpParams, final IHttpCallBack httpCallBack) {
        if (null != httpCallBack) {
            mHandler.post(() -> httpCallBack.onAfter(httpParams));
        }
    }

    /***
     * 请求失败后调用
     *
     * @param httpParams
     * @param httpCallBack
     * @param message
     */
    private void sendFailCallBack(BaseHttpParams httpParams, IHttpCallBack httpCallBack, int error, String message) {
        if (null != httpCallBack) {
            if (httpParams.isAsyncBack()) {
                httpCallBack.onFail(httpParams, error, message);
                return;
            }
            mHandler.post(() -> httpCallBack.onFail(httpParams, error, message));
        }
    }

    /****
     * 请求成功后调用
     *
     * @param httpParams
     * @param httpCallBack
     * @param body
     */
    private void sendSuccessCallBack(final BaseHttpParams httpParams, final IHttpCallBack httpCallBack, final String body) {
        if (null != httpCallBack) {
            if (httpParams.isAsyncBack()) {
                httpCallBack.onSuccess(httpParams, body);
                return;
            }
            mHandler.post(() -> httpCallBack.onSuccess(httpParams, body));
        }
    }

    /***
     * 取消请求  by  tag
     *
     * @param tag
     */
    public void cancel(Object tag) {
        if (null == tag) {
            return;
        }
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (null == call || !call.request().tag().equals(tag)) {
                continue;
            }
            call.cancel();
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (null == call || !call.request().tag().equals(tag)) {
                continue;
            }
            call.cancel();
        }
    }
}
