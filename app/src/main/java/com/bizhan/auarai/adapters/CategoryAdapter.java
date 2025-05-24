package com.bizhan.auarai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bizhan.auarai.R;
import com.bizhan.auarai.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categories;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(Context context, List<Category> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView tvCategoryName, tvCount;
        private ImageView ivIcon;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardCategory);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCount = itemView.findViewById(R.id.tvCount);
            ivIcon = itemView.findViewById(R.id.ivCategoryIcon);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(categories.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Category category) {
            tvCategoryName.setText(category.getName());

            if (category.getIconRes() != 0) {
                ivIcon.setImageResource(category.getIconRes());
                ivIcon.setVisibility(View.VISIBLE);
            } else {
                ivIcon.setVisibility(View.GONE);
            }

            if (category.getCount() > 0) {
                tvCount.setText(String.valueOf(category.getCount()));
                tvCount.setVisibility(View.VISIBLE);
            } else {
                tvCount.setVisibility(View.GONE);
            }

            if (category.isSelected()) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_light_primary));
                tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.md_theme_light_onPrimary));
                tvCount.setTextColor(ContextCompat.getColor(context, R.color.md_theme_light_onPrimary));
                ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.md_theme_light_onPrimary));
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_chip_bg));
                tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
                tvCount.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
                ivIcon.clearColorFilter();
            }
        }
    }

    public void updateCategories(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }
}