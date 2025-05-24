package com.bizhan.auarai.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bizhan.auarai.R;
import com.bizhan.auarai.models.ClothingItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class ProductDetailActivity extends AppCompatActivity {
    public static final String EXTRA_PRODUCT = "extra_product";
    private ClothingItem product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        product = getIntent().getParcelableExtra(EXTRA_PRODUCT);
        if (product == null) {
            Toast.makeText(this, "Error: Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        setupViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        ImageButton btnFavorite = findViewById(R.id.btnFavorite);
        btnFavorite.setOnClickListener(v -> {
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupViews() {
        ImageView ivProductImage = findViewById(R.id.ivProductImage);
        Glide.with(this)
                .load(product.getImageURL())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .transform(new RoundedCorners(16)))
                .into(ivProductImage);

        TextView tvBrand = findViewById(R.id.tvBrand);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvGender = findViewById(R.id.tvGender);
        TextView tvColors = findViewById(R.id.tvColors);
        TextView tvSizes = findViewById(R.id.tvSizes);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvStoreName = findViewById(R.id.tvStoreName);
        TextView tvTags = findViewById(R.id.tvTags);

        tvBrand.setText(product.getBrand());
        tvName.setText(product.getName());
        tvPrice.setText(String.format("%.0f ₸", product.getPrice()));
        tvCategory.setText(product.getCategory());
        tvGender.setText(product.getGender());
        tvColors.setText(product.getColorDisplay());
        tvSizes.setText(product.getSizeDisplay());
        tvStoreName.setText(product.getStoreName());
        
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            tvDescription.setText(product.getDescription());
            tvDescription.setVisibility(View.VISIBLE);
        } else {
            tvDescription.setVisibility(View.GONE);
        }
        
        if (product.getTags() != null && !product.getTags().isEmpty()) {
            StringBuilder tagsBuilder = new StringBuilder();
            for (String tag : product.getTags()) {
                tagsBuilder.append("#").append(tag).append(" ");
            }
            tvTags.setText(tagsBuilder.toString().trim());
            tvTags.setVisibility(View.VISIBLE);
        } else {
            tvTags.setVisibility(View.GONE);
        }

        findViewById(R.id.btnBuy).setOnClickListener(v -> {
            String url = product.getProductURL();
            if (url != null && !url.isEmpty()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Could not open product URL", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Product URL not available", Toast.LENGTH_SHORT).show();
            }
        });
    }
}