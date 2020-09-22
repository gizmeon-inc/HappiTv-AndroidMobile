package com.happi.android.webservice;


import com.happi.android.models.AnalyticsResponseModel;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import static com.happi.android.utils.ConstantUtils.ANALYTICS_BASE_URL;

public class AnalyticsApi {

    public static AnalyticsService create(){

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ANALYTICS_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(AnalyticsService.class);
    }

    public static AnalyticsServiceScalar createScalar(){

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(1,TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ANALYTICS_BASE_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return  retrofit.create(AnalyticsServiceScalar.class);

    }

    public interface AnalyticsService{

        @POST("poppo-analytics-api/device")
        Observable<AnalyticsResponseModel> firstTimeInstall(@Body JsonObject details);

        @POST("poppo-analytics-api/event")
        Observable<AnalyticsResponseModel> eventCall(@Body JsonObject eventDetails);

    }
    public interface AnalyticsServiceScalar{

        @POST("device")
        Call<String> firstTimeInstall(@Body JsonObject details);

        @POST("event")
        Call<String> eventCall2(@Body JsonObject eventDetails);
    }
}
