package com.devoxx.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.annimon.stream.Optional;
import com.devoxx.BuildConfig;
import com.devoxx.Configuration;
import com.devoxx.connection.cfp.CfpApi;
import com.devoxx.data.conference.ConferenceManager;
import com.devoxx.data.model.RealmConference;
import com.devoxx.utils.Logger;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

@EBean(scope = EBean.Scope.Singleton)
public class Connection {

    @RootContext
    Context context;

    @SystemService
    ConnectivityManager cm;

    @Pref
    ConnectionConfigurationStore_ connectionConfigurationStore;

    @Bean
    ConferenceManager conferenceManager;

    private DevoxxApi devoxxApi;
    private CfpApi cfpApi;

    @AfterInject
    void afterInject() {
        initiCfpApi();
    }

    public void setupConferenceApi(String conferenceEndpoint) {
        connectionConfigurationStore.edit().activeConferenceApiUrl()
                .put(conferenceEndpoint).apply();

        final OkHttpClient client = new OkHttpClient();

        if (BuildConfig.LOGGING) {
            client.interceptors().add(new LoggingInterceptor());
        }

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(conferenceEndpoint)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        devoxxApi = retrofit.create(DevoxxApi.class);
    }

    public DevoxxApi getDevoxxApi() {
        if (devoxxApi == null) {
            final Optional<RealmConference> conference = conferenceManager.getActiveConference();
            if (conference.isPresent()) {
                setupConferenceApi(conference.get().getCfpURL());
            }
        }
        return devoxxApi;
    }

    public CfpApi getCfpApi() {
        return cfpApi;
    }

    public String getActiveConferenceApiUrl() {
        return connectionConfigurationStore.activeConferenceApiUrl().get();
    }

    private void initiCfpApi() {
        final OkHttpClient client = new OkHttpClient();
        if (BuildConfig.LOGGING) {
            client.interceptors().add(new LoggingInterceptor());
        }

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Configuration.CFP_API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        cfpApi = retrofit.create(CfpApi.class);
    }

    public boolean isOnline() {
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
