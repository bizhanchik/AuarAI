package com.bizhan.auarai.fragments.wardrobe;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bizhan.auarai.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class WardrobeFragment extends Fragment {
    ImageButton takePic;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wardrobe, container, false);

        takePic = view.findViewById(R.id.takePic);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        takePic.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CameraActivity.class);
            startActivity(intent);
        });
    }
}