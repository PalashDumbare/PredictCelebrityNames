package com.example.identifypeople.repo;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.example.identifypeople.model.Predictions;
import com.example.identifypeople.RetrofitInstance;
import com.example.identifypeople.interfaces.ImageClassification;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageClassificationRepo {
    private Context context;
    private File file;
    private CompositeDisposable compositeDisposable;

    public ImageClassificationRepo(Context context) {
        this.context = context;
     }

    public void predict_name(Bitmap bitmap){
        try {
            createFile(bitmap);
             ImageClassification classification = RetrofitInstance.getRetrofitInstance().create(ImageClassification.class);

            DisposableObserver<Predictions> request  = classification.predict_name(getPartImage()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Predictions>() {
                        @Override
                        public void onNext(Predictions predictions) {
                            Toast.makeText(context,new Gson().toJson(predictions),Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });


        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void createFile(Bitmap bitmap) throws IOException {
        file = new File(context.getCacheDir(), "PREDICTION_IMAGE");
        file.createNewFile();


        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
         } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MultipartBody.Part getPartImage(){
        String imagePath = file.getAbsolutePath();

        if (imagePath.isEmpty()){
            return null;
        }
        File file = new File(imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        return body;
    }
}
