package com.sonnyjack.library.network;


import com.sonnyjack.library.network.interfaces.BaseHttpParams;
import com.sonnyjack.library.network.interfaces.IHttpCallBack;

/**
 * @author SonnyJack
 * @data 2018/4/17 21:58
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description: the class of listener for download progress
 */
class ProgressListener {

    private BaseHttpParams mBaseHttpParams;
    private IHttpCallBack mIHttpCallBack;

    public ProgressListener(BaseHttpParams httpParams, IHttpCallBack httpCallBack) {
        this.mBaseHttpParams = httpParams;
        this.mIHttpCallBack = httpCallBack;
    }

    public void onProgress(final long bytesRead, final long contentLength, final boolean finish) {
        if (null != mIHttpCallBack) {
            mBaseHttpParams.getHandler().post(() -> mIHttpCallBack.onProgress(mBaseHttpParams, bytesRead, contentLength, finish));
        }
    }
}
