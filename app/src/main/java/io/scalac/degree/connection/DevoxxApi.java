package io.scalac.degree.connection;

import java.util.List;

import io.scalac.degree.connection.model.ConferenceSingleApiModel;
import io.scalac.degree.connection.model.ConferencesApiModel;
import io.scalac.degree.connection.model.LinkApiModel;
import io.scalac.degree.connection.model.ProposalTypesApiModel;
import io.scalac.degree.connection.model.SpeakerFullApiModel;
import io.scalac.degree.connection.model.SpeakerShortApiModel;
import io.scalac.degree.connection.model.SpecificScheduleApiModel;
import io.scalac.degree.connection.model.TalkFullApiModel;
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
    Call<SpeakerFullApiModel> speaker(
            @Path("confCode") String confCode,
            @Path("uuid") String uuid
    );

    @GET
    Call<SpeakerFullApiModel> speaker(
            @Url String url
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
