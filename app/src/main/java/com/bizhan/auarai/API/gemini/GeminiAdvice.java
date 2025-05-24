package com.bizhan.auarai.API.gemini;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bizhan.auarai.API.APIFetcher;
import com.bizhan.auarai.API.GetAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class GeminiAdvice {
    private static final String TAG = "GeminiAdvice";
    private final String token;
    private final Context context;
    private final JSONObject body;

    public interface GeminiCallback{
        void onSuccess(String answer);
        void onFailure(String message);
    }

    public GeminiAdvice(Context context, String token, JSONObject body) {
        this.context = context;
        this.token = token;
        this.body = body;
    }

    public void getGeminiAdvice(GeminiCallback callback) {
        APIFetcher fetcher = APIFetcher.getInstance();
        OkHttpClient client = new OkHttpClient();

        String url = "https://auarai.onrender.com/api/ask_gemini";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(body.toString(), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Network error: " + e.getMessage());
                callback.onFailure("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : null;

                if (!response.isSuccessful() || responseBody == null) {
                    callback.onFailure("Request failed with status: " + response.code());
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String answer = jsonObject.optString("answer", null);

                    if (answer != null && !answer.isEmpty()) {
                        callback.onSuccess(answer);
                    } else if (jsonObject.has("msg")) {
                        callback.onFailure(jsonObject.getString("msg"));
                    } else {
                        callback.onFailure("Unexpected response format");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFailure("Error parsing response: " + e.getMessage());
                }
            }
        });
    }
}
