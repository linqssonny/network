package com.sonnyjack.example.network.bean;

import com.sonnyjack.example.network.bean.Data;

/**
 * @author SonnyJack
 * @data 2018/4/18 15:05
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description:
 */
public class Result2 {
    private Data data;
    private String status;
    private String desc;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
