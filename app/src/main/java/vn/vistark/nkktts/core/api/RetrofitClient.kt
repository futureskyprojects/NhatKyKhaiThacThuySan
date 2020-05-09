package vn.vistark.nkktts.core.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.vistark.nkktts.core.constants.Constants
import java.util.concurrent.TimeUnit


class RetrofitClient {
    companion object {
        private var retrofit: Retrofit? = null
        fun getClient(): Retrofit? {
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val request = chain.request().newBuilder().addHeader(
                    "Authorization",
                    "${Constants.tokenType} ${Constants.userToken}"
                ).build()
                chain.proceed(request)
            }
            httpClient.connectTimeout(60, TimeUnit.SECONDS)
            httpClient.readTimeout(60, TimeUnit.SECONDS)
            retrofit = Retrofit.Builder()
                .baseUrl("http://nhatkyktts.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
            return retrofit
        }

        fun getTempClient(tokenType: String, token: String): Retrofit? {
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val request = chain.request().newBuilder().addHeader(
                    "Authorization",
                    "${tokenType} ${token}"
                ).build()
                chain.proceed(request)
            }
            httpClient.connectTimeout(10, TimeUnit.SECONDS)
            httpClient.readTimeout(10, TimeUnit.SECONDS)
            retrofit = Retrofit.Builder()
                .baseUrl("http://nhatkyktts.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
            return retrofit
        }
    }
}