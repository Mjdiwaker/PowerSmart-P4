package com.neotechindia.plugsmart.activity;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit_request2 {


    private static Retrofit getBuilder(){
        String Clienturl="http://192.168.6.105:8080/kngproduct/rest/mapp/client/";

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        return new Retrofit.Builder()
                .baseUrl(Clienturl)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    private static RequestInterface2 service = (RequestInterface2) getBuilder().create(RequestInterface2.class);

    public static Call<GeneralResponse> submitDetails(SubmitDetailsRequest request2) {
        return service.submit_button(request2);
    }
}
