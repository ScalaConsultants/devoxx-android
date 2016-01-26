package com.devoxx.connection.vote;

import android.content.Context;

import com.devoxx.utils.Logger;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.IOException;

import com.devoxx.Configuration;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

@EBean
public class VoteConnection {

    @RootContext
    Context context;

    private VoteApi voteApi;

    @AfterInject
    void afterInject() {
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
        voteApi = retrofit.create(VoteApi.class);
    }

    public VoteApi getVoteApi() {
        return voteApi;
    }

    class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            Logger.l(String.format("[VOTE_API] Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Logger.l(String.format("[VOTE_API] Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        }
    }
}
