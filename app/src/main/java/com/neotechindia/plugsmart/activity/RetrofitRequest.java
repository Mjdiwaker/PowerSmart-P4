package com.neotechindia.plugsmart.activity;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RetrofitRequest {

    private static Retrofit getBuilder(){
        String edgeurl="http://192.168.6.105:8080/smartplug/rest/user/";
//        String edgeurl="http://192.168.6.113:8080/smartplug/rest/user/";
   //     String url1="http://localhost:8080/kngproduct/rest/mapp/registration";


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        return new Retrofit.Builder()
                .baseUrl(edgeurl)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    private static RequestInterface service = getBuilder().create(RequestInterface.class);

    public static Call<GeneralResponse> submitDetails(SubmitDetailsRequest request) {
        return service.submit_button(request);
    }


}
