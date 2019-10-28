package com.example.identifypeople.interfaces;

import com.example.identifypeople.model.Predictions;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;



public interface ImageClassification {
    @Multipart
    @POST("predict_name")
    Observable<Predictions>predict_name(@Part MultipartBody.Part image);
}
