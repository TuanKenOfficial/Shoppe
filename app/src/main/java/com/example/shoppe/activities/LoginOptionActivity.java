package com.example.shoppe.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.shoppe.R;
import com.example.shoppe.activities.toast.Utils;
import com.example.shoppe.databinding.ActivityLoginOptionBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginOptionActivity extends AppCompatActivity {
    private ActivityLoginOptionBinding binding;

    private FirebaseAuth firebaseAuth;

    private static final String TAG="LOGINOPTION";

    private GoogleSignInClient googleSignInClient; //có liên quan đến thư viện này 'com.google.android.gms:play-services-auth:20.5.0'

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginOptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
        Log.d(TAG, "onCreate: googleSignInClient" +gso+"\n"+googleSignInClient);
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //đăng nhập email
        binding.loginEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(LoginOptionActivity.this);
                //thiết lập tiêu đề:
                builder.setTitle("Trang đăng nhập Email");
                //thiết lập icon:
                builder.setIcon(android.R.drawable.ic_dialog_email);
                //thiết lập nội dung cho dialog:
                builder.setMessage("Bạn đang vào trang đăng nhập Email?");
                //thiết lập các nút lệnh để người dùng tương tác:
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(LoginOptionActivity.this, "Bạn vào trang đăng nhập Email thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginOptionActivity.this, LoginEmailActivity.class));
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

        //đăng nhập google
        binding.loginGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(LoginOptionActivity.this);
                //thiết lập tiêu đề:
                builder.setTitle("Trang đăng nhập Google");
                //thiết lập icon:
                builder.setIcon(R.drawable.google);
                //thiết lập nội dung cho dialog:
                builder.setMessage("Bạn đang vào trang đăng nhập Google?");
                //thiết lập các nút lệnh để người dùng tương tác:
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.toastySuccess(LoginOptionActivity.this, "Bạn vào trang đăng nhập Google thành công");
                        //xử lý
                        LoginGoogle();
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

        //đăng nhập số điện thoại
        binding.loginPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(LoginOptionActivity.this);
                //thiết lập tiêu đề:
                builder.setTitle("Trang đăng nhập Số điện thoại");
                //thiết lập icon:
                builder.setIcon(R.drawable.ic_phone);
                //thiết lập nội dung cho dialog:
                builder.setMessage("Bạn đang vào trang đăng nhập Số điện thoại?");
                //thiết lập các nút lệnh để người dùng tương tác:
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(LoginOptionActivity.this, "Bạn vào trang đăng nhập số điện thoại thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginOptionActivity.this, LoginPhoneActivity.class));
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
    }

    private void LoginGoogle() {
        Log.d(TAG, "LoginGoogle: GoogleSingin");

        Intent googleSignInIntent = googleSignInClient.getSignInIntent();
        googleSinginClientURL.launch(googleSignInIntent);
    }
    private ActivityResultLauncher<Intent> googleSinginClientURL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: googleSinginClientURL");
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();

                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                        try {
                           GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d(TAG, "onActivityResult: Account ID: "+account.getId());
                            firebaseAuthWithGoogleAccount(account.getIdToken());
                        }
                        catch (Exception e){
                            Log.e(TAG, "onActivityResult: Lỗi: ", e);
                        }
                    }
                    else {
                        Log.d(TAG, "onActivityResult: Thoát");
                        Utils.toastyError(LoginOptionActivity.this, "Thoát");

                    }
                }
            }
    );

    private void firebaseAuthWithGoogleAccount(String idToken) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: idToken: "+idToken);

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (authResult.getAdditionalUserInfo().isNewUser()){
                            Log.d(TAG, "onSuccess: Đang update dữ liệu");
                            Utils.toastySuccess(LoginOptionActivity.this, "Đang update dữ liệu");
                            updateDatabase();
                        }
                        else {
                            Log.d(TAG, "onSuccess: Tạo tài khoản google");
                            if (firebaseAuth.getCurrentUser() != null){
                                googleSignInClient.signOut();
                                startActivity(new Intent(LoginOptionActivity.this, MainActivity.class));
                                finishAffinity();
                            }

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Lỗi: ", e);
                    }
                });
    }

    private void updateDatabase() {
        progressDialog.setMessage("Update lên cơ sở dữ liệu");

        long timestamp = Utils.getTimestamp();
        String registerUserEmail = firebaseAuth.getCurrentUser().getEmail();
        String registerUserUid = firebaseAuth.getUid();
        String name = firebaseAuth.getCurrentUser().getDisplayName();


        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("name",name);
        hashMap.put("phoneCode","");
        hashMap.put("phoneNumber","");
        hashMap.put("profileImageUrl","");
        hashMap.put("dob","");
        hashMap.put("userType","Google");
        hashMap.put("typringTo","");
        hashMap.put("timestamp",timestamp);
        hashMap.put("onlineStatus",true);
        hashMap.put("email", registerUserEmail);
        hashMap.put("uid", registerUserUid);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(registerUserUid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Utils.toastySuccess(LoginOptionActivity.this, "Tạo tài khoản thành công");
                        Log.d(TAG, "onSuccess: Thành công");
                        startActivity(new Intent(LoginOptionActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Utils.toastyError(LoginOptionActivity.this,"Lỗi: "+e);
                        Log.d(TAG, "onFailure: Lỗi: "+e);
                    }
                });
    }

}