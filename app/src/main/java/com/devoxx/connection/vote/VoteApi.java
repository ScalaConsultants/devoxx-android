package com.devoxx.connection.vote;

import com.devoxx.connection.vote.model.VoteApiModel;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Vote API based on that service:
 * <url>https://bitbucket.org/jonmort/devoxx-vote-api</url>
 */
public interface VoteApi {

    @POST("/{confCode}/vote")
    Call<VoteApiModel> vote(
            @Path("confCode") String confCode,
            @Body VoteApiModel model
    );
}
