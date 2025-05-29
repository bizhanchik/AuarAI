package com.bizhan.auarai.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bizhan.auarai.R;
import com.bizhan.auarai.activities.WardrobeItemDetailActivity;
import com.bizhan.auarai.models.UserClothingItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class WardrobeAdapter extends RecyclerView.Adapter<WardrobeAdapter.ViewHolder> {
    private final Context context;
    private List<UserClothingItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(UserClothingItem item);
    }

    public WardrobeAdapter(Context context, List<UserClothingItem> items) {
        this.context = context;
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_clothing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserClothingItem item = items.get(position);
        holder.bind(item);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<UserClothingItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImage;
        private TextView tvName;
        private TextView tvCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }

        public void bind(UserClothingItem item) {
            tvName.setText(item.getName());
            tvCategory.setText(item.getCategory());

            if (item.getImageData() != null) {
                byte[] imageBytes = Base64.decode(item.getImageData(), Base64.DEFAULT);
                Glide.with(context)
                    .load(imageBytes)
                    .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                    .into(ivImage);
            } else {
                ivImage.setImageResource(R.drawable.placeholder_image);
            }
        }
    }
}