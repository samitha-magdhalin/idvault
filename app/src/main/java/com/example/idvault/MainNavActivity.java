package com.example.idvault;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainNavActivity extends AppCompatActivity {

    LinearLayout navHome, navSearch, navProfile;
    TextView navHomeLabel, navSearchLabel, navProfileLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);

        navHome = findViewById(R.id.navHome);
        navSearch = findViewById(R.id.navSearch);
        navProfile = findViewById(R.id.navProfile);

        navHomeLabel = (TextView) navHome.getChildAt(1);
        navSearchLabel = (TextView) navSearch.getChildAt(1);
        navProfileLabel = (TextView) navProfile.getChildAt(1);

        loadFragment(new HomeFragment());
        setActiveTab(navHomeLabel);

        navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            setActiveTab(navHomeLabel);
        });

        navSearch.setOnClickListener(v -> {
            loadFragment(new SearchFragment());
            setActiveTab(navSearchLabel);
        });

        navProfile.setOnClickListener(v -> {
            loadFragment(new ProfileFragment());
            setActiveTab(navProfileLabel);
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void setActiveTab(TextView active) {
        navHomeLabel.setTextColor(getResources().getColor(R.color.text_secondary));
        navSearchLabel.setTextColor(getResources().getColor(R.color.text_secondary));
        navProfileLabel.setTextColor(getResources().getColor(R.color.text_secondary));
        active.setTextColor(getResources().getColor(R.color.primary));
    }
}