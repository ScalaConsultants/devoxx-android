package io.scalac.degree.data.model;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.scalac.degree.connection.model.SpeakerFullApiModel;
import io.scalac.degree.connection.model.TalkShortApiModel;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 30/10/2015
 */
public class SpeakerDbModel extends RealmObject {
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
	private RealmList<TalkDbModel> acceptedTalks;

	public static SpeakerDbModel fromApiModel(Realm realm, SpeakerFullApiModel apiModel) {
		final SpeakerDbModel result = new SpeakerDbModel();
		result.setAvatarURL(apiModel.avatarURL);
		result.setBio(apiModel.bio);
		result.setBioAsHtml(apiModel.bioAsHtml);
		result.setBlog(apiModel.blog);
		result.setCompany(apiModel.company);
		result.setFirstName(apiModel.firstName);
		result.setLang(apiModel.lang);
		result.setLastName(apiModel.lastName);
		result.setTwitter(apiModel.twitter);
		result.setUuid(apiModel.uuid);

		final RealmList<TalkDbModel> talks = new RealmList<>();
		for (TalkShortApiModel talkShortApiModel : apiModel.acceptedTalks) {
			talks.add(TalkDbModel.fromApiModel(talkShortApiModel));
		}

		realm.beginTransaction();
		result.setAcceptedTalks(talks);
		realm.commitTransaction();

		return result;
	}

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

	public RealmList<TalkDbModel> getAcceptedTalks() {
		return acceptedTalks;
	}

	public void setAcceptedTalks(RealmList<TalkDbModel> acceptedTalks) {
		this.acceptedTalks = acceptedTalks;
	}
}
