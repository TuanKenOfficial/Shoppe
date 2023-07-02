package com.example.shoppe.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.shoppe.R;
import com.example.shoppe.activities.toast.Utils;
import com.example.shoppe.adapter.AdapterImagePicked;
import com.example.shoppe.databinding.ActivityAdCreateBinding;
import com.example.shoppe.models.ModelImagePicked;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdCreateActivity extends AppCompatActivity {

    private ActivityAdCreateBinding binding;

    private static final String TAG="AD_CREATE";

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private Uri imageUri = null;

    private ArrayList<ModelImagePicked> imagePickedArrayList;

    private AdapterImagePicked adapterImagePicked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Vui lòng đợi");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        //load danh sách category
        ArrayAdapter<String> adapterCategories = new ArrayAdapter<>(this, R.layout.row_categore_createad, Utils.categories);
        binding.categoryEt.setAdapter(adapterCategories);

        //load danh sách condition
        ArrayAdapter<String> adapterConditions = new ArrayAdapter<>(this, R.layout.row_condition_createad, Utils.conditions);
        binding.conditionEt.setAdapter(adapterConditions);

        imagePickedArrayList = new ArrayList<>();

        loadImages();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.toolbarImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickOption();
            }
        });

        binding.postAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });


    }
    private void loadImages() {
        Log.d(TAG, "loadImages: ");
        adapterImagePicked = new AdapterImagePicked(this, imagePickedArrayList);

        binding.imagesRv.setAdapter(adapterImagePicked);
    }

    private void showImagePickOption() {
        Log.d(TAG, "showImagePickOption: ");

        androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(AdCreateActivity.this,binding.toolbarImageBtn);
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
                        pickFromGallery();
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
                        pickFromCamera();
                    }
                    else {
                        Log.d(TAG, "onActivityResult: Tất cả hoặc chỉ có một quyền");
                        Toast.makeText(AdCreateActivity.this, "Quyền camera hoặc storage", Toast.LENGTH_SHORT).show();
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
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(AdCreateActivity.this, "Quyền Storage chưa cấp quyền", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void pickFromGallery() {
        Log.d(TAG, "pickFromGallery: ");
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
                        Log.d(TAG, "onActivityResult: Hình ảnh thư viện: "+imageUri);
                        Intent data = result.getData();
                        imageUri = data.getData();

                        String timestamp = ""+Utils.getTimestamp();

                        ModelImagePicked modelImagePicked = new ModelImagePicked(timestamp,imageUri,null,false);
                        imagePickedArrayList.add(modelImagePicked);

                        loadImages();
                    }
                    else {
                        Toast.makeText(AdCreateActivity.this, "Hủy", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );


    private void pickFromCamera() {
        Log.d(TAG, "pickFromCamera: ");
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");


        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLaucher.launch(intent);

    }
    private ActivityResultLauncher<Intent> cameraActivityResultLaucher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG, "onActivityResult: Camera"+imageUri);
                        String timestamp = ""+System.currentTimeMillis();

                        ModelImagePicked modelImagePicked = new ModelImagePicked(timestamp,imageUri,null,false);
                        imagePickedArrayList.add(modelImagePicked);

                        loadImages();
                    }
                    else {
                        Toast.makeText(AdCreateActivity.this, "Hủy", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private String brand="";
    private String category="";
    private String condition="";
    private String address="";
    private String price="";
    private String title="";
    private String description="";
    private double latitude=0;
    private double longtide=0;

    private void validate() {
        brand = binding.brandEt.getText().toString().trim();
        category = binding.categoryEt.getText().toString().trim();
        condition = binding.conditionEt.getText().toString().trim();
        address = binding.locationEt.getText().toString().trim();
        price = binding.priceEt.getText().toString().trim();
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionsEt.getText().toString().trim();


        if (brand.isEmpty()){
            binding.brandEt.setError("Nhập thương hiệu của bạn");
            binding.brandEt.requestFocus();
        }
        else if (category.isEmpty()){
            binding.categoryEt.setError("Bạn chưa chọn loại");
            binding.categoryEt.requestFocus();
        }
        else if (condition.isEmpty()){
            binding.conditionEt.setError("Bạn chưa chọn tình trạng");
            binding.conditionEt.requestFocus();
        }
        else if (title.isEmpty()){
            binding.titleEt.setError("Bạn chưa nhập tiêu đề");
            binding.titleEt.requestFocus();
        }

        else if (description.isEmpty()){
            binding.descriptionsEt.setError("Bạn chưa mô tả sản phẩm");
            binding.descriptionsEt.requestFocus();
        }
        else if (price.isEmpty()){
            binding.priceEt.setError("Bạn chưa nhập giá cho sản phẩm");
            binding.priceEt.requestFocus();
        }
        else if (imagePickedArrayList.isEmpty()){
            Utils.toastyInfo(this,"Chọn ít nhất trên hình ảnh");
        }
        else{
            postAd();
        }
    }

    private void postAd() {
        Log.d(TAG, "postAd: ");

        progressDialog.setMessage("Đang upload quảng cáo");
        progressDialog.show();

        long timestamp = Utils.getTimestamp();
        String uidAd = firebaseAuth.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ads");
        String keyId = reference.push().getKey();


        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",""+keyId);
        hashMap.put("uid",firebaseAuth.getUid());
        hashMap.put("brand",brand);
        hashMap.put("category",category);
        hashMap.put("condition",condition);
        hashMap.put("address",address);
        hashMap.put("price",price);
        hashMap.put("title",title);
        hashMap.put("description",description);
        hashMap.put("timestamp",timestamp);
        hashMap.put("latitude",latitude);
        hashMap.put("longtide",longtide);
        hashMap.put("status",Utils.AD_STATUS_AVAILABLE);


        reference.child(keyId)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: quảng cáo được phát hành");
                        Log.d(TAG, "uploadImagesStorage: ");

                        uploadImageUrl(keyId); //lấy theo Id Ads

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: ",e);
                        progressDialog.dismiss();
                        Utils.toastyError(AdCreateActivity.this,"Lỗi: " +e);
                    }
                });


    }

    private void uploadImageUrl(String keyId) {
        for (int i=0; i<imagePickedArrayList.size(); i++){
            ModelImagePicked modelImagePicked = imagePickedArrayList.get(i);

            String imageName = modelImagePicked.getId();
            String filePathName = "Ads/"+imageName;


            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
            int imageIndexForProgress = i+1;

            Log.d(TAG, "uploadImagesStorage: "+imageName);
            Log.d(TAG, "uploadImagesStorage: "+filePathName);
            Log.d(TAG, "uploadImagesStorage: "+imageIndexForProgress);

            storageReference.putFile(modelImagePicked.getImageUri())
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred())/ snapshot.getTotalByteCount();

                            String message = "Uploading "+imageIndexForProgress +" of "+ imagePickedArrayList.size() + "hình ảnh...\n tiến trình "
                                    + (int)progress +"%";
                            progressDialog.setMessage(message);
                            progressDialog.show();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "onSuccess: ");

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            String uploadImageUrl = uriTask.getResult().toString();

                            if(uriTask.isSuccessful()){
                                HashMap <String,Object> hashMap = new HashMap<>();
                                if (uploadImageUrl != null){
                                    hashMap.put("imageUrl",""+uploadImageUrl);
                                    hashMap.put("idImageAd",""+modelImagePicked.getImageUri());
                                    Log.d(TAG, "UploadImageStorageUrl: imageUrl: "+uploadImageUrl);
                                    Log.d(TAG, "UploadImageStorageUrl: idImageAd: "+modelImagePicked.getImageUri());

                                }

                                //lấy theo id Ads. Chia cây Ads-IdAd->Images->ImageId->ImageData
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ads");
                                databaseReference.child(keyId).child("Images").child(imageName)
                                        .updateChildren(hashMap);
                                startActivity(new Intent(AdCreateActivity.this,MainActivity.class));

                            }
                            progressDialog.dismiss();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Lỗi",e);
                            Utils.toastyError(AdCreateActivity.this,"Lỗi: "+e);
                            progressDialog.dismiss();
                        }
                    });
        }
    }

}