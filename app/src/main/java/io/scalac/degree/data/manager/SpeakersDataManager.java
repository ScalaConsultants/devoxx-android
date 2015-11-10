package io.scalac.degree.data.manager;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.scalac.degree.connection.model.SpeakerShortApiModel;
import io.scalac.degree.data.downloader.SpeakersDownloader;
import io.scalac.degree.utils.Logger;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 29/10/2015
 */
@EBean
public class SpeakersDataManager extends AbstractDataManager<SpeakerShortApiModel> {

	@Bean SpeakersDownloader speakersDownloader;

	private List<SpeakerShortApiModel> speakers = new ArrayList<>();

	public List<SpeakerShortApiModel> getSpeakers() {
		return speakers;
	}

	@Background public void fetchSpeakers(
			final String confCode,
			@Nullable IDataManagerListener<SpeakerShortApiModel> listener) {

		try {
			notifyAboutStart(listener);
			speakers.clear();
			final List<SpeakerShortApiModel> speakers = speakersDownloader
					.downloadSpeakers(confCode);
			speakers.addAll(Stream.of(speakers)
					.sortBy(new Function<SpeakerShortApiModel, Comparable>() {
						@Override public Comparable apply(SpeakerShortApiModel value) {
							return value.lastName;
						}
					})
					.collect(Collectors.<SpeakerShortApiModel>toList()));
			notifyAboutSuccess(listener, speakers);
		} catch (IOException e) {
			Logger.exc(e);
			notifyAboutFailed(listener);
		}
	}
}
