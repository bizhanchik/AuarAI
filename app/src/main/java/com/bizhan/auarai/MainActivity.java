package com.bizhan.auarai;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bizhan.auarai.API.auth.Login;
import com.bizhan.auarai.authorization.LoginActivity;
import com.bizhan.auarai.fragments.create.CreateFragment;
import com.bizhan.auarai.fragments.favorites.FavoritesFragment;
import com.bizhan.auarai.fragments.map.MapFragment;
import com.bizhan.auarai.fragments.profile.ProfileFragment;
import com.bizhan.auarai.fragments.shop.ShopFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                else if(itemId == R.id.navShop){
                    selectedFragment = new ShopFragment();
                }
                else if(itemId == R.id.navCreate){
                    selectedFragment = new CreateFragment();
                }
                else if(itemId == R.id.navFavs){
                    selectedFragment = new FavoritesFragment();
                }
                else{
                    selectedFragment = new ProfileFragment();
                }
                loadFragment(selectedFragment);

                return true;
            }
        });

    }

    private void loadFragment (Fragment fragment ){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}