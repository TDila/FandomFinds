package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.canhub.cropper.CropImageView;
import com.vulcan.fandomfinds.R;

import java.io.ByteArrayOutputStream;

public class ImageCropper extends AppCompatActivity {

    ImageView imageCropBackButton;
    TextView cropButton;
    CropImageView cropImageView;
    String userString;
    String socialMediaString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cropper);

        initComponents();
        setUriToCropImgView();
        setListeners();
    }
    private void initComponents() {
        imageCropBackButton = findViewById(R.id.imageCropBackButton);
        cropButton = findViewById(R.id.cropButton);
        cropImageView = findViewById(R.id.cropImageView);
    }
    private void setUriToCropImgView() {
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");
        if(imagePath == null){
            finish();
        }
        userString = intent.getStringExtra("user");
        socialMediaString = intent.getStringExtra("socialMedia");
        Uri uri = Uri.parse(imagePath);

        cropImageView.setImageUriAsync(uri);
    }
    private void setListeners() {
        imageCropBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ImageCropper.this,ProfileInformationActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent1.putExtra("user",userString);
                intent1.putExtra("socialMedia",socialMediaString);
                startActivity(intent1);
                finish();
            }
        });
        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = cropImageView.getCroppedImage();
                Uri uri1 = getImageUri(ImageCropper.this,bitmap);

                if(uri1 != null){
                    Intent intent1 = new Intent(ImageCropper.this,ProfileInformationActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent1.putExtra("croppedImgPath",String.valueOf(uri1));
                    intent1.putExtra("user",userString);
                    intent1.putExtra("socialMedia",socialMediaString);
                    startActivity(intent1);
                    finish();
                }
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        if(path != null){
            return Uri.parse(path);
        }
        return null;
    }
}