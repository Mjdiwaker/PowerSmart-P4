package com.neotechindia.plugsmart.activity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface2 {

    @POST("registration")
    Call<GeneralResponse> submit_button(@Body SubmitDetailsRequest request2);

}
