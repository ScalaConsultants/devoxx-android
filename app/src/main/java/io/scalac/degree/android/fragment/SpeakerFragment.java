package io.scalac.degree.android.fragment;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.List;

import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.connection.model.SpeakerFullApiModel;
import io.scalac.degree.connection.model.SpeakerShortApiModel;
import io.scalac.degree.connection.model.TalkShortApiModel;
import io.scalac.degree.connection.model.TalkSpeakerApiModel;
import io.scalac.degree.data.manager.AbstractDataManager;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree.data.manager.SpeakerDataManager;
import io.scalac.degree.utils.AnimateFirstDisplayListener;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_speaker)
public class SpeakerFragment extends BaseFragment implements
        AbstractDataManager.IDataManagerListener<SpeakerFullApiModel> {

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    @FragmentArg
    TalkSpeakerApiModel speaker;
    @FragmentArg
    SpeakerShortApiModel speakerShortApiModel;
    @ViewById(R.id.imageSpeaker)
    ImageView imageView;
    @ViewById(R.id.textName)
    TextView textName;
    @ViewById(R.id.textBio)
    TextView textBio;
    @ViewById(R.id.linearLayoutTalks)
    LinearLayout linearLayoutTalks;
    @ViewById(R.id.textViewTalks)
    View textViewTalks;
    @Bean
    SpeakerDataManager speakerDataManager;
    @Bean
    SlotsDataManager slotsDataManager;
    @StringRes(R.string.devoxx_conference)
    String conferenceCode;
    DisplayImageOptions imageLoaderOptions;
    private ImageLoadingListener animateFirstListener =
            new AnimateFirstDisplayListener();

    @AfterViews
    void afterViews() {
        logFlurryEvent("Speaker_profile_watched");

        initImageLoader();

        setupView();
    }

    @Override
    public boolean needsToolbarSpinner() {
        return false;
    }

    @Nullable
    @Override
    public String getTitleAsString() {
        return determineName();
    }

    private String determineName() {
        return speaker != null ? speaker.name : speakerShortApiModel.getName();
    }

    private String determineImageUrl() {
        return speaker != null ? speaker.avatarURL : speakerShortApiModel.avatarURL;
    }

    private void setupView() {
        imageLoader.displayImage(determineImageUrl(), imageView,
                imageLoaderOptions, animateFirstListener);

        textName.setText(getTitleAsString());

        final String uuid;
        if (speaker != null) {
            uuid = Uri.parse(speaker.link.href).getLastPathSegment();
        } else {
            uuid = speakerShortApiModel.uuid;
        }
        speakerDataManager.fetchSpeaker(conferenceCode, uuid, this);
    }

    private void initImageLoader() {
        imageLoaderOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.th_background)
                .showImageForEmptyUri(R.drawable.no_photo)
                .showImageOnFail(R.drawable.no_photo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public void onDataAvailable(SpeakerFullApiModel speakerFullApiModel) {
        textBio.setText(Html.fromHtml(speakerFullApiModel.bioAsHtml));
        textBio.setMovementMethod(LinkMovementMethod.getInstance());
        if (speakerFullApiModel.acceptedTalks.size() > 0) {
            for (final TalkShortApiModel talkModel : speakerFullApiModel.acceptedTalks) {
                Button buttonItem = (Button) LayoutInflater.from(getActivity())
                        .inflate(R.layout.button_item, linearLayoutTalks, false);
                buttonItem.setText(talkModel.title);
                buttonItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SlotApiModel slotModel = slotsDataManager.
                                getSlotByTalkId(talkModel.id);
                        if (slotModel != null) {
                            getMainActivity().replaceFragment(TalkFragment_.builder()
                                    .slotModel(slotModel).build(), true);
                        } else {
                            Toast.makeText(getContext(), "Brak talka.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                linearLayoutTalks.addView(buttonItem);
            }
        } else {
            textViewTalks.setVisibility(View.GONE);
            linearLayoutTalks.setVisibility(View.GONE);
        }

        imageLoader.displayImage(speakerFullApiModel.avatarURL, imageView,
                imageLoaderOptions, animateFirstListener);
    }

    @Override
    public void onDataStartFetching() {

    }

    @Override
    public void onDataAvailable(List<SpeakerFullApiModel> items) {

    }

    @Override
    public void onDataError() {

    }
}
