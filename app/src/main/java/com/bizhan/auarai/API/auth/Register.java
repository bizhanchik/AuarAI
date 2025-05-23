package com.bizhan.auarai.API.auth;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register {
    public interface RegisterCallback {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static void register(Context context, String email, String password, RegisterCallback callback){
        OkHttpClient okHttpClient = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e){
            e.printStackTrace();
            callback.onFailure("Error forming JSON");
            return;
        }
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        Request request = new Request.Builder()
                .url("https://auarai.onrender.com/api/auth/register")
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String msg = jsonObject.optString("msg", "Регистрация прошла успешно");
                        callback.onSuccess(msg);
                    } catch (Exception e){
                        e.printStackTrace();
                        callback.onFailure("Ошибка парсинга ответа");
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String errorMsg = jsonObject.optString("msg", "Ошибка регистрации: " + response.code());
                        callback.onFailure(errorMsg);
                    } catch (Exception e) {
                        callback.onFailure("Ошибка регистрации: " + response.code());
                    }
                }
            }
        });
    }
}
