package io.scalac.degree.connection;

import com.github.kubatatami.judonetworking.annotations.RequestMethod;
import com.github.kubatatami.judonetworking.callbacks.Callback;
import com.github.kubatatami.judonetworking.controllers.raw.RawRestController;

import java.util.List;

import io.scalac.degree.connection.model.ConferenceSingleApiModel;
import io.scalac.degree.connection.model.ConferencesApiModel;
import io.scalac.degree.connection.model.LinkApiModel;
import io.scalac.degree.connection.model.ProposalTypesApiModel;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.connection.model.SpeakerFullApiModel;
import io.scalac.degree.connection.model.SpeakerShortApiModel;
import io.scalac.degree.connection.model.TalkFullApiModel;

public interface DevoxxApi {

	@RawRestController.Rest(value = "conferences")
	@RequestMethod(async = true) void conferences(
			Callback<ConferencesApiModel> callback
	);

	@RawRestController.Rest(value = "conferences/{0}")
	@RequestMethod(async = true) void conference(
			String confCOde, Callback<ConferenceSingleApiModel> callback
	);

	@RawRestController.Rest(value = "conferences/{0}/speakers")
	@RequestMethod(async = true) void speakers(
			String confCode, Callback<List<SpeakerShortApiModel>> callback
	);

	@RawRestController.Rest(value = "conferences/{0}/schedules")
	@RequestMethod(async = true) void schedules(
			String confCode, Callback<List<LinkApiModel>> callback
	);

	@RawRestController.Rest(value = "conferences/{0}/schedules/{1}")
	@RequestMethod(async = true) void specificSchedule(
			String confCode, String dayOfWeek, Callback<List<SlotApiModel>> callback
	);

	@RawRestController.Rest(value = "conferences/{0}/proposalTypes")
	@RequestMethod(async = true) void proposalTypes(
			String confCode, Callback<ProposalTypesApiModel> callback
	);

	@RawRestController.Rest(value = "conferences/{0}/speakers/{1}")
	@RequestMethod(async = true) void speaker(
			String confCode, String uuid, Callback<SpeakerFullApiModel> callback
	);

	@RawRestController.Rest(value = "conferences/{0}/talks/{1}")
	@RequestMethod(async = true) void talk(
			String confCode, String talkId, Callback<TalkFullApiModel> callback
	);
}
