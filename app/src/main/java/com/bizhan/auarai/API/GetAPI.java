package com.bizhan.auarai.API;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class GetAPI {

    private final String token;
    private final Context context;
    private final APIFetcher fetcher;

    public interface APIResultCallback {
        void onSuccess(boolean parsed);
        void onFailure(String message);
    }

    public GetAPI(Context context, String token, APIFetcher sharedFetcher) {
        this.context = context;
        this.token = token;
        this.fetcher = sharedFetcher;
    }

    public void fetch(APIResultCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://auarai.onrender.com/api/auth/apikeys")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure("Ошибка подключения: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    boolean success = fetcher.fetchAPI(body);
                    callback.onSuccess(success);
                } else {
                    callback.onFailure("Ошибка сервера: " + response.code());
                }
            }
        });
    }
}
