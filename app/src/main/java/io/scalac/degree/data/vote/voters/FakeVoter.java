package io.scalac.degree.data.vote.voters;

import org.androidannotations.annotations.EBean;

import io.scalac.degree.connection.vote.model.VoteTalkModel;
import io.scalac.degree.data.vote.interfaces.IOnGetTalkVotesListener;
import io.scalac.degree.data.vote.interfaces.IOnVoteForTalkListener;
import io.scalac.degree.data.vote.interfaces.ITalkVoter;

/**
 * scalac.io
 * jacek.modrakowski@scalac.io
 * 08/12/2015.
 */
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
    public void getVotesCountForTalk(String talkId, IOnGetTalkVotesListener listener) {
        if (listener != null) {
            if (System.currentTimeMillis() % 2 == 0) {
                listener.onTalkVotesAvailable(VoteTalkModel.createFake("3"));
            } else {
                listener.onTalkVotesAvailable(VoteTalkModel.createFake("45"));
            }
        }
    }
}
