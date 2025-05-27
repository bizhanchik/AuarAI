package com.bizhan.auarai.fragments.shop;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bizhan.auarai.API.clothes.ClothingAPIService;
import com.bizhan.auarai.API.auth.Login;
import com.bizhan.auarai.R;
import com.bizhan.auarai.activities.ProductDetailActivity;
import com.bizhan.auarai.adapters.CategoryAdapter;
import com.bizhan.auarai.adapters.ClothingAdapter;
import com.bizhan.auarai.models.Category;
import com.bizhan.auarai.models.ClothingItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ShopFragment extends Fragment {

    private RecyclerView rvClothingItems, rvCategories;
    private EditText etSearch;
    private ImageButton btnFilter, btnSort;
    private TextView tvItemsCount, tvSortBy, tvErrorMessage;
    private LinearLayout layoutLoading, layoutError, layoutEmpty;
    private FloatingActionButton fabScrollToTop;
    private Button btnRetry;
    private NestedScrollView nestedScrollView;

    private ClothingAdapter clothingAdapter;
    private CategoryAdapter categoryAdapter;

    private List<ClothingItem> allClothingItems = new ArrayList<>();
    private List<ClothingItem> filteredClothingItems = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    private String currentSearchQuery = "";
    private String selectedCategory = "All";
    private String selectedGender = "All";
    private SortType currentSortType = SortType.POPULARITY;

    private ClothingAPIService apiService;
    private SharedPreferences preferences;
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    private static final int SEARCH_DELAY = 500; // мс
    private static final String PREF_SORT_TYPE = "sort_type";

    public enum SortType {
        POPULARITY("By popularity"),
        PRICE_LOW_HIGH("Price: Low to High"),
        PRICE_HIGH_LOW("Price: High to Low"),
        NAME_A_Z("Name: A-Z"),
        NEWEST("Newest first");

        private final String displayName;

        SortType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        rvClothingItems = view.findViewById(R.id.rvClothingItems);
        rvCategories = view.findViewById(R.id.rvCategories);
        etSearch = view.findViewById(R.id.etSearch);
        btnFilter = view.findViewById(R.id.btnFilter);
        btnSort = view.findViewById(R.id.btnSort);
        tvItemsCount = view.findViewById(R.id.tvItemsCount);
        tvSortBy = view.findViewById(R.id.tvSortBy);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutError = view.findViewById(R.id.layoutError);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        fabScrollToTop = view.findViewById(R.id.fabScrollToTop);
        btnRetry = view.findViewById(R.id.btnRetry);

        initPreferences();
        setupRecyclerViews();
        setupSearch();
        setupListeners();
        initCategories();

        String token = getAuthToken();
        if (token != null) {
            apiService = new ClothingAPIService(getContext(), token);
            loadClothingItems();
        } else {
            showError("Token authorization not found");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initPreferences();
        setupRecyclerViews();
        setupSearch();
        setupListeners();
        initCategories();

        String token = getAuthToken();
        if (token != null) {
            Log.d("ShopFragment", "Initializing API service with token");
            apiService = new ClothingAPIService(getContext(), token);
            loadClothingItems();
        } else {
            Log.e("ShopFragment", "No auth token found");
            showError("Token authorization not found");
        }
    }

    private void initViews(View view) {
        rvClothingItems = view.findViewById(R.id.rvClothingItems);
        rvCategories = view.findViewById(R.id.rvCategories);
        etSearch = view.findViewById(R.id.etSearch);
        btnFilter = view.findViewById(R.id.btnFilter);
        btnSort = view.findViewById(R.id.btnSort);
        tvItemsCount = view.findViewById(R.id.tvItemsCount);
        tvSortBy = view.findViewById(R.id.tvSortBy);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutError = view.findViewById(R.id.layoutError);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        fabScrollToTop = view.findViewById(R.id.fabScrollToTop);
        btnRetry = view.findViewById(R.id.btnRetry);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
    }

    private void initPreferences() {
        preferences = getActivity().getSharedPreferences("shop_prefs", getContext().MODE_PRIVATE);
        String savedSortType = preferences.getString(PREF_SORT_TYPE, SortType.POPULARITY.name());
        try {
            currentSortType = SortType.valueOf(savedSortType);
        } catch (IllegalArgumentException e) {
            currentSortType = SortType.POPULARITY;
        }
        updateSortDisplay();
    }

    private void setupRecyclerViews() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvClothingItems.setLayoutManager(gridLayoutManager);
        clothingAdapter = new ClothingAdapter(getContext(), filteredClothingItems);
        
        // Set up click listeners
        clothingAdapter.setOnItemClickListener(new ClothingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ClothingItem item) {
                Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, item);
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(ClothingItem item) {
                // TODO: Implement favorite functionality
                Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStoreClick(ClothingItem item) {
                if (item.getStoreURL() != null && !item.getStoreURL().isEmpty()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getStoreURL()));
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Could not open store URL", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Store URL not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        rvClothingItems.setAdapter(clothingAdapter);

        LinearLayoutManager categoriesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvCategories.setLayoutManager(categoriesLayoutManager);
        categoryAdapter = new CategoryAdapter(getContext(), categories, this::onCategorySelected);
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    currentSearchQuery = s.toString().trim();
                    applyFilters();
                };

                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupListeners() {
        btnFilter.setOnClickListener(v -> showFilterDialog());
        btnSort.setOnClickListener(v -> showSortDialog());
        btnRetry.setOnClickListener(v -> loadClothingItems());
        fabScrollToTop.setOnClickListener(v -> nestedScrollView.smoothScrollTo(0, 0));

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > 500) {
                fabScrollToTop.show();
            } else {
                fabScrollToTop.hide();
            }
        });
    }

    private void initCategories() {
        categories.clear();
        categories.add(new Category("All", "all", true));
        categories.add(new Category("Tops", "tops", false));
        categories.add(new Category("Bottoms", "bottoms", false));
        categories.add(new Category("Shoes", "shoes", false));
        categories.add(new Category("Accessories", "accessories", false));
        categories.add(new Category("Sportswear", "sportswear", false));
        categoryAdapter.notifyDataSetChanged();
    }

    private void loadClothingItems() {
        showLoading();

        apiService.fetchClothingItems(new ClothingAPIService.ClothingCallback() {
            @Override
            public void onSuccess(List<ClothingItem> clothingItems) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    allClothingItems.clear();
                    allClothingItems.addAll(clothingItems);
                    applyFilters();
                    hideLoading();
                });
            }

            @Override
            public void onFailure(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    hideLoading();
                    showError(message);
                });
            }
        });
    }


    private void applyFilters() {
        filteredClothingItems.clear();

        for (ClothingItem item : allClothingItems) {
            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    item.getName().toLowerCase().contains(currentSearchQuery.toLowerCase()) ||
                    (item.getBrand() != null && item.getBrand().toLowerCase().contains(currentSearchQuery.toLowerCase())) ||
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(currentSearchQuery.toLowerCase()));

            boolean matchesCategory = selectedCategory.equals("All") ||
                    selectedCategory.equals(mapToGeneralCategory(item.getCategory()));

            boolean matchesGender = selectedGender.equals("All") ||
                    item.getGender().equals(selectedGender.toLowerCase());

            if (matchesSearch && matchesCategory && matchesGender) {
                filteredClothingItems.add(item);
            }
        }

        applySorting();
        updateItemsCount();
        clothingAdapter.updateItems(filteredClothingItems);

        if (filteredClothingItems.isEmpty()) {
            showEmpty();
        } else {
            showContent();
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
                case "jorts":
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

    private void applySorting() {
        switch (currentSortType) {
            case PRICE_LOW_HIGH:
                Collections.sort(filteredClothingItems, Comparator.comparingDouble(ClothingItem::getPrice));
                break;
            case PRICE_HIGH_LOW:
                Collections.sort(filteredClothingItems, (a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                break;
            case NAME_A_Z:
                Collections.sort(filteredClothingItems, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                break;
            case NEWEST:
                Collections.sort(filteredClothingItems, (a, b) -> b.getUpdatedAt().compareToIgnoreCase(a.getUpdatedAt()));
                break;
            case POPULARITY:
            default:
                break;
        }
    }

    private void onCategorySelected(Category category) {
        selectedCategory = category.getName();

        for (Category cat : categories) {
            cat.setSelected(cat.getName().equals(selectedCategory));
        }
        categoryAdapter.notifyDataSetChanged();

        applyFilters();
    }

    private void showFilterDialog() {
        Toast.makeText(getContext(), "Filters in development", Toast.LENGTH_SHORT).show();
    }

    private void showSortDialog() {
        String[] sortOptions = Arrays.stream(SortType.values())
                .map(SortType::getDisplayName)
                .toArray(String[]::new);

        int currentIndex = Arrays.asList(SortType.values()).indexOf(currentSortType);

        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Sort")
                .setSingleChoiceItems(sortOptions, currentIndex, (dialog, which) -> {
                    currentSortType = SortType.values()[which];
                    preferences.edit().putString(PREF_SORT_TYPE, currentSortType.name()).apply();
                    updateSortDisplay();
                    applyFilters();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateSortDisplay() {
        tvSortBy.setText(currentSortType.getDisplayName());
    }

    private void updateItemsCount() {
        int count = filteredClothingItems.size();
        String text = count == 1 ? "Found 1 product" :
                "Found " + count + " products";
        tvItemsCount.setText(text);
    }

    private void showLoading() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        rvClothingItems.setVisibility(View.GONE);
    }

    private void hideLoading() {
        layoutLoading.setVisibility(View.GONE);
    }

    private void showError(String message) {
        layoutError.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        rvClothingItems.setVisibility(View.GONE);
        tvErrorMessage.setText(message);
    }

    private void showEmpty() {
        layoutEmpty.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        rvClothingItems.setVisibility(View.GONE);
    }

    private void showContent() {
        rvClothingItems.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private String getAuthToken() {
        String token = Login.getToken(requireContext());
        Log.d("ShopFragment", "Retrieved token: " + (token != null ? "present" : "null"));
        return token;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}
