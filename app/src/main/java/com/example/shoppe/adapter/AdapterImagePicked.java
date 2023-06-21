package com.example.shoppe.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoppe.R;
import com.example.shoppe.activities.toast.Utils;
import com.example.shoppe.databinding.RowImagesCreateBinding;
import com.example.shoppe.models.ModelImagePicked;

import java.util.ArrayList;

public class AdapterImagePicked extends RecyclerView.Adapter<AdapterImagePicked.HolderImagePicked>{

    private RowImagesCreateBinding binding;

    private static final String TAG ="ROWIMAGE";

    private Context context;

    private ArrayList<ModelImagePicked> modelImagePickedArrayList;

    public AdapterImagePicked(Context context, ArrayList<ModelImagePicked> modelImagePickedArrayList) {
        this.context = context;
        this.modelImagePickedArrayList = modelImagePickedArrayList;
    }

    @NonNull
    @Override
    public HolderImagePicked onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowImagesCreateBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderImagePicked(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderImagePicked holder, int position) {
        ModelImagePicked model = modelImagePickedArrayList.get(position);

        Uri imageUri = model.getImageUri();

        try {
            Glide.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.image)
                    .into(holder.imageIv);
        }catch (Exception e){
            Log.d(TAG, "onBindViewHolder: "+e);
            Utils.toastyError(context,""+e);
        }

        holder.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modelImagePickedArrayList.remove(model);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelImagePickedArrayList.size();
    }

    public class HolderImagePicked extends RecyclerView.ViewHolder{

        ImageView imageIv;
        ImageButton closeBtn;
        public HolderImagePicked(@NonNull View itemView) {
            super(itemView);

            imageIv = binding.imageIv;
            closeBtn = binding.closeBtn;
        }
    }
}
