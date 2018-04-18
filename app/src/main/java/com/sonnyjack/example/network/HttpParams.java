package com.sonnyjack.example.network;

import com.sonnyjack.library.network.interfaces.BaseHttpParams;

/**
 * @author SonnyJack
 * @data 2018/4/18 13:58
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description: http params
 */
public class HttpParams extends BaseHttpParams {

    //是否要解析返回结果
    private boolean isAnalysisResult;

    public boolean isAnalysisResult() {
        return isAnalysisResult;
    }

    public void setAnalysisResult(boolean analysisResult) {
        isAnalysisResult = analysisResult;
    }

    //显示错误信息
    private boolean showErrorMessage;

    public boolean isShowErrorMessage() {
        return showErrorMessage;
    }

    public void setShowErrorMessage(boolean showErrorMessage) {
        this.showErrorMessage = showErrorMessage;
    }
}
