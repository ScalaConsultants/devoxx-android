package io.scalac.degree.connection.vote;

import java.util.List;

import io.scalac.degree.connection.vote.model.VoteTalkModel;
import io.scalac.degree.connection.vote.model.VoteTalkSimpleModel;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Vote API based on that service:
 * <url>https://bitbucket.org/jonmort/devoxx-vote-api</url>
 */
public interface VoteApi {

    @GET("/{confCode}/top/talks")
    Call<List<VoteTalkSimpleModel>> topTalks(
            @Path("confCode") String confCode
    );

    @GET("/{confCode}/talk/{talkId}")
    Call<VoteTalkModel> talk(
            @Path("confCode") String confCode,
            @Path("talkId") String talkId
    );
}
