package com.example.shoppe.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.shoppe.R;
import com.example.shoppe.activities.toast.Utils;
import com.example.shoppe.databinding.ActivityLoginEmailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginEmailActivity extends AppCompatActivity {

    private ActivityLoginEmailBinding binding;
    
    private ProgressDialog progressDialog;
    
    private FirebaseAuth firebaseAuth;

    private static final String TAG ="LOGIN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi");
        progressDialog.setCanceledOnTouchOutside(false);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        //vào trang đăng ký
        binding.noAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(LoginEmailActivity.this);
                //thiết lập tiêu đề:
                builder.setTitle("Trang đăng ký tài khoản email");
                //thiết lập icon:
                builder.setIcon(R.drawable.ic_phone);
                //thiết lập nội dung cho dialog:
                builder.setMessage("Bạn đang vào trang đăng ký tài khoản email?");
                //thiết lập các nút lệnh để người dùng tương tác:
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(LoginEmailActivity.this, "Bạn vào trang đăng ký tài khoản Email thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginEmailActivity.this, RegisterEmailActivity.class));
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                //tạo cửa sổ Dialog:
                AlertDialog dialog=builder.create();
                dialog.setCanceledOnTouchOutside(false);
                //hiển thị cửa sổ này lên:
                dialog.show();
            }
        });
        
        //vào trang quên mật khẩu
        binding.noPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //quên mật khẩu
            }
        });
        
        // đăng nhập
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data();
            }
        });
    }

    private String email, password;
    private void Data() {
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.setError("Email không đúng");
            binding.emailEt.requestFocus();
        }
        else if (password.isEmpty()){
            binding.passwordEt.setError("Mật khẩu chưa nhập");
            binding.passwordEt.requestFocus();
        }
        else{
            loginUser();
        }
    }

    private void loginUser() {
        progressDialog.setMessage("Đăng nhập");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        Log.d(TAG, "onSuccess: Đăng nhập thành công...");
                        progressDialog.dismiss();
                        Utils.toastySuccess(LoginEmailActivity.this, "Đăng nhập thành công");
                        startActivity(new Intent(LoginEmailActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ",e );
                        Utils.toastyError(LoginEmailActivity.this,"Lỗi: "+e);
                        progressDialog.dismiss();
                    }
                });
    }
}