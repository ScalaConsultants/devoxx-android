package com.devoxx.connection;

import android.content.Context;

import com.devoxx.Configuration;
import com.devoxx.connection.cfp.CfpApi;
import com.devoxx.utils.Logger;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

@EBean(scope = EBean.Scope.Singleton)
public class Connection {

    @RootContext
    Context context;

    @Pref
    ConnectionConfigurationStore_ connectionConfigurationStore;

    private DevoxxApi devoxxApi;
    private CfpApi cfpApi;

    public void warmUpCfpApi() {
        final OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new LoggingInterceptor());
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Configuration.CFP_API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        cfpApi = retrofit.create(CfpApi.class);
    }

    public void setupConferenceApi(String conferenceEndpoint) {
        connectionConfigurationStore.edit().activeConferenceApiUrl()
                .put(conferenceEndpoint).apply();

        final OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new LoggingInterceptor());

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(conferenceEndpoint)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        devoxxApi = retrofit.create(DevoxxApi.class);
    }

    public DevoxxApi getDevoxxApi() {
        return devoxxApi;
    }

    public CfpApi getCfpApi() {
        return cfpApi;
    }

    public String getActiveConferenceApiUrl() {
        return connectionConfigurationStore.activeConferenceApiUrl().get();
    }

    class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
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
