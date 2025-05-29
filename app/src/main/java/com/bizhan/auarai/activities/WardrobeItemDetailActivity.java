package com.bizhan.auarai.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bizhan.auarai.R;
import com.bizhan.auarai.models.UserClothingItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class WardrobeItemDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ITEM = "extra_item";
    private UserClothingItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_item_detail);

        item = getIntent().getParcelableExtra(EXTRA_ITEM);
        if (item == null) {
            Toast.makeText(this, "Error: Item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        setupViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wardrobe_item_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            // TODO: Implement delete functionality
            Toast.makeText(this, "Delete functionality coming soon", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Item Details");
        }
    }

    private void setupViews() {
        ImageView ivItemImage = findViewById(R.id.ivItemImage);
        if (item.getImageData() != null) {
            byte[] imageBytes = android.util.Base64.decode(item.getImageData(), android.util.Base64.DEFAULT);
            Glide.with(this)
                .load(imageBytes)
                .apply(new RequestOptions()
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .transform(new RoundedCorners(16)))
                .into(ivItemImage);
        }

        TextView tvName = findViewById(R.id.tvName);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvDescription = findViewById(R.id.tvDescription);

        tvName.setText(item.getName());
        tvCategory.setText(item.getCategory());
        
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            tvDescription.setText(item.getDescription());
            tvDescription.setVisibility(View.VISIBLE);
        } else {
            tvDescription.setVisibility(View.GONE);
        }

        ChipGroup chipGroupColors = findViewById(R.id.chipGroupColors);
        if (item.getColor() != null && !item.getColor().isEmpty()) {
            chipGroupColors.setVisibility(View.VISIBLE);
            for (String color : item.getColor()) {
                Chip chip = new Chip(this);
                chip.setText(color);
                chip.setChipBackgroundColorResource(R.color.chip_background);
                chip.setTextColor(ContextCompat.getColor(this, R.color.chip_text));
                chipGroupColors.addView(chip);
            }
        } else {
            chipGroupColors.setVisibility(View.GONE);
        }

        TextView tvSize = findViewById(R.id.tvSize);
        if (item.getSize() != null && !item.getSize().isEmpty()) {
            tvSize.setText(item.getSizeDisplay());
            tvSize.setVisibility(View.VISIBLE);
        } else {
            tvSize.setVisibility(View.GONE);
        }

        TextView tvMaterial = findViewById(R.id.tvMaterial);
        String materialDisplay = item.getMaterialDisplay();
        if (materialDisplay != null && !materialDisplay.isEmpty()) {
            tvMaterial.setText(materialDisplay);
            tvMaterial.setVisibility(View.VISIBLE);
        } else {
            tvMaterial.setVisibility(View.GONE);
        }

        ChipGroup chipGroupOccasions = findViewById(R.id.chipGroupOccasions);
        if (item.getOccasions() != null && !item.getOccasions().isEmpty()) {
            chipGroupOccasions.setVisibility(View.VISIBLE);
            for (String occasion : item.getOccasions()) {
                Chip chip = new Chip(this);
                chip.setText(occasion);
                chip.setChipBackgroundColorResource(R.color.chip_background);
                chip.setTextColor(ContextCompat.getColor(this, R.color.chip_text));
                chipGroupOccasions.addView(chip);
            }
        } else {
            chipGroupOccasions.setVisibility(View.GONE);
        }
    }
}