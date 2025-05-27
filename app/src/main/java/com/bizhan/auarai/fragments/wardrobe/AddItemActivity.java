package com.bizhan.auarai.fragments.wardrobe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bizhan.auarai.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddItemActivity extends AppCompatActivity {

    private ImageView imageHolder;
    private AutoCompleteTextView actvCategory, actvGender;
    private TextInputLayout tilCategory, tilGender;
    private MaterialButton btnAgain, btnSave;

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
        tilCategory = findViewById(R.id.tilCategory);
        btnAgain = findViewById(R.id.btnAgain);
        btnSave = findViewById(R.id.btnSave);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            new ArrayList<>(Arrays.asList(categories))
        );
        actvCategory.setAdapter(categoryAdapter);
        actvCategory.setThreshold(1);


        byte[] byteArray = getIntent().getByteArrayExtra("captured_image");
        if (byteArray != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageHolder.setImageBitmap(bitmap);
        }

        btnAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddItemActivity.this, CameraActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> emptyFields = new ArrayList<>();

                String name = ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.etName)).getText().toString().trim();
                String category = actvCategory.getText().toString().trim();
                String gender = actvGender.getText().toString().trim();
                String colors = ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.etColors)).getText().toString().trim();
                String material = ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.etMaterial)).getText().toString().trim();
                String tags = ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.etTags)).getText().toString().trim();
                String occasions = ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.etOccasions)).getText().toString().trim();

                if (name.isEmpty()) emptyFields.add("Name");
                if (category.isEmpty()) emptyFields.add("Category");
                if (gender.isEmpty()) emptyFields.add("Gender");
                if (colors.isEmpty()) emptyFields.add("Colors");
                if (material.isEmpty()) emptyFields.add("Material");
                if (tags.isEmpty()) emptyFields.add("Tags");
                if (occasions.isEmpty()) emptyFields.add("Occasions");

                if (!emptyFields.isEmpty()) {
                    String message = "Please fill in the following fields:\n- " + String.join("\n- ", emptyFields);

                    new androidx.appcompat.app.AlertDialog.Builder(AddItemActivity.this)
                            .setTitle("Incomplete Form")
                            .setMessage(message)
                            .setPositiveButton("OK", null)
                            .show();
                } else {

                }
            }
        });

    }
}
