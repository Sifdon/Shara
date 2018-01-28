package org.seemsGood.shara.util;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.module.AppGlideModule;
import com.google.firebase.storage.FirebaseStorage;

@com.bumptech.glide.annotation.GlideModule
public class ImageLoader extends AppGlideModule {

    public static void loadImage(ImageView imageView, String ref) {
        if (ref == null) return;

        FirebaseUtil.getInstance()
                .getStorageReference(ref)
                .getDownloadUrl()
                .addOnSuccessListener(uri ->
                        GlideApp.with(imageView.getContext())
                                .load(uri)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(imageView));
    }

}