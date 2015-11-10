package io.scalac.degree.connection;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.scalac.degree.Configuration;
import io.scalac.degree.utils.Logger;
import retrofit.CallAdapter;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

@EBean
public class Connection {

	@RootContext Context context;

	private DevoxxApi devoxxApi;

	@AfterInject void afterInject() {
		setupApi();
	}

	private void setupApi() {
		final OkHttpClient client = new OkHttpClient();
		client.interceptors().add(new LoggingInterceptor());

		final Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(Configuration.API_URL)
				.client(client)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		devoxxApi = retrofit.create(DevoxxApi.class);
	}

	public DevoxxApi getDevoxxApi() {
		return devoxxApi;
	}

	class LoggingInterceptor implements Interceptor {
		@Override public Response intercept(Chain chain) throws IOException {
			Request request = chain.request();

			long t1 = System.nanoTime();
			Logger.l(String.format("Sending request %s on %s%n%s",
					request.url(), chain.connection(), request.headers()));

			Response response = chain.proceed(request);

			long t2 = System.nanoTime();
			Logger.l(String.format("Received response for %s in %.1fms%n%s",
					response.request().url(), (t2 - t1) / 1e6d, response.headers()));

			return response;
		}
	}
}
