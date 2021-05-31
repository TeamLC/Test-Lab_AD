package com.example.test_lab_ad;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface HerokuService {
    @GET("/")
    Call<ResponseBody> hello();
}
