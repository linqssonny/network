package com.sonnyjack.library.network.bean;

import android.content.Context;
import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SonnyJack
 * @data 2018/4/17 21:58
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description: the base params for network
 */
public abstract class BaseHttpParams {

    //上下文
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    //是否显示loading，需用户自己实现
    private boolean loading;

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    //请求地址
    private String httpUrl;

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    //Http请求参数
    private Map<String, Object> param = new HashMap<>();

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public void addParam(String name, Object value) {
        if (null == value) {
            return;
        }
        param.put(name, value.toString());
    }

    //Http请求头
    private Map<String, Object> headers = new HashMap<>();

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public void addheader(String name, Object value) {
        if (null == value) {
            return;
        }
        headers.put(name, value.toString());
    }

    public String getParamString() {
        StringBuffer stringBuffer = new StringBuffer();
        if (null != param && param.size() > 0) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                if (null == entry) {
                    continue;
                }
                if (stringBuffer.length() > 0) {
                    stringBuffer.append("&");
                }
                stringBuffer.append(entry.getKey()).append("=").append(entry.getValue().toString());
            }
        }
        return stringBuffer.toString();
    }

    //请求Tag
    private Object tag;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    //是否主线程回调(默认主线程回调)
    private boolean asyncBack;

    public boolean isAsyncBack() {
        return asyncBack;
    }

    public void setAsyncBack(boolean asyncBack) {
        this.asyncBack = asyncBack;
    }

    //保存文件路径
    private String saveFilePath;

    public String getSaveFilePath() {
        return saveFilePath;
    }

    public void setSaveFilePath(String saveFilePath) {
        this.saveFilePath = saveFilePath;
    }

    //保存文件名称
    private String saveFileName;

    public String getSaveFileName() {
        return saveFileName;
    }

    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    //Handler
    private Handler handler;

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("context = " + (null == context ? "null" : context));
        stringBuilder.append("; loading = " + loading);
        stringBuilder.append("; httpUrl = " + httpUrl);
        stringBuilder.append("; param = {" + getParamString() + "}");
        stringBuilder.append("; tag = " + (null == tag ? "null" : tag.toString()));
        stringBuilder.append("; asyncBack = " + asyncBack);
        stringBuilder.append("; saveFilePath = " + (null == saveFilePath ? "" : saveFilePath));
        stringBuilder.append("; saveFileName = " + (null == saveFileName ? "" : saveFileName));
        stringBuilder.append("; handler = " + (null == handler ? "" : handler));
        return stringBuilder.toString();
    }
}
