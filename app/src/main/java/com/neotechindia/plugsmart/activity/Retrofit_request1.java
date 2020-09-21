package com.neotechindia.plugsmart.activity;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class Retrofit_request1 {

    private static Retrofit getBuilder(){
        //String Buyerurl="http://10.10.0.196:8080/kngproduct/rest/mapp/buyer/";
        String Buyerurl=" http://192.168.6.105:8080/KnowNGrowPro/rest/mapp/buyer/";


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        return new Retrofit.Builder()
                .baseUrl(Buyerurl)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    private static RequestInterface1 service = getBuilder().create(RequestInterface1.class);

    public static Call<GeneralResponse> submitDetails(SubmitDetailsRequest request1) {
        return service.submit_button(request1);
    }

}
