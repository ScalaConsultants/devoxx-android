package com.devoxx.data.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmSpeaker extends RealmObject {
    @PrimaryKey
    private String uuid;
    private String firstName;
    private String lastName;
    private String avatarURL;
    private String bio;
    private String bioAsHtml;
    private String company;
    private String blog;
    private String twitter;
    private String lang;
    private RealmList<RealmTalk> acceptedTalks;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBioAsHtml() {
        return bioAsHtml;
    }

    public void setBioAsHtml(String bioAsHtml) {
        this.bioAsHtml = bioAsHtml;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public RealmList<RealmTalk> getAcceptedTalks() {
        return acceptedTalks;
    }

    public void setAcceptedTalks(RealmList<RealmTalk> acceptedTalks) {
        this.acceptedTalks = acceptedTalks;
    }

    public static class Contract {
        public static final String UUID = "uuid";
    }
}
