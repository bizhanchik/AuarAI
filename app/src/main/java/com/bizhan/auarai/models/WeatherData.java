package com.bizhan.auarai.models;

public class WeatherData {
    public double currentTemp;
    public double feelsLike;
    public int humidity;
    public int visibility;
    public double windSpeed;
    public double minTempToday;
    public double maxTempToday;
    public double pop;
    public String cityName;
    public String description;
    public String icon;

    public WeatherData(double currentTemp, double feelsLike, int humidity, int visibility,
                       double windSpeed, double minTempToday, double maxTempToday, double pop, String cityName, String description, String icon) {
        this.currentTemp = currentTemp;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.visibility = visibility;
        this.windSpeed = windSpeed;
        this.minTempToday = minTempToday;
        this.maxTempToday = maxTempToday;
        this.pop = pop;
        this.cityName = cityName;
        this.description = description;
        this.icon = icon;
    }
}
