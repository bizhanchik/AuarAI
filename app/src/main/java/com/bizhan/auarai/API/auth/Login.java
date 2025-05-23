package com.bizhan.auarai.API.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login {

    public interface LoginCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String TOKEN_KEY = "auth_token";

    public static void login(Context context, String email, String password, LoginCallback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();

        String json = "{"
                + "\"email\":\"" + email + "\","
                + "\"password\":\"" + password + "\""
                + "}";

        RequestBody body = RequestBody.create(json, JSON);


        Request request = new Request.Builder()
                .url("https://auarai.onrender.com/api/auth/login")
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String responceBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responceBody);
                        String token = jsonObject.getString("token");

                        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        prefs.edit().putString(TOKEN_KEY, token).apply();

                        callback.onSuccess();
                    }catch (Exception e){
                        callback.onFailure("Parsing error");
                    }
                }else {
                    callback.onFailure("Request failed with code: " + response.code());
                }
            }
        });
    }
    public static String getToken(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(TOKEN_KEY, null);
    }
}
