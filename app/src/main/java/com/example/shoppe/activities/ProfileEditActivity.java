package com.example.shoppe.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.bumptech.glide.Glide;
import com.example.shoppe.R;
import com.example.shoppe.Users;
import com.example.shoppe.activities.toast.Utils;
import com.example.shoppe.databinding.ActivityProfileEditBinding;
import com.example.shoppe.databinding.FragmentProfileBinding;
import com.example.shoppe.fragments.ProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class ProfileEditActivity extends AppCompatActivity {

    //view binding
    private ActivityProfileEditBinding binding;

    //firebase user
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private static final String TAG = "PROFILE_EDIT_TAG";


    //xử lí profile ảnh
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    //image picked uri
    private Uri image_uri;
    private String imageUrl = "" ;
    //Xử lý đăng lên storage firebase
    private StorageTask uploadTask;
    StorageReference Postreference;

    private String name ="";
    private String email="";
    private String dob="";
    private String password="";
    private String phoneCode="";
    private String phoneNumber="";
    private String phoneNumberWithCode="";

    private  String mUserType="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi trong giây lát");
        progressDialog.setCanceledOnTouchOutside(false);

        //setup firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //load tất cả hồ sơ profile
        loadUserInfo();
        //init permissions array
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        //handle click, button back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileEditActivity.this,MainActivity.class));
//                initFragment();
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CropImage.activity().setAspectRatio(1,1)
//                        .setCropShape(CropImageView.CropShape.RECTANGLE)
//                        .start(ProfileEditActivity.this);
                Log.d(TAG, "onClick: bấm vào thay đổi ảnh");
                imageDialog();
            }
        });

        //handle click, button upload
        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();

            }
        });
    }
