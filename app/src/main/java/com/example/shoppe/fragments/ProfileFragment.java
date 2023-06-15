package com.example.shoppe.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.shoppe.R;
import com.example.shoppe.Users;
import com.example.shoppe.activities.ChangePasswordActivity;
import com.example.shoppe.activities.LoginOptionActivity;
import com.example.shoppe.activities.ProfileEditActivity;
import com.example.shoppe.activities.toast.Utils;
import com.example.shoppe.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private static final String TAG ="ProfileFragment";

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
        binding.logoutCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(mContext, LoginOptionActivity.class));
                getActivity().finishAffinity();
            }
        });
        binding.editprofileCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ProfileEditActivity.class));
                getActivity().finishAffinity();
            }
        });

        binding.changePasswordCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ChangePasswordActivity.class));
                getActivity().finishAffinity();
            }
        });

        loadMyInfo();

        return binding.getRoot();
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users user = snapshot.getValue(Users.class);
                        String name = ""+snapshot.child("name").getValue();
                        String email = ""+snapshot.child("email").getValue();
                        String dob = ""+snapshot.child("dob").getValue();
                        String onlineStatus = ""+snapshot.child("onlineStatus").getValue();
                        String profileImageUrl = ""+snapshot.child("profileImageUrl").getValue();
                        String typringTo = ""+snapshot.child("typringTo").getValue();
                        String userType = ""+snapshot.child("userType").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String phoneCode = ""+snapshot.child("phoneCode").getValue();
                        String phoneNumber = ""+snapshot.child("phoneNumber").getValue();

                        //số điện thoại
                        String phone = phoneCode + phoneNumber;

                        //thời gian
                        if(timestamp.equals("null")){
                            timestamp ="0";
                        }
                        //ngày-tháng-năm
                        String formattedDate = Utils.formatTimestampDate(Long.parseLong(timestamp));

                        //set data
                        binding.nameTv.setText(name);
                        binding.emailTv.setText(email);
                        binding.dobTv.setText(dob);
                        binding.phoneTv.setText(phone);
                        binding.memberSignleTv.setText(formattedDate);

                        //xử lý trạng thái tài khoản
                        if(userType.equals("Email")){
                            boolean isVerified = firebaseAuth.getCurrentUser().isEmailVerified();
                            if(isVerified){
                                binding.verifiedTv.setText("Đã xác minh");
                            }
                            else {
                                binding.verifiedTv.setText("Chưa xác minh");
                            }
                        }
                        else {
                            binding.verifiedTv.setText("Đã xác minh");
                        }

                        //hình ảnh profile
                        try {
                            Glide.with(mContext)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.edituser)
                                    .into(binding.profileIv);
                        }
                        catch (Exception e){
                            Log.d(TAG, "onDataChange: "+e);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}