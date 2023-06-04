package com.example.shoppe.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shoppe.R;
import com.example.shoppe.activities.LoginOptionActivity;
import com.example.shoppe.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {


    private FragmentProfileBinding binding;

    public ProfileFragment (){
        //contrustor
    }
    private Context mContext;
    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(LayoutInflater.from(mContext), container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        binding.dangxuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(mContext, LoginOptionActivity.class));
                getActivity().finishAffinity();
            }
        });



        return binding.getRoot();
    }
}