//    //khi activity back sẽ chuyển sang fragment
//    private void initFragment() {
//        binding.toolbarRl.setVisibility(View.GONE);
//        binding.rv.setVisibility(View.GONE);
//
//        ProfileFragment firstFragment = new ProfileFragment();
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//
//        FragmentTransaction ft = fragmentManager.beginTransaction();
//
//        ft.replace(R.id.container_body, firstFragment);
//
//        ft.commit();
//
//    }

    private void validate() {
        email = binding.emailEt.getText().toString().trim();
        name = binding.nameEt.getText().toString().trim();
        dob = binding.dobEt.getText().toString().trim();
        phoneCode = binding.phoneCode.getSelectedCountryCodeWithPlus();
        phoneNumber = binding.phoneNumber.getText().toString().trim();


        if (image_uri == null){
            Log.d(TAG, "onClick: image_uri = null");
            uploadProfileDb(null); // suy nghĩ chỗ này

        }
        else {
            Log.d(TAG, "onClick: image_uri != null");
            uploadProfileImageStorageDb();

        }
    }

    private void uploadProfileImageStorageDb() {
        progressDialog.setMessage("Upload");
        progressDialog.show();
        Log.d(TAG, "updateAnh: Đã vô tới update ảnh");
        //name and path of image
        String filePathAndName = "profile_images/"+"profile_"+ firebaseAuth.getUid();
        Log.d(TAG, "updateAnh: "+filePathAndName);
        //upload image
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        storageReference.putFile(image_uri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        Log.d(TAG, "onProgress: "+progress);
                        progressDialog.setMessage("Upload hình ảnh. Tiến triển: "+(int)progress + "%");

                    }
                })
                .addOnSuccessListener(taskSnapshot -> {
                    //get url of uploaded image
                    Log.d(TAG, "uploadProfileImageStorageDb: Upload thành công...");
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    String uploadImageUrl = uriTask.getResult().toString();
                    if(uriTask.isSuccessful()){
                        uploadProfileDb(uploadImageUrl);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Lỗi: "+e);
                        Utils.toastyError(ProfileEditActivity.this, "Lỗi" +e);
                    }
                });
    }


    private void uploadProfileDb(String imageUrl) {
        progressDialog.setMessage("Update user");
        progressDialog.show();

        String registerUserUid = firebaseAuth.getUid();
        long timestamp = Utils.getTimestamp();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("name",""+name);
        hashMap.put("dob",""+dob);
        hashMap.put("typringTo","");
        hashMap.put("timestamp",timestamp);
        hashMap.put("onlineStatus",true);
        hashMap.put("uid", registerUserUid);

        Log.d(TAG, "uploadProfileDb: ");
        Log.d(TAG, "uploadProfileDb: name:"+name);
        Log.d(TAG, "uploadProfileDb: dob"+dob);
        Log.d(TAG, "uploadProfileDb: timestamp"+timestamp);
        Log.d(TAG, "uploadProfileDb: uid"+registerUserUid);

        if (imageUrl != null){
            hashMap.put("profileImageUrl",""+imageUrl);
            Log.d(TAG, "uploadProfileDb: profileImageUrl"+imageUrl);
        }

        if(mUserType.equalsIgnoreCase("Google")){
            hashMap.put("userType","Google");
            String registerUserEmail = firebaseAuth.getCurrentUser().getEmail();
            hashMap.put("email",registerUserEmail);
            hashMap.put("phoneCode",""+phoneCode);
            hashMap.put("phoneNumber",""+phoneNumber);
            Log.d(TAG, "uploadProfileDb: "+registerUserEmail);
            Log.d(TAG, "uploadProfileDb: "+phoneCode);
            Log.d(TAG, "uploadProfileDb: "+phoneNumber);
        }
        else if(mUserType.equalsIgnoreCase("Email")){
            hashMap.put("userType","Email");
            hashMap.put("email", email);
            hashMap.put("phoneCode",""+phoneCode);
            hashMap.put("phoneNumber",""+phoneNumber);
            Log.d(TAG, "uploadProfileDb: "+email);
            Log.d(TAG, "uploadProfileDb: "+phoneCode);
            Log.d(TAG, "uploadProfileDb: "+phoneNumber);
        }
        else if(mUserType.equalsIgnoreCase("Phone")){
            hashMap.put("userType","Phone");
            hashMap.put("email", email);
            hashMap.put("phoneCode",""+phoneCode);
            hashMap.put("phoneNumber",""+phoneNumber);
            Log.d(TAG, "uploadProfileDb: "+email);
            Log.d(TAG, "uploadProfileDb: "+phoneCode);
            Log.d(TAG, "uploadProfileDb: "+phoneNumber);
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid())
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Utils.toastySuccess(ProfileEditActivity.this, " Update thành công cơ sở dữ liệu Phone");
                        Log.d(TAG, "onSuccess: Thành công");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Utils.toastyError(ProfileEditActivity.this,"Lỗi: "+e);
                        Log.d(TAG, "onFailure: Lỗi: "+e);
                    }
                });

    }



    //Xử lý hình ảnh chỉnh sửa hồ sơ, cập nhật ảnh
    private void imageDialog(){
        PopupMenu popupMenu = new PopupMenu(ProfileEditActivity.this,binding.profileIv);
        popupMenu.getMenu().add(Menu.NONE,1,1,"Camera");
        popupMenu.getMenu().add(Menu.NONE,2,2,"Gallery");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId ==1){
                    Log.d(TAG, "onMenuItemClick: Mở camera, check camera");
                    if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
                        requestCameraPemissions.launch(new String[]{Manifest.permission.CAMERA});
                    }else {
                        requestCameraPemissions.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
                    }
                }
                else if (itemId==2){
                    Log.d(TAG, "onMenuItemClick: Mở storage, check storage");
                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
                        pickFromGallery1();
                    }else {
                        requestStoragePemissions.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
                return false;
            }
        });
    }
    private ActivityResultLauncher<String[]> requestCameraPemissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String,Boolean>>(){

                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    Log.d(TAG, "onActivityResult: "+result.toString());
                    boolean areAllGranted = true;
                    for (Boolean isGranted: result.values()){
                        areAllGranted = areAllGranted && isGranted;
                    }
                    if (areAllGranted){
                        Log.d(TAG, "onActivityResult: Tất cả quyền camera & storage");
                        pickFromCamera1();
                    }
                    else {
                        Log.d(TAG, "onActivityResult: Tất cả hoặc chỉ có một quyền");
                        Toast.makeText(ProfileEditActivity.this, "Quyền camera hoặc storage", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private ActivityResultLauncher<String> requestStoragePemissions = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted){
                        pickFromGallery1();
                    }
                    else {
                        Toast.makeText(ProfileEditActivity.this, "Quyền Storage chưa cấp quyền", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void pickFromGallery1() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
       galleryActivityResultLaucher.launch(intent);
    }
    private ActivityResultLauncher<Intent> galleryActivityResultLaucher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG, "onActivityResult: Hình ảnh thư viện: "+image_uri);
                        Intent data = result.getData();
                        image_uri = data.getData();
                        try {
                            Glide.with(ProfileEditActivity.this)
                                    .load(image_uri)
                                    .placeholder(R.drawable.edituser)
                                    .into(binding.profileIv);
                        }catch (Exception e){
                            Log.d(TAG, "onActivityResult: "+e);
                            Toast.makeText(ProfileEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(ProfileEditActivity.this, "Hủy", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );


    private void pickFromCamera1() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        cameraActivityResultLaucher.launch(intent);

    }
    private ActivityResultLauncher<Intent> cameraActivityResultLaucher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG, "onActivityResult: Hình ảnh: "+image_uri);
                        try {
                            Glide.with(ProfileEditActivity.this)
                                    .load(image_uri)
                                    .placeholder(R.drawable.edituser)
                                    .into(binding.profileIv);
                        }catch (Exception e){
                            Log.d(TAG, "onActivityResult: "+e);
                            Toast.makeText(ProfileEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(ProfileEditActivity.this, "Hủy", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    //load tất cả những thông tin cần có của phần profile
    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Đang tải thông tin người dùng của người dùng");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get all info of user here from snapshot
                        String email = "" + snapshot.child("email").getValue();
                        String name = "" + snapshot.child("name").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        String phoneCode = "" + snapshot.child("phoneCode").getValue();
                        String phoneNumber = "" + snapshot.child("phoneNumber").getValue();
                        String profileImageUrl = "" + snapshot.child("profileImageUrl").getValue();
                        String uid = "" + snapshot.child("uid").getValue();
                        String dob = "" + snapshot.child("dob").getValue();
                        String password = "" + snapshot.child("dob").getValue();
                        mUserType = "" + snapshot.child("userType").getValue();
                        String phone = phoneCode + phoneNumber;

                        if (timestamp.equals("null")){
                            timestamp ="0";
                        }

                        //format date
                        String formattedDate = Utils.formatTimestampDate(Long.parseLong(timestamp));

                        /*set image, using picasso
                         *nếu bạn là admin thì sẽ load hình profile admin
                         * và ngược lại nếu bạn là người dùng thì load hình profile người dùng
                         * và cuối cùng nếu bạn thay đổi ảnh profile của cả hai thì nó sẽ load hình thay đổi đó */

                        if (mUserType.equals("Google")){
                            binding.profileIv.setImageResource(R.drawable.google);
                        }
                        else if (mUserType.equals("Phone")){
                            binding.profileIv.setImageResource(R.drawable.ic_phone);
                        }
                        else if (mUserType.equals("Email")){
                            binding.profileIv.setImageResource(R.drawable.ic_profile);
                        }

                        if(mUserType.equalsIgnoreCase("Email") || mUserType.equalsIgnoreCase("Google")){
                            binding.emailTil.setEnabled(false);
                            binding.emailEt.setEnabled(false);
                        }
                        else {
                            binding.phoneCode.setEnabled(false);
                            binding.phoneNumber.setEnabled(false);
                            binding.phoneTil.setEnabled(false);
                        }
                        //set data to ui
                        binding.nameEt.setText(name);
                        binding.emailEt.setText(email);
                        binding.dobEt.setText(dob);
                        binding.phoneNumber.setText(phoneNumber);
                        try {
                            int phoneCodeInt = Integer.parseInt(phoneCode.replace("+",""));
                            binding.phoneCode.setCountryForPhoneCode(phoneCodeInt);
                        }
                        catch (Exception e){
                            Log.d(TAG, "onDataChange: "+e);
                        }
                        try {
                            Glide.with(ProfileEditActivity.this)
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