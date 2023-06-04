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
import com.example.shoppe.databinding.ActivityRegisterEmailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterEmailActivity extends AppCompatActivity {

    private ActivityRegisterEmailBinding binding;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private static final String TAG = "REGISTER";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterEmailBinding.inflate(getLayoutInflater());
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
        binding.noLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(RegisterEmailActivity.this);
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
                        Toast.makeText(RegisterEmailActivity.this, "Bạn vào trang đăng ký tài khoản Email thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterEmailActivity.this, LoginEmailActivity.class));
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


        // đăng nhập
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data();
            }
        });
    }
    private String email, password,cPassword;
    private void Data() {
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();
        cPassword = binding.confirmpasswordEt.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.setError("Email không đúng");
            binding.emailEt.requestFocus();
        }
        else if (password.isEmpty()){
            binding.passwordEt.setError("Mật khẩu chưa nhập");
            binding.passwordEt.requestFocus();
        }
        else if (password.length()<6){
            binding.passwordEt.setError("Mật khẩu phải 6 số");
            binding.passwordEt.requestFocus();
        }
        else if (!password.equals(cPassword)){
            binding.confirmpasswordEt.setError("Mật khẩu không khớp");
            binding.confirmpasswordEt.requestFocus();
        }
        else{
            registerUser();
        }
    }

    private void registerUser() {
        progressDialog.setMessage("Đăng ký");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        Log.d(TAG, "onSuccess: Đăng nhập thành công...");
                        progressDialog.dismiss();
                        updateUsers();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ",e );
                        Utils.toastyError(RegisterEmailActivity.this,"Lỗi: "+e);
                        progressDialog.dismiss();
                    }
                });

    }

    private void updateUsers() {
        progressDialog.setMessage("Update lên cơ sở dữ liệu");

        long timestamp = Utils.getTimestamp();
        String registerUserEmail = firebaseAuth.getCurrentUser().getEmail();
        String registerUserUid = firebaseAuth.getUid();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("name","");
        hashMap.put("phoneCode","");
        hashMap.put("phoneNumber","");
        hashMap.put("profileImageUrl","");
        hashMap.put("dob","");
        hashMap.put("userType","Email");
        hashMap.put("typringTo","");
        hashMap.put("timestamp",timestamp);
        hashMap.put("onlineStatus",true);
        hashMap.put("email", registerUserEmail);
        hashMap.put("uid", registerUserUid);
        hashMap.put("password",password);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(registerUserUid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Utils.toastySuccess(RegisterEmailActivity.this, "Tạo tài khoản thành công");
                        Log.d(TAG, "onSuccess: Thành công");
                        startActivity(new Intent(RegisterEmailActivity.this, LoginEmailActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Utils.toastyError(RegisterEmailActivity.this,"Lỗi: "+e);
                        Log.d(TAG, "onFailure: Lỗi: "+e);
                    }
                });
    }
}