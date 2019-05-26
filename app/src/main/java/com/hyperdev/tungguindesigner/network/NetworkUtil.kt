package com.hyperdev.tungguindesigner.network

import android.content.Context
import com.hyperdev.tungguindesigner.BuildConfig
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class NetworkUtil {

    companion object {
        private var retrofit: Retrofit? = null
        fun getClient(context: Context): Retrofit? {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val clientTest = OkHttpClient.Builder()
                .addInterceptor(ChuckInterceptor(context))
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            if(retrofit == null){
                retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL +"/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(clientTest)
                    .build()
            }
            return retrofit
        }
    }
}