package com.bizhan.auarai.API.openWeatherMapAPI;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class GetWeather {

    private static final OkHttpClient client = new OkHttpClient();

    public static void getWeatherByCoords(double lat, double lon, String api, WeatherDualCallback callback) {
        String forecastUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat
                + "&lon=" + lon
                + "&appid=" + api
                + "&units=metric";

        String currentUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat
                + "&lon=" + lon
                + "&appid=" + api
                + "&units=metric";

        CountDownLatch latch = new CountDownLatch(2);
        final String[] forecastResult = new String[1];
        final String[] currentResult = new String[1];
        final String[] error = new String[1];

        // Forecast Request
        Request forecastRequest = new Request.Builder().url(forecastUrl).build();
        client.newCall(forecastRequest).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                error[0] = e.getMessage();
                latch.countDown();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    forecastResult[0] = response.body().string();
                } else {
                    error[0] = "Forecast response code: " + response.code();
                }
                latch.countDown();
            }
        });

        // Current Weather Request
        Request currentRequest = new Request.Builder().url(currentUrl).build();
        client.newCall(currentRequest).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                error[0] = e.getMessage();
                latch.countDown();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    currentResult[0] = response.body().string();
                } else {
                    error[0] = "Current response code: " + response.code();
                }
                latch.countDown();
            }
        });

        // Wait for both to complete
        new Thread(() -> {
            try {
                latch.await();
                if (error[0] != null) {
                    callback.onFailure(error[0]);
                } else {
                    callback.onSuccess(currentResult[0], forecastResult[0]);
                }
            } catch (InterruptedException e) {
                callback.onFailure("Interrupted: " + e.getMessage());
            }
        }).start();
    }

    public interface WeatherDualCallback {
        void onSuccess(String currentWeatherJson, String forecastJson);
        void onFailure(String error);
    }
}
