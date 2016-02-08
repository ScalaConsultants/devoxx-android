package com.devoxx.data.downloader;

import com.devoxx.connection.Connection;
import com.google.gson.Gson;
import com.devoxx.utils.Logger;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.devoxx.connection.DevoxxApi;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.connection.model.SpecificScheduleApiModel;
import com.devoxx.data.cache.SlotsCache;

import retrofit.Call;

@EBean
public class SlotsDownloader {

    private final List<String> AVAILABLE_CONFERENCE_DAYS = Collections.unmodifiableList(
            Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
    );

    @Bean
    Connection connection;

    @Bean
    SlotsCache slotsCache;

    public boolean isDataUpdateNeeded(String confCode) {
        return !slotsCache.isValid(confCode);
    }

    public List<SlotApiModel> downloadTalks(String confCode) throws IOException {
        final List<SlotApiModel> result;

        if (slotsCache.isValid(confCode)) {
            updateAllDataAsync(confCode);
            return slotsCache.getData(confCode);
        } else {
            result = downloadAllData(confCode);
        }

        return result;
    }

    private String deserializeData(List<SlotApiModel> result) {
        return new Gson().toJson(result);
    }

    @Background
    void updateAllDataAsync(String confCode) {
        try {
            downloadAllData(confCode);
        } catch (IOException e) {
            Logger.l("Can't update slots!");
            Logger.exc(e);
        }
    }

    private List<SlotApiModel> downloadAllData(String confCode) throws IOException {
        final List<SlotApiModel> result = new ArrayList<>();
        for (String day : AVAILABLE_CONFERENCE_DAYS) {
            downloadTalkSlotsForDay(confCode, result, day);
        }
        slotsCache.upsert(deserializeData(result), confCode);
        return result;
    }

    private void downloadTalkSlotsForDay(
            String confCode, List<SlotApiModel> result, String day) throws IOException {
        final DevoxxApi devoxxApi = connection.getDevoxxApi();
        final Call<SpecificScheduleApiModel> call =
                devoxxApi.specificSchedule(confCode, day);

        result.addAll(call.execute().body().slots);
    }
}
