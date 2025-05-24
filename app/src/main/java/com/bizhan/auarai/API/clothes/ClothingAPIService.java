package com.bizhan.auarai.API.clothes;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bizhan.auarai.models.ClothingItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ClothingAPIService {
    private static final String TAG = "ClothingAPIService";
    private final String token;
    private final Context context;

    public interface ClothingCallback {
        void onSuccess(List<ClothingItem> clothingItems);
        void onFailure(String message);
    }

    public ClothingAPIService(Context context, String token) {
        this.context = context;
        this.token = token;
        Log.d(TAG, "Initialized with token: " + (token != null ? "present" : "null"));
    }

    public void fetchClothingItems(ClothingCallback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://auarai.onrender.com/api/addcloth";
        Log.d(TAG, "Fetching clothing items from: " + url);
        
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Network error: " + e.getMessage());
                callback.onFailure("Ошибка подключения: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    Log.d(TAG, "Response body: " + body);
                    List<ClothingItem> clothingItems = parseClothingItems(body);
                    if (clothingItems != null) {
                        Log.d(TAG, "Successfully parsed " + clothingItems.size() + " items");
                        callback.onSuccess(clothingItems);
                    } else {
                        Log.e(TAG, "Failed to parse response");
                        callback.onFailure("Ошибка парсинга данных");
                    }
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "No error body";
                    Log.e(TAG, "Server error: " + response.code() + ", Body: " + errorBody);
                    callback.onFailure("Ошибка сервера: " + response.code());
                }
            }
        });
    }

    private List<ClothingItem> parseClothingItems(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            List<ClothingItem> clothingItems = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemJson = jsonArray.getJSONObject(i);
                ClothingItem item = parseClothingItem(itemJson);
                if (item != null) {
                    clothingItems.add(item);
                }
            }

            return clothingItems;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ClothingItem parseClothingItem(JSONObject itemJson) {
        try {
            ClothingItem item = new ClothingItem();

            item.set_id(itemJson.optString("_id", ""));
            item.setName(itemJson.optString("name", ""));
            item.setBrand(itemJson.optString("brand", ""));
            item.setCategory(itemJson.optString("category", ""));
            item.setGender(itemJson.optString("gender", ""));
            item.setMaterial(itemJson.optString("material", ""));
            item.setDescription(itemJson.optString("description", ""));
            item.setImageURL(itemJson.optString("imageURL", ""));
            item.setStoreName(itemJson.optString("storeName", ""));
            item.setStoreURL(itemJson.optString("storeURL", ""));
            item.setProductURL(itemJson.optString("productURL", ""));
            item.setPrice(itemJson.optDouble("price", 0.0));
            item.setAvailable(itemJson.optBoolean("available", true));
            item.setUpdatedAt(itemJson.optString("updatedAt", ""));

            item.setColor(parseStringArray(itemJson.optJSONArray("color")));
            item.setSize(parseStringArray(itemJson.optJSONArray("size")));
            item.setTags(parseStringArray(itemJson.optJSONArray("tags")));
            item.setOccasions(parseStringArray(itemJson.optJSONArray("occasions")));
            item.setWeatherSuitability(parseStringArray(itemJson.optJSONArray("weatherSuitability")));

            item.setAiGeneratedStyleEmbedding(parseDoubleArray(itemJson.optJSONArray("aiGeneratedStyleEmbedding")));

            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> parseStringArray(JSONArray jsonArray) {
        List<String> stringList = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    stringList.add(jsonArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringList;
    }

    private List<Double> parseDoubleArray(JSONArray jsonArray) {
        List<Double> doubleList = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    doubleList.add(jsonArray.getDouble(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return doubleList;
    }
}