package com.sonnyjack.library.network.http;

import android.text.TextUtils;

import com.sonnyjack.library.network.constant.HttpCode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author SonnyJack
 * @data 2018/4/25 16:27
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description:
 */

public class SonnyJackCallBack implements okhttp3.Callback {

    public static final int ACTION_GET = 1, ACTION_POST = 2, ACTION_DOWNLOAD = 3, ACTION_UPLOAD = 4;

    private IHttpCallBack mIHttpCallBack;
    private HttpRequestBody mHttpRequestBody;
    private int mAction = ACTION_GET;

    public SonnyJackCallBack(int action, IHttpCallBack httpCallBack) {
        mAction = action;
        mIHttpCallBack = httpCallBack;
    }

    public SonnyJackCallBack(int action, HttpRequestBody httpRequestBody, IHttpCallBack httpCallBack) {
        mAction = action;
        mHttpRequestBody = httpRequestBody;
        mIHttpCallBack = httpCallBack;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        switch (mAction) {
            case ACTION_GET:
            case ACTION_POST:
            case ACTION_DOWNLOAD:
            case ACTION_UPLOAD:
                if (call.isCanceled()) {
                    sendFailCallBack(mIHttpCallBack, HttpCode.HTTP_CODE_ERROR_CANCEL, "request is cancel");
                } else {
                    sendFailCallBack(mIHttpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, e.getMessage());
                }
                break;
        }
        sendAfterCallBack(mIHttpCallBack);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()) {
            switch (mAction) {
                case ACTION_GET:
                case ACTION_POST:
                case ACTION_UPLOAD:
                    try {
                        String body = response.body().string();
                        sendSuccessCallBack(mIHttpCallBack, body);
                    } catch (Exception e) {
                        sendFailCallBack(mIHttpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, e.getMessage());
                    }
                    break;
                case ACTION_DOWNLOAD:
                    InputStream is = null;
                    try {
                        //获取返回体（流的形式）
                        is = response.body().byteStream();
                        boolean success = Utils.saveFile(is, mHttpRequestBody.getHttpParams().getSaveFilePath(), mHttpRequestBody.getHttpParams().getSaveFileName());
                        if (success) {
                            String filePath = new File(mHttpRequestBody.getHttpParams().getSaveFilePath(), mHttpRequestBody.getHttpParams().getSaveFileName()).getAbsolutePath();
                            sendSuccessCallBack(mIHttpCallBack, filePath);
                        } else {
                            sendFailCallBack(mIHttpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, "save file fail");
                        }
                    } catch (Exception e) {
                        sendFailCallBack(mIHttpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, e.getMessage());
                    } finally {
                        Utils.closeInputStream(is);
                    }
                    break;
            }
        } else {
            sendFailCallBack(mIHttpCallBack, response.code(), response.message());
        }
        sendAfterCallBack(mIHttpCallBack);
    }

    public static void executeCallback(Response response, String body, String error, IHttpCallBack httpCallBack) {
        if (null == response || TextUtils.isEmpty(body)) {
            sendFailCallBack(httpCallBack, HttpCode.HTTP_CODE_ERROR_NORMAL, error);
        } else if (!response.isSuccessful()) {
            sendFailCallBack(httpCallBack, response.code(), response.message());
        } else {
            sendSuccessCallBack(httpCallBack, body);
        }
        sendAfterCallBack(httpCallBack);
    }

    /***
     * 请求前调用
     *
     * @param httpCallBack
     */
    public static void sendBeforeCallBack(IHttpCallBack httpCallBack) {
        if (null == httpCallBack) {
            return;
        }
        httpCallBack.getHandler().post(() -> httpCallBack.before());
    }

    /***
     * 请求结束后调用
     *
     * @param httpCallBack
     */
    public static void sendAfterCallBack(IHttpCallBack httpCallBack) {
        if (null == httpCallBack) {
            return;
        }
        httpCallBack.getHandler().post(() -> httpCallBack.after());
    }

    /***
     * 请求失败后调用
     *
     * @param httpCallBack
     * @param message
     */
    public static void sendFailCallBack(IHttpCallBack httpCallBack, int error, String message) {
        if (null == httpCallBack) {
            return;
        }
        if (httpCallBack.getBaseHttpParams().isAsyncBack()) {
            httpCallBack.fail(error, message);
            return;
        }
        httpCallBack.getHandler().post(() -> httpCallBack.fail(error, message));
    }

    /****
     * 请求成功后调用
     *
     * @param httpCallBack
     * @param body
     */
    public static void sendSuccessCallBack(IHttpCallBack httpCallBack, String body) {
        if (null == httpCallBack) {
            return;
        }
        if (httpCallBack.getBaseHttpParams().isAsyncBack()) {
            httpCallBack.success(body);
            return;
        }
        httpCallBack.getHandler().post(() -> httpCallBack.success(body));
    }
}
