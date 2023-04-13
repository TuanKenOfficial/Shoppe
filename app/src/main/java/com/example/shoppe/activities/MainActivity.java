package com.example.shoppe.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;

import com.example.shoppe.R;
import com.example.shoppe.databinding.ActivityMainBinding;
import com.example.shoppe.fragments.ChatsFragment;
import com.example.shoppe.fragments.HomeFragment;
import com.example.shoppe.fragments.NotificationFragment;
import com.example.shoppe.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showHomeFragment(); //Home Fragment


        binding.bottomNv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId== R.id.menu_home){
                    //Home item click, Fragment Home
                    showHomeFragment();
                    return true;

                }
                else if (itemId == R.id.menu_chat){
                    //Home item click, Fragment Chat
                    showChatsFragment();
                    return true;
                }
                else if (itemId == R.id.menu_fav){
                    //Home item click, Fragment Navigition
                    showFavFragment();
                    return true;
                }
                else if (itemId == R.id.menu_person){
                    //Home item click, Fragment Profile
                    showProfileFragment();
                    return true;
                }
                else {

                    //Home item click,

                    return false;
                }

            }
        });
    }



    private void showHomeFragment() {
        binding.toolbarTitleTv.setText("Home");

        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "HomeFragment");
        fragmentTransaction.commit();
    }


    private void showChatsFragment() {
        binding.toolbarTitleTv.setText("Chats");

        ChatsFragment fragment = new ChatsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "ChatsFragment");
        fragmentTransaction.commit();
    }


    private void showFavFragment() {
        binding.toolbarTitleTv.setText("Notification");

        NotificationFragment fragment = new NotificationFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "NotificationFragment");
        fragmentTransaction.commit();
    }

    private void showProfileFragment() {
        binding.toolbarTitleTv.setText("Profile");

        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "ProfileFragment");
        fragmentTransaction.commit();

    }
}