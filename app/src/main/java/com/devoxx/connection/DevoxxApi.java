package com.devoxx.connection;

import com.devoxx.connection.model.ProposalTypesApiModel;
import com.devoxx.connection.model.TalkFullApiModel;
import com.squareup.okhttp.ResponseBody;

import java.util.List;

import com.devoxx.connection.model.ConferenceSingleApiModel;
import com.devoxx.connection.model.ConferencesApiModel;
import com.devoxx.connection.model.LinkApiModel;
import com.devoxx.connection.model.SpeakerFullApiModel;
import com.devoxx.connection.model.SpeakerShortApiModel;
import com.devoxx.connection.model.SpecificScheduleApiModel;
import com.devoxx.connection.model.TracksApiModel;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Url;

public interface DevoxxApi {

    @GET("/api/conferences/{confCode}/speakers")
    Call<List<SpeakerShortApiModel>> speakers(
            @Path("confCode") String confCode
    );

    @GET("/api/conferences/{confCode}/schedules/{dayOfWeek}")
    Call<SpecificScheduleApiModel> specificSchedule(
            @Path("confCode") String confCode,
            @Path("dayOfWeek") String dayOfWeek
    );

    @GET("/api/conferences/{confCode}/speakers/{uuid}")
    Call<ResponseBody> speaker(
            @Path("confCode") String confCode,
            @Path("uuid") String uuid
    );

    @GET("/api/conferences/{confCode}/speakers/{uuid}")
    Call<SpeakerFullApiModel> speakerModel(
            @Path("confCode") String confCode,
            @Path("uuid") String uuid
    );

    @GET
    Call<SpeakerFullApiModel> speaker(
            @Url String url
    );

    @GET("/api/conferences/{confCode}/tracks")
    Call<TracksApiModel> tracks(
            @Path("confCode") String confCode
    );

    @GET("/api/conferences")
    Call<ConferencesApiModel> conferences();

    @GET("/api/conferences/{confCode}")
    Call<ConferenceSingleApiModel> conference(
            @Path("confCode") String confCode
    );

    @GET("api/conferences/{confCode}/schedules")
    Call<List<LinkApiModel>> schedules(
            @Path("confCOde") String confCode
    );

    @GET("/api/conferences/{confCode}/proposalTypes")
    Call<ProposalTypesApiModel> proposalTypes(
            @Path("confCode") String confCode
    );

    @GET("/api/conferences/{confCode}/talks/{talkId}")
    Call<TalkFullApiModel> talk(
            @Path("confCode") String confCode,
            @Path("talkId") String talkId
    );
}
