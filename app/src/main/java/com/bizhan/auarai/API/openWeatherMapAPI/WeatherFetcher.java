package com.bizhan.auarai.API.openWeatherMapAPI;

import com.bizhan.auarai.API.openWeatherMapAPI.model.WeatherData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherFetcher {

    private static WeatherFetcher instance;

    private WeatherFetcher() {}

    public static synchronized WeatherFetcher getInstance() {
        if (instance == null) {
            instance = new WeatherFetcher();
        }
        return instance;
    }

    public WeatherData fetchWeatherAPI(String currentWeatherInfo, String weatherForecastInfo) {
        try {
            JSONObject currentRoot = new JSONObject(currentWeatherInfo);
            JSONObject mainCurrent = currentRoot.getJSONObject("main");

            double temp = mainCurrent.getDouble("temp");
            double feels_like = mainCurrent.getDouble("feels_like");
            int humidity = mainCurrent.getInt("humidity");
            int visibility = currentRoot.optInt("visibility", -1);
            String description = currentRoot.getJSONArray("weather").getJSONObject(0).getString("description");

            JSONObject wind = currentRoot.getJSONObject("wind");
            double wind_speed = wind.getDouble("speed");

            JSONObject forecastRoot = new JSONObject(weatherForecastInfo);
            JSONArray list = forecastRoot.getJSONArray("list");
            String cityName = forecastRoot.getJSONObject("city").getString("name");

            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            double minTemp = Double.MAX_VALUE;
            double maxTemp = Double.MIN_VALUE;
            double pop = 0;

            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                String dt_txt = item.getString("dt_txt");

                if (dt_txt.startsWith(today)) {
                    JSONObject itemMain = item.getJSONObject("main");
                    double tempForecast = itemMain.getDouble("temp");

                    minTemp = Math.min(minTemp, tempForecast);
                    maxTemp = Math.max(maxTemp, tempForecast);

                    if (item.has("pop")) {
                        double currentPop = item.getDouble("pop");
                        pop = Math.max(pop, currentPop);
                    }
                }
            }

            return new WeatherData(temp, feels_like, humidity, visibility, wind_speed, minTemp, maxTemp, pop, cityName,description);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
