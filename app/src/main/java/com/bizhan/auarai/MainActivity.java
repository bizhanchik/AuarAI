package com.bizhan.auarai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bizhan.auarai.API.auth.Login;
import com.bizhan.auarai.authorization.LoginActivity;
import com.bizhan.auarai.fragments.create.CreateFragment;
import com.bizhan.auarai.fragments.wardrobe.WardrobeFragment;
import com.bizhan.auarai.fragments.map.MapFragment;
import com.bizhan.auarai.fragments.profile.ProfileFragment;
import com.bizhan.auarai.fragments.shop.ShopFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String THEME_KEY = "theme_mode";
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavView);
        frameLayout = findViewById(R.id.frameLayout);

        String token = Login.getToken(this);
        if (token == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (savedInstanceState == null) {
            loadFragment(new MapFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navMap){
                    selectedFragment = new MapFragment();
                }
                else if(itemId == R.id.navShop) {
                    selectedFragment = new ShopFragment();
                }
                else if(itemId == R.id.navCam){
                    selectedFragment = new WardrobeFragment();
                }
                else{
                    selectedFragment = new ProfileFragment();
                }
                loadFragment(selectedFragment);

                return true;
            }
        });
    }

    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedTheme = prefs.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(savedTheme);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}