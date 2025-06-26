package com.bizhan.auarai.fragments.wardrobe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bizhan.auarai.API.auth.Login;
import com.bizhan.auarai.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddItemActivity extends AppCompatActivity {

    private ImageView imageHolder;
    private AutoCompleteTextView actvCategory;
    private MaterialButton btnAgain, btnSave;
    private Bitmap capturedBitmap = null;
    private String token;


    private final String[] categories = {
            "Tops", "Bottoms", "Shoes", "Accessories", "Sportswear",
            "Dresses", "Outerwear", "Underwear", "Swimwear", "Formal Wear"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        imageHolder = findViewById(R.id.imageView);
        actvCategory = findViewById(R.id.actvCategory);
        btnAgain = findViewById(R.id.btnAgain);
        btnSave = findViewById(R.id.btnSave);

        token = Login.getToken(this);


        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(Arrays.asList(categories))
        );
        actvCategory.setAdapter(categoryAdapter);
        actvCategory.setThreshold(1);

        byte[] byteArray = getIntent().getByteArrayExtra("captured_image");
        if (byteArray != null) {
            capturedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageHolder.setImageBitmap(capturedBitmap);
        }

        btnAgain.setOnClickListener(v -> {
            Intent intent = new Intent(AddItemActivity.this, CameraActivity.class);
            startActivity(intent);
            finish();
        });

        btnSave.setOnClickListener(v -> {
            List<String> emptyFields = new ArrayList<>();

            String name = findText(R.id.etName);
            String category = actvCategory.getText().toString().trim();
            String colors = findText(R.id.etColors);
            String material = findText(R.id.etMaterial);
            String tags = findText(R.id.etTags);
            String occasions = findText(R.id.etOccasions);

            if (name.isEmpty()) emptyFields.add("Name");
            if (category.isEmpty()) emptyFields.add("Category");
            if (colors.isEmpty()) emptyFields.add("Colors");
            if (material.isEmpty()) emptyFields.add("Material");
            if (tags.isEmpty()) emptyFields.add("Tags");
            if (occasions.isEmpty()) emptyFields.add("Occasions");

            if (!emptyFields.isEmpty()) {
                String message = "Please fill in:\n- " + String.join("\n- ", emptyFields);
                new androidx.appcompat.app.AlertDialog.Builder(AddItemActivity.this)
                        .setTitle("Missing Fields")
                        .setMessage(message)
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            if (capturedBitmap == null) {
                Toast.makeText(this, "No image found!", Toast.LENGTH_SHORT).show();
                return;
            }

            String base64 = bitmapToBase64(capturedBitmap);

            JSONObject json = new JSONObject();
            try {
                json.put("name", name);
                json.put("category", category);
                json.put("color", new JSONArray(Arrays.asList(colors.split(","))));
                json.put("material", new JSONArray(Arrays.asList(material.split(","))));
                json.put("tags", new JSONArray(Arrays.asList(tags.split(","))));
                json.put("occasions", new JSONArray(Arrays.asList(occasions.split(","))));
                json.put("imageBase64", base64);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "JSON error", Toast.LENGTH_SHORT).show();
                return;
            }

            sendToBackend(json.toString());
        });
    }

    private String findText(int id) {
        return ((com.google.android.material.textfield.TextInputEditText) findViewById(id)).getText().toString().trim();
    }

    private Bitmap cropCenter(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int newSize = Math.min(width, height);
        int xOffset = (width - newSize) / 2;
        int yOffset = (height - newSize) / 2;

        return Bitmap.createBitmap(bitmap, xOffset, yOffset, newSize, newSize);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        Bitmap cropped = cropCenter(bitmap);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cropped.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    private void sendToBackend(String jsonBody) {
        OkHttpClient client = new OkHttpClient();
        String backendUrl = "https://auarai.onrender.com/api/userclothes/add";

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(backendUrl)
                .addHeader("Authorization", "Bearer " + token)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AddItemActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddItemActivity.this, "Item added!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddItemActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
