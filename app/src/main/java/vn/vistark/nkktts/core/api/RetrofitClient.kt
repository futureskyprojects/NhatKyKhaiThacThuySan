package vn.vistark.nkktts.core.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.vistark.nkktts.core.constants.Constants


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
            retrofit = Retrofit.Builder()
                .baseUrl("http://nhatkyktts.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
            return retrofit
        }
    }
}