package com.example.shoppe.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppe.activities.toast.Utils;
import com.example.shoppe.databinding.ActivityDeleteAccountBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteAccountActivity extends AppCompatActivity {

    private ActivityDeleteAccountBinding binding;

    private static  final String TAG ="DELETE_ACCOUNT";

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Vui lòng đợi");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //quay lại
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(DeleteAccountActivity.this, MainActivity.class));
               finishAffinity();
            }
        });

        binding.deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

    }

    private void deleteAccount() {
        Log.d(TAG, "deleteAccount: ");

        progressDialog.setMessage("Xóa tài khoản");
        progressDialog.show();

        String myUid = firebaseAuth.getUid();

        firebaseUser.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                progressDialog.setMessage("Xóa tài khoản user Ads");
                //Xóa tài khoản user quảng cáo, hiện tại chưa làm việc tới nó nẽ lưu Db->Ads->AdsId
                DatabaseReference refAd = FirebaseDatabase.getInstance().getReference("Ads");
                refAd.orderByChild("uid").equalTo(myUid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    ds.getRef().removeValue();
                                    Log.d(TAG, "onDataChange: "+ds);
                                }
                                progressDialog.setMessage("Xóa tài khoản user data");
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(myUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: Thành công");
                                        progressDialog.dismiss();
                                        Utils.toastySuccess(DeleteAccountActivity.this,"Thành công");
                                        startMain(); //chuyển về trang main
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Thất bại"+e);
                                        progressDialog.dismiss();
                                        Utils.toastyError(DeleteAccountActivity.this,"Thất bại");
                                        startMain();//chuyển về trang main
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d(TAG, "onSuccess: Thất bại");
                Utils.toastySuccess(DeleteAccountActivity.this,"Thành công");
            }
        });
    }
    private void startMain(){
        startActivity(new Intent(DeleteAccountActivity.this, MainActivity.class));
        finishAffinity();
    }
}