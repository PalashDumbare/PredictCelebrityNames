package com.example.identifypeople.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;


import com.example.identifypeople.R;
import com.example.identifypeople.databinding.ActivityMainBinding;
import com.example.identifypeople.repo.ImageClassificationRepo;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;


public class MainActivity extends AppCompatActivity  {

     private ActivityMainBinding binding;
    private ImageClassificationRepo imageClassificationRepo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        init();



    }

    private void init() {

        imageClassificationRepo = new ImageClassificationRepo(this);



        binding.cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                Bitmap bitmap = cameraKitImage.getBitmap();

               // bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);

                binding.imageViewResult.setImageBitmap(bitmap);

                imageClassificationRepo.predict_name(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        binding.btnToggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cameraView.toggleFacing();
            }
        });

        binding.btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cameraView.captureImage();
            }
        });

        makeButtonVisible();

     }


    private void makeButtonVisible() {
                binding.btnDetectObject.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        binding.cameraView.start();
        super.onResume();
    }
}
