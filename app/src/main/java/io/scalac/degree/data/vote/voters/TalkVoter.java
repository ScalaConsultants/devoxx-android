package io.scalac.degree.data.vote.voters;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;

import io.scalac.degree.connection.vote.VoteApi;
import io.scalac.degree.connection.vote.VoteConnection;
import io.scalac.degree.connection.vote.model.VoteTalkModel;
import io.scalac.degree.data.vote.interfaces.IOnGetTalkVotesListener;
import io.scalac.degree.data.vote.interfaces.IOnVoteForTalkListener;
import io.scalac.degree33.R;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * scalac.io
 * jacek.modrakowski@scalac.io
 * 25/11/2015.
 */
@EBean
public class TalkVoter extends AbstractVoter {

    @StringRes(R.string.devoxx_conference)
    String confCode;

    @Bean
    VoteConnection voteConnection;

    @Override
    public void voteForTalk(String talkId, IOnVoteForTalkListener listener) {
        // TODO Connect to voting service. Handle user_id somehow (Huntly).
    }

    @Override
    public void getVotesCountForTalk(String talkId, final IOnGetTalkVotesListener listener) {
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
