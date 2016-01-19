package io.scalac.degree.data.downloader;

import com.google.gson.Gson;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.scalac.degree.connection.DevoxxApi;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.connection.model.SpecificScheduleApiModel;
import io.scalac.degree.data.cache.SlotsCache;
import io.scalac.degree.utils.Logger;
import retrofit.Call;

@EBean
public class SlotsDownloader extends AbstractDownloader<SlotApiModel> {

    private final List<String> AVAILABLE_CONFERENCE_DAYS = Collections.unmodifiableList(
            Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
    );

    @Bean
    SlotsCache slotsCache;

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
