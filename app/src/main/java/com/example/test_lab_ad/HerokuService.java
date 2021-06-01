package com.example.test_lab_ad;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface HerokuService {
    @GET("read")
    Call<List<Name>> all();

    @POST("read")
    Call<Name> create(@Body Name name);
}
