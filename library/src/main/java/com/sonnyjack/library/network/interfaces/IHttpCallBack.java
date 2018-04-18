package com.sonnyjack.library.network.interfaces;


/**
 * @author SonnyJack
 * @data 2018/4/17 21:58
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description: the base callback for network
 */
public interface IHttpCallBack {

    void onBefore(BaseHttpParams httpParams);

    void onProgress(BaseHttpParams httpParams, long bytesRead, long contentLength, boolean finish);

    void onAfter(BaseHttpParams httpParams);

    void onFail(BaseHttpParams httpParams, int error, String message);

    void onSuccess(BaseHttpParams httpParams, String body);
}
