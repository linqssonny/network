package com.sonnyjack.library.network.http;


/**
 * @author SonnyJack
 * @data 2018/4/17 21:58
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description: the class of listener for download progress
 */
class ProgressListener {

    private IHttpCallBack mIHttpCallBack;

    public ProgressListener(IHttpCallBack httpCallBack) {
        this.mIHttpCallBack = httpCallBack;
    }

    public void onProgress(final long bytesRead, final long contentLength, final boolean finish) {
        if (null != mIHttpCallBack) {
            mIHttpCallBack.getHandler().post(() -> mIHttpCallBack.progress(bytesRead, contentLength, finish));
        }
    }
}
