package com.devoxx.data.vote.voters;

import com.devoxx.connection.vote.model.VoteTalkModel;
import com.devoxx.data.vote.interfaces.IOnGetTalkVotesListener;
import com.devoxx.data.vote.interfaces.IOnVoteForTalkListener;

import org.androidannotations.annotations.EBean;

@EBean
public class FakeVoter extends AbstractVoter {

    @Override
    public void voteForTalk(String talkId, IOnVoteForTalkListener listener) {
        if (System.currentTimeMillis() % 3 == 0) {
            listener.onVoteForTalkSucceed();
        } else {
            listener.onVoteForTalkFailed();
        }
    }

    @Override
    public void getVotesCountForTalk(String confCode, String talkId, IOnGetTalkVotesListener listener) {
        if (listener != null) {
            if (System.currentTimeMillis() % 2 == 0) {
                listener.onTalkVotesAvailable(VoteTalkModel.createFake("3"));
            } else {
                listener.onTalkVotesAvailable(VoteTalkModel.createFake("45"));
            }
        }
    }
}
