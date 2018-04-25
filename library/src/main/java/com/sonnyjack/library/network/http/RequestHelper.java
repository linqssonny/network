package com.sonnyjack.library.network.http;

import android.text.TextUtils;

import com.sonnyjack.library.network.bean.BaseHttpParams;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author SonnyJack
 * @data 2018/4/25 16:56
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description:
 */

class RequestHelper {
    /***
     * 创建post请求Request
     *
     * @param httpRequestBody
     * @return
     */
    public static Request createPostRequest(HttpRequestBody httpRequestBody) {
        BaseHttpParams httpParams = httpRequestBody.getHttpParams();
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
     * 创建get请求Request
     *
     * @param httpRequestBody
     * @return
     */
    public static Request createGetRequest(HttpRequestBody httpRequestBody) {
        BaseHttpParams httpParams = httpRequestBody.getHttpParams();
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

    /**
     * 创建download请求Request
     *
     * @param httpRequestBody
     * @return
     */
    public static Request createDownloadRequest(HttpRequestBody httpRequestBody) {
        BaseHttpParams httpParams = httpRequestBody.getHttpParams();
        Request.Builder builder = new Request.Builder();
        builder.url(httpParams.getHttpUrl());
        if (null != httpParams.getTag()) {
            builder.tag(httpParams.getTag());
        }
        addHeaders(builder, httpParams);
        return builder.build();
    }

    /**
     * 创建upload请求Request
     *
     * @param httpRequestBody
     * @return
     */
    public static Request createUploadRequest(HttpRequestBody httpRequestBody, IHttpCallBack httpCallBack) {
        BaseHttpParams httpParams = httpRequestBody.getHttpParams();
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
        b.post(new ProgressRequestBody(requestBody, new ProgressListener(httpCallBack)));
        addHeaders(b, httpParams);
        return b.build();
    }

    /**
     * 添加Header
     *
     * @param builder
     * @param httpParams
     */
    private static void addHeaders(Request.Builder builder, BaseHttpParams httpParams) {
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
}
