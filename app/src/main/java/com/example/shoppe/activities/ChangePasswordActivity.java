package com.example.shoppe.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.shoppe.R;
import com.example.shoppe.activities.toast.Utils;
import com.example.shoppe.databinding.ActivityChangePasswordBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {


    private ActivityChangePasswordBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private static  final String TAG= "ChangePassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Vui lòng đợi trong giây lát");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
            }
        });
        binding.changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private String currentPassword ="", newpassword="", cPassword="";
    private void validate() {
        currentPassword = binding.currentPasswordEt.getText().toString().trim();
        newpassword = binding.passwordEt.getText().toString().trim();
        cPassword = binding.cPasswordEt.getText().toString().trim();

        Log.d(TAG, "validate: currentPassword"+currentPassword);
        Log.d(TAG, "validate: currentPassword"+newpassword);
        Log.d(TAG, "validate: currentPassword"+cPassword);

        if (currentPassword.isEmpty()){
            binding.currentPasswordEt.setError("Vui lòng nhập mật khẩu cũ");
            binding.currentPasswordEt.requestFocus();
        }
        else if(newpassword.isEmpty()){
            binding.passwordEt.setError("Vui lòng nhập mật khẩu mới của bạn");
            binding.passwordEt.requestFocus();
        }
        else if(cPassword.isEmpty()){
            binding.passwordEt.setError("Vui lòng nhập lại mật khẩu mới của bạn");
            binding.passwordEt.requestFocus();
        }
        else if(!newpassword.equals(cPassword)){
            binding.cPasswordEt.setError("Mật khẩu mới bạn nhập không khớp của bạn");
            binding.cPasswordEt.requestFocus();
        }
        else {
            authenticateUserForUpdatePassword();
        }

    }

    private void authenticateUserForUpdatePassword() {
        Log.d(TAG, "authenticateUserForUpdatePassword: ");

        progressDialog.setMessage("Tài khoản User");
        progressDialog.show();

        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentPassword);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updatePassword();
                        Log.d(TAG, "onSuccess: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e);
                        Utils.toastyError(ChangePasswordActivity.this, "Lỗi" +e);
                        progressDialog.dismiss();

                    }
                });
    }

    private void updatePassword() {
        Log.d(TAG, "updatePassword: ");

        progressDialog.setMessage("Update mật khẩu mới");
        progressDialog.show();

        firebaseUser.updatePassword(newpassword)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Update thành công mật khẩu mới");
                        Utils.toastySuccess(ChangePasswordActivity.this,"Update thành công mật khẩu mới");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: "+e);
                        Utils.toastyError(ChangePasswordActivity.this,"Lỗi" +e);

                    }
                });
    }
}