package com.devoxx.data.vote.voters;

import com.devoxx.connection.vote.model.VoteTalkModel;
import com.devoxx.data.vote.interfaces.IOnGetTalkVotesListener;
import com.devoxx.data.vote.interfaces.IOnVoteForTalkListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import com.devoxx.connection.vote.VoteApi;
import com.devoxx.connection.vote.VoteConnection;

import com.devoxx.R;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

@EBean
public class TalkVoter extends AbstractVoter {

    @Bean
    VoteConnection voteConnection;

    @Override
    public void voteForTalk(String talkId, IOnVoteForTalkListener listener) {
        // TODO Connect to service.
        listener.onVoteForTalkSucceed();
    }

    @Override
    public void getVotesCountForTalk(String confCode, String talkId, final IOnGetTalkVotesListener listener) {
        final VoteApi voteApi = voteConnection.getVoteApi();
        final Call<VoteTalkModel> voteTalkModelCall = voteApi.talk(confCode, talkId);
        voteTalkModelCall.enqueue(new Callback<VoteTalkModel>() {
            @Override
            public void onResponse(Response<VoteTalkModel> response, Retrofit retrofit) {
                if (listener != null) {
                    listener.onTalkVotesAvailable(response.body());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (listener != null) {
                    listener.onTalkVotesError();
                }
            }
        });
    }
}
