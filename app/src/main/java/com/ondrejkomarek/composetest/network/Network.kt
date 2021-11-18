package com.ondrejkomarek.composetest.network

import com.ondrejkomarek.composetest.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Timeout constant used for connection, read, write operations on our HttpClient.
 * Time in seconds.
 * */
const val CONNECTION_TIMEOUT = 30L

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

	@Provides
	@Singleton
	fun provideMovieApi(retrofit: Retrofit): MovieApi = retrofit.create()

}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

	@Provides
	@Singleton
	fun provideMoshi(): Moshi = Moshi.Builder()
		.add(KotlinJsonAdapterFactory())
		.build()

	@Provides
	@Singleton
	fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().apply {
		if (BuildConfig.DEBUG) {
			addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
		}
		connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
		writeTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
		readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
		addInterceptor(provideQueryInterceptor())
	}.build()

	@Provides
	@Singleton
	fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
		.baseUrl(BuildConfig.API_BASE_URL)
		.client(client)
		.addConverterFactory(MoshiConverterFactory.create(moshi))
		.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
		.build()

}

private fun provideQueryInterceptor(): Interceptor = Interceptor { chain ->
	val original = chain.request()
	val originalHttpUrl = original.url

	val url = originalHttpUrl.newBuilder()
		.addQueryParameter("api_key", "392fe77c77afb19427f16500e60885c3")
		.build()

	// Request customization: add request headers
	val requestBuilder = original.newBuilder().url(url)

	val request = requestBuilder.build()
	chain.proceed(request)
}
