package io.scalac.degree.connection;

import com.github.kubatatami.judonetworking.Endpoint;
import com.github.kubatatami.judonetworking.EndpointFactory;
import com.github.kubatatami.judonetworking.callbacks.Callback;
import com.github.kubatatami.judonetworking.controllers.json.simple.JsonSimpleRestController;
import com.github.kubatatami.judonetworking.transports.OkHttpTransportLayer;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;

import java.util.List;

import io.scalac.degree.Configuration;
import io.scalac.degree.connection.model.ConferenceSingleApiModel;
import io.scalac.degree.connection.model.ConferencesApiModel;
import io.scalac.degree.connection.model.LinkApiModel;
import io.scalac.degree.connection.model.ProposalTypesApiModel;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.connection.model.SpeakerFullApiModel;
import io.scalac.degree.connection.model.SpeakerShortApiModel;
import io.scalac.degree.connection.model.TalkFullApiModel;
import io.scalac.degree33.BuildConfig;

@EBean
public class Connection implements DevoxxApi {

	@RootContext Context context;

	private DevoxxApi devoxxApi;

	@AfterInject void afterInject() {
		setupApi();
	}

	private void setupApi() {
		final JsonSimpleRestController controller = new JsonSimpleRestController();
		final OkHttpTransportLayer transportLayer = new OkHttpTransportLayer();

		final Endpoint endpoint = EndpointFactory.createEndpoint(context,
				controller, transportLayer, Configuration.API_URL);
		endpoint.setDebugFlags(BuildConfig.LOGGING
				? Endpoint.FULL_DEBUG : Endpoint.NO_DEBUG);
		endpoint.setCacheEnabled(true);
		endpoint.setCallbackThread(false);

		devoxxApi = endpoint.getService(DevoxxApi.class);
	}

	@Override public void conferences(Callback<ConferencesApiModel> callback) {
		devoxxApi.conferences(callback);
	}

	@Override
	public void conference(String confCOde, Callback<ConferenceSingleApiModel> callback) {
		devoxxApi.conference(confCOde, callback);
	}

	@Override
	public void speakers(String confCode, Callback<List<SpeakerShortApiModel>> callback) {
		devoxxApi.speakers(confCode, callback);
	}

	@Override
	public void schedules(String confCode, Callback<List<LinkApiModel>> callback) {
		devoxxApi.schedules(confCode, callback);
	}

	@Override
	public void specificSchedule(
			String confCode, String dayOfWeek, Callback<List<SlotApiModel>> callback) {
		devoxxApi.specificSchedule(confCode, dayOfWeek, callback);
	}

	@Override
	public void proposalTypes(String confCode, Callback<ProposalTypesApiModel> callback) {
		devoxxApi.proposalTypes(confCode, callback);
	}

	@Override
	public void speaker(String confCode, String uuid, Callback<SpeakerFullApiModel> callback) {
		devoxxApi.speaker(confCode, uuid, callback);
	}

	@Override
	public void talk(String confCode, String talkId, Callback<TalkFullApiModel> callback) {
		devoxxApi.talk(confCode, talkId, callback);
	}
}
