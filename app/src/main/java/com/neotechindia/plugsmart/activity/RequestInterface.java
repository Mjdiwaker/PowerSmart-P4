package com.neotechindia.plugsmart.activity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface RequestInterface {

    @POST("register")
    Call<GeneralResponse> submit_button(@Body SubmitDetailsRequest request);

}
