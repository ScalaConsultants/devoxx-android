package com.devoxx.data.vote.voters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.RatingBar;

import com.afollestad.materialdialogs.MaterialDialog;
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

    @Override
    public void showVoteDialog(Context context, String talkId, IOnVoteForTalkListener listener) {
        final MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        final MaterialDialog dialog = builder
                .customView(R.layout.talk_rating_layout, true)
                .title("Talk voting")
                .positiveText("Vote!")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        final View customView = dialog.getCustomView();
                        final RatingBar ratingBar = (RatingBar) customView.findViewById(R.id.talkRatingBar);
                        final int rating = (int) ratingBar.getRating();
                        voteForTalk(talkId, listener);
                    }
                })
                .build();
        dialog.show();

        final View customView = dialog.getCustomView();
        final RatingBar ratingBar = (RatingBar) customView.findViewById(R.id.talkRatingBar);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            DrawableCompat.setTint(ratingBar.getProgressDrawable(), Color.parseColor("#E3B505"));
        }
    }
}
