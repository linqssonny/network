package com.sonnyjack.example.network.bean;

import java.util.List;

/**
 * @author SonnyJack
 * @data 2018/4/18 15:06
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description:
 */
public class Data {
    private String aqi;
    private String city;
    private String ganmao;
    private String wendu;
    private List<Wheather> forecast;
    private Wheather yesterday;

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGanmao() {
        return ganmao;
    }

    public void setGanmao(String ganmao) {
        this.ganmao = ganmao;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public List<Wheather> getForecast() {
        return forecast;
    }

    public void setForecast(List<Wheather> forecast) {
        this.forecast = forecast;
    }

    public Wheather getYesterday() {
        return yesterday;
    }

    public void setYesterday(Wheather yesterday) {
        this.yesterday = yesterday;
    }
}
