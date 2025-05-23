package com.bizhan.auarai.API;

import org.json.JSONException;
import org.json.JSONObject;

public class APIFetcher {
    private static APIFetcher instance;
    private String googleMapsApi, openWeatherMapApi, geminiApi;

    private APIFetcher() {}

    public static synchronized APIFetcher getInstance() {
        if (instance == null) {
            instance = new APIFetcher();
        }
        return instance;
    }

    public boolean fetchAPI(String json){
        try {
            JSONObject obj = new JSONObject(json);
            googleMapsApi = obj.optString("googleMapsApiKey", "-");
            openWeatherMapApi = obj.optString("openWeatherMapApiKey", "-");
            geminiApi = obj.optString("geminiApiKey", "-");
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getGoogleMapsApi() {
        return googleMapsApi;
    }

    public String getOpenWeatherMapApi() {
        return openWeatherMapApi;
    }

    public String getGeminiApi() {
        return geminiApi;
    }
}
