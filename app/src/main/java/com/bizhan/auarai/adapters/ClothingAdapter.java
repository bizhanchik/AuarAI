package com.bizhan.auarai.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bizhan.auarai.R;
import com.bizhan.auarai.models.ClothingItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ClothingViewHolder> {

    private Context context;
    private List<ClothingItem> clothingItems;
    private OnItemClickListener listener;
    private NumberFormat priceFormat;

    public interface OnItemClickListener {
        void onItemClick(ClothingItem item);
        void onFavoriteClick(ClothingItem item);
        void onStoreClick(ClothingItem item);
    }

    public ClothingAdapter(Context context, List<ClothingItem> clothingItems) {
        this.context = context;
        this.clothingItems = clothingItems;
        this.priceFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateItems(List<ClothingItem> newItems) {
        this.clothingItems = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClothingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_clothing, parent, false);
        return new ClothingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClothingViewHolder holder, int position) {
        ClothingItem item = clothingItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return clothingItems.size();
    }

    public class ClothingViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvName, tvBrand, tvCategory, tvGender, tvColors, tvSizes;
        private TextView tvPrice, tvOldPrice, tvStoreName, tvTags, tvAvailability, tvDiscount;
        private ImageButton btnFavorite, btnAction;

        public ClothingViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.ivImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvColors = itemView.findViewById(R.id.tvColors);
            tvSizes = itemView.findViewById(R.id.tvSizes);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvOldPrice = itemView.findViewById(R.id.tvOldPrice);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
            tvTags = itemView.findViewById(R.id.tvTags);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnAction = itemView.findViewById(R.id.btnAction);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(clothingItems.get(getAdapterPosition()));
                }
            });

            btnFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(clothingItems.get(getAdapterPosition()));
                }
            });

            btnAction.setOnClickListener(v -> {
                ClothingItem item = clothingItems.get(getAdapterPosition());
                openProductUrl(item);
            });
        }

        public void bind(ClothingItem item) {
            loadImage(item.getImageURL());

            tvName.setText(item.getName());
            setBrandInfo(item);
            setCategoryInfo(item);
            setGenderInfo(item);
            setColorInfo(item);
            setSizeInfo(item);
            setPriceInfo(item);
            setStoreInfo(item);
            setAvailabilityInfo(item);
            setTagsInfo(item);
            setDiscountInfo(item);
        }

        private void loadImage(String imageUrl) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(imageUrl)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.error_image)
                                .transform(new RoundedCorners(16)))
                        .into(ivImage);
            } else {
                ivImage.setImageResource(R.drawable.placeholder_image);
            }
        }

        private void setBrandInfo(ClothingItem item) {
            if (item.getBrand() != null && !item.getBrand().isEmpty()) {
                tvBrand.setText(item.getBrand());
                tvBrand.setVisibility(View.VISIBLE);
            } else {
                tvBrand.setVisibility(View.GONE);
            }
        }

        private void setCategoryInfo(ClothingItem item) {
            String categoryText = mapToGeneralCategory(item.getCategory());
            tvCategory.setText(categoryText);
        }

        private void setGenderInfo(ClothingItem item) {
            String genderText = getGenderDisplayName(item.getGender());
            tvGender.setText(genderText);
        }

        private void setColorInfo(ClothingItem item) {
            if (item.getColor() != null && !item.getColor().isEmpty()) {
                tvColors.setText(item.getColorDisplay());
                tvColors.setVisibility(View.VISIBLE);
                ((View) tvColors.getParent()).setVisibility(View.VISIBLE);
            } else {
                tvColors.setVisibility(View.GONE);
                ((View) tvColors.getParent()).setVisibility(View.GONE);
            }
        }

        private void setSizeInfo(ClothingItem item) {
            if (item.getSize() != null && !item.getSize().isEmpty()) {
                tvSizes.setText(item.getSizeDisplay());
                tvSizes.setVisibility(View.VISIBLE);
                ((View) tvSizes.getParent()).setVisibility(View.VISIBLE);
            } else {
                tvSizes.setVisibility(View.GONE);
                ((View) tvSizes.getParent()).setVisibility(View.GONE);
            }
        }

        private void setPriceInfo(ClothingItem item) {
            String formattedPrice = formatPrice(item.getPrice());
            tvPrice.setText(formattedPrice);
        }

        private void setStoreInfo(ClothingItem item) {
            tvStoreName.setText(item.getStoreName());
        }

        private void setAvailabilityInfo(ClothingItem item) {
            if (item.isAvailable()) {
                tvAvailability.setText("In stock");
                tvAvailability.setBackgroundResource(R.drawable.availability_badge_bg);
            } else {
                tvAvailability.setText("Out of stock");
                tvAvailability.setBackgroundResource(R.drawable.unavailable_badge_background);
            }
        }

        private void setTagsInfo(ClothingItem item) {
            if (item.getTags() != null && !item.getTags().isEmpty()) {
                StringBuilder tagsBuilder = new StringBuilder();
                for (String tag : item.getTags()) {
                    tagsBuilder.append("#").append(tag).append(" ");
                }
                tvTags.setText(tagsBuilder.toString().trim());
                tvTags.setVisibility(View.VISIBLE);
            } else {
                tvTags.setVisibility(View.GONE);
            }
        }

        private void setDiscountInfo(ClothingItem item) {
        }

        private String getGenderDisplayName(String gender) {
            if (gender == null) return "";

            switch (gender.toLowerCase()) {
                case "male":
                    return "Male";
                case "female":
                    return "Female";
                case "unisex":
                    return "Unisex";
                default:
                    return gender;
            }
        }

        private String formatPrice(double price) {
            return String.format(Locale.getDefault(), "%.0f ₸", price);
        }

        private void openProductUrl(ClothingItem item) {
            try {
                String url = item.getProductURL();
                if (url != null && !url.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Ссылка на товар недоступна", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(context, "Не удалось открыть ссылку", Toast.LENGTH_SHORT).show();
            }
        }

        private String mapToGeneralCategory(String category) {
            if (category == null) return "";
            switch (category.toLowerCase()) {
                case "shirt":
                case "t-shirt":
                case "blouse":
                    return "Tops";
                case "pants":
                case "jeans":
                case "shorts":
                    return "Bottoms";
                case "shoes":
                case "sneakers":
                case "boots":
                    return "Shoes";
                case "bag":
                case "hat":
                case "accessories":
                    return "Accessories";
                case "sportswear":
                case "activewear":
                    return "Sportswear";
                default:
                    return category;
            }
        }
    }
}
