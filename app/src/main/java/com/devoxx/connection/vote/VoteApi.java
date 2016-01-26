package com.devoxx.connection.vote;

import com.devoxx.connection.vote.model.VoteTalkModel;
import com.devoxx.connection.vote.model.VoteTalkSimpleModel;

import java.util.List;

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
