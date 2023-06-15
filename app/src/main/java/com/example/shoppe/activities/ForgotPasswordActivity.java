package com.example.shoppe.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.shoppe.R;
import com.example.shoppe.activities.toast.Utils;
import com.example.shoppe.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;


    //progress
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Vui lòng đợi trong giây lát");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private String email = "";
    private void validateData() {
        //get data email
        email = binding.emailEt.getText().toString().trim();

        //validate data shouldn't empty and should be valid format
        if (email.isEmpty()){
            Utils.toastyInfo(this, "Vui lòng nhập email");

        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.setError("Email không đúng");
            binding.emailEt.requestFocus();
            Utils.toastyInfo(this, "Định dạng email không hợp lệ");
        }
        else {
            recoverPassword();
        }

    }
    private void recoverPassword() {
        //progress dialog
        progressDialog.setMessage("Đã gửi hướng dẫn khôi phục mật khẩu tới "+email);
        progressDialog.show();

        //begin sending recovery
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //sent
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "Hướng dẫn khôi phục mật khẩu đã được gửi đến email." +
                                " Email bạn đã nhập ở trên", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "Không gửi được do "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}