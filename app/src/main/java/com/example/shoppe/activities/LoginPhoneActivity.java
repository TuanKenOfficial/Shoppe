package com.example.shoppe.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.shoppe.activities.toast.Utils;
import com.example.shoppe.databinding.ActivityLoginPhoneBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LoginPhoneActivity extends AppCompatActivity {

    private ActivityLoginPhoneBinding binding;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId;

    private static final String TAG ="LOGINPHONE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.phoneInputRl.setVisibility(View.VISIBLE);
        binding.otpInputRl.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        phoneLoginCallBack();
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //gửi SĐT
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

        //chưa gửi xác thực
        binding.resentOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resentOTP(forceResendingToken);
            }
        });

        //xác thực OTP
        binding.btnVeryfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = binding.otpView.getOTP();
                Log.d(TAG, "onClick: OTP: "+otp);

                if (otp.isEmpty()){
                    Utils.toastyInfo(LoginPhoneActivity.this,"mã OTP không được bỏ trống");
                }
                else if (otp.length()<6){
                    Utils.toastyInfo(LoginPhoneActivity.this,"mã OTP là 6 số");
                }
                else {

                    verifyPhone(mVerificationId,otp);
                }
            }
        });


    }




    private String phoneCode ="", phoneNumber="", phoneNumberWithCode="";

    private void updateData() {
        phoneCode = binding.phoneCode.getSelectedCountryCodeWithPlus();
        phoneNumber = binding.phoneNumber.getText().toString().trim();
        phoneNumberWithCode = phoneCode+phoneNumber;

        Log.d(TAG, "updateData: phoneCode: "+phoneCode);
        Log.d(TAG, "updateData: phoneNumber: "+phoneNumber);
        Log.d(TAG, "updateData: phoneNumberWithCode: "+phoneNumberWithCode);



        if (phoneNumber.isEmpty()){
            Utils.toastyInfo(LoginPhoneActivity.this,"Số điện thoại đang trống");
        }
        else if (phoneNumber.length()<9){
            Utils.toastyInfo(LoginPhoneActivity.this,"Số điện thoại phải 9 số trở lên, cộng với đầu +84 hoặc nhập 10 số");
        }
        else if (phoneNumber.length()>10){
            Utils.toastyInfo(LoginPhoneActivity.this,"Số điện thoại không lớn hơn 10 số");
        }
        else {
            startPhone();
        }

    }


    private void startPhone() {
        Log.d(TAG, "startPhone: ");

        progressDialog.setMessage("Gửi OTP đến số điện thoại: "+phoneNumberWithCode);
        progressDialog.show();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumberWithCode)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(LoginPhoneActivity.this)
                .setCallbacks(mCallbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void phoneLoginCallBack() {
        Log.d(TAG, "phoneLoginCallBack: ");

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted: ");
                signinWithPhone(credential);
                Utils.toastyInfo(LoginPhoneActivity.this,"Thành công VerificationId");

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.dismiss();
                Utils.toastyInfo(LoginPhoneActivity.this,"Lỗi VerificationId: "+e);
                Log.e(TAG, "onVerificationFailed: ",e );
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                mVerificationId = verificationId;
                forceResendingToken =token ;

                progressDialog.dismiss();

                binding.phoneInputRl.setVisibility(View.INVISIBLE);
                binding.otpInputRl.setVisibility(View.VISIBLE);

                Utils.toastySuccess(LoginPhoneActivity.this, "OTP đã được gửi đến: "+phoneNumberWithCode);

                binding.txtOTP.setText("OTP đã được gửi đến: "+phoneNumberWithCode);
            }
        };
    }
    private void resentOTP(PhoneAuthProvider.ForceResendingToken token) {

        Log.d(TAG, "resentOTP: resendToke: "+token);

        progressDialog.setMessage("Gửi lại OTP đến số điện thoại: "+phoneNumberWithCode);
        progressDialog.show();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumberWithCode)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(LoginPhoneActivity.this)
                .setCallbacks(mCallbacks)
                .setForceResendingToken(token)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void verifyPhone(String verificationId, String otp) {
        Log.d(TAG, "verifyPhone: verificationId: "+verificationId);
        Log.d(TAG, "verifyPhone: otp: "+otp);

        progressDialog.setMessage("Xác thực OTP");
        progressDialog.show();

        PhoneAuthCredential credential =PhoneAuthProvider.getCredential(verificationId,otp);
        signinWithPhone(credential);

    }

    private void signinWithPhone(PhoneAuthCredential credential) {
        Log.d(TAG, "signinWithPhone: ");
        progressDialog.setMessage("Đăng nhập số điện thoại");
        progressDialog.show();

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (authResult.getAdditionalUserInfo().isNewUser()){
                            progressDialog.dismiss();
                            Log.d(TAG, "onSuccess: Đang update dữ liệu và tạo tài khoản");
                            Utils.toastySuccess(LoginPhoneActivity.this, "Đang update dữ liệu");
                            updateDatabase();
                        }
                        else {
                            progressDialog.dismiss();
                            Log.d(TAG, "onSuccess: Tài khoản đã có");
                            Utils.toastySuccess(LoginPhoneActivity.this, "Đang update dữ liệu");
                            startActivity(new Intent(LoginPhoneActivity.this, MainActivity.class));
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


        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("name","");
        hashMap.put("phoneCode",phoneCode);
        hashMap.put("phoneNumber",phoneNumber);
        hashMap.put("phone",phoneNumberWithCode);
        hashMap.put("profileImageUrl","");
        hashMap.put("dob","");
        hashMap.put("userType","Phone");
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
                        Utils.toastySuccess(LoginPhoneActivity.this, "Tạo tài khoản thành công");
                        Log.d(TAG, "onSuccess: Thành công");
                        startActivity(new Intent(LoginPhoneActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Utils.toastyError(LoginPhoneActivity.this,"Lỗi: "+e);
                        Log.d(TAG, "onFailure: Lỗi: "+e);
                    }
                });
    }
}