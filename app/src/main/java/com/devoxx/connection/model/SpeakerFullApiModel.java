package com.devoxx.connection.model;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import com.devoxx.data.model.RealmSpeaker;
import com.devoxx.data.model.RealmTalk;

public class SpeakerFullApiModel extends SpeakerBaseApiModel {
    public String bio;
    public String bioAsHtml;
    public String company;
    public String blog;
    public String twitter;
    public String lang;
    public List<LinkApiModel> links;
    public List<TalkShortApiModel> acceptedTalks;

    public static SpeakerFullApiModel fromDb(RealmSpeaker dbModel) {
        final SpeakerFullApiModel result = new SpeakerFullApiModel();
        result.avatarURL = dbModel.getAvatarURL();
        result.firstName = dbModel.getFirstName();
        result.lastName = dbModel.getLastName();
        result.bio = dbModel.getBio();
        result.bioAsHtml = dbModel.getBioAsHtml();
        result.blog = dbModel.getBlog();
        result.company = dbModel.getCompany();
        result.lang = dbModel.getLang();
        result.links = new ArrayList<>();
        result.twitter = dbModel.getTwitter();

        final RealmList<RealmTalk> talks = dbModel.getAcceptedTalks();
        result.acceptedTalks = new ArrayList<>(talks.size());

        for (RealmTalk realmTalk : talks) {
            result.acceptedTalks.add(TalkShortApiModel.fromDb(realmTalk));
        }

        return result;
    }
}
