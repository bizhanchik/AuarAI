package com.bizhan.auarai.fragments.wardrobe;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bizhan.auarai.R;
import com.bizhan.auarai.activities.WardrobeItemDetailActivity;
import com.bizhan.auarai.adapters.WardrobeAdapter;
import com.bizhan.auarai.models.UserClothingItem;
import com.bizhan.auarai.API.clothes.ClothingAPIService;
import com.bizhan.auarai.API.auth.Login;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class WardrobeFragment extends Fragment implements ChipGroup.OnCheckedChangeListener {

    private RecyclerView rvWardrobe;
    private EditText etSearch;
    private FloatingActionButton btnCamera;
    private WardrobeAdapter adapter;
    private List<UserClothingItem> allItems = new ArrayList<>();
    private List<UserClothingItem> filtered = new ArrayList<>();
    private ClothingAPIService apiService;
    private ChipGroup chipGroupCategories;
    private String currentCategory = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wardrobe, container, false);

        etSearch = view.findViewById(R.id.etSearchWardrobe);
        rvWardrobe = view.findViewById(R.id.rvWardrobe);
        btnCamera = view.findViewById(R.id.btnTakePic);
        chipGroupCategories = view.findViewById(R.id.chipGroupCategories);

        rvWardrobe.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new WardrobeAdapter(getContext(), filtered);
        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(getContext(), WardrobeItemDetailActivity.class);
            intent.putExtra(WardrobeItemDetailActivity.EXTRA_ITEM, item);
            startActivity(intent);
        });
        rvWardrobe.setAdapter(adapter);

        chipGroupCategories.setOnCheckedChangeListener(this);

        String token = Login.getToken(requireContext());
        if (token != null) {
            apiService = new ClothingAPIService(getContext(), token);
            loadItems();
        } else {
            Toast.makeText(getContext(), "Please log in to view your wardrobe", Toast.LENGTH_SHORT).show();
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnCamera.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CameraActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onCheckedChanged(ChipGroup group, int checkedId) {
        if (checkedId == View.NO_ID) {
            currentCategory = "All";
        } else {
            Chip chip = group.findViewById(checkedId);
            if (chip != null) {
                currentCategory = chip.getText().toString();
            }
        }
        filter(etSearch.getText().toString());
    }

    private void loadItems() {
        apiService.fetchUserClothes(new ClothingAPIService.UserClothingCallback() {
            @Override
            public void onSuccess(List<UserClothingItem> items) {
                if (!isAdded()) return;
                getActivity().runOnUiThread(() -> {
                    allItems.clear();
                    allItems.addAll(items);
                    filter(etSearch.getText().toString());
                });
            }

            @Override
            public void onFailure(String msg) {
                if (!isAdded()) return;
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void filter(String query) {
        filtered.clear();
        for (UserClothingItem item : allItems) {
            boolean matchesSearch = item.getName().toLowerCase().contains(query.toLowerCase());
            boolean matchesCategory = currentCategory.equals("All") || 
                                    item.getCategory().equals(currentCategory);
            
            if (matchesSearch && matchesCategory) {
                filtered.add(item);
            }
        }
        adapter.updateItems(filtered);
    }
}
