package io.scalac.degree.android.fragment;

import com.annimon.stream.Optional;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.connection.model.TalkSpeakerApiModel;
import io.scalac.degree.data.Settings_;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree.data.manager.SpeakersDataManager;
import io.scalac.degree.data.model.RealmSpeaker;
import io.scalac.degree.data.model.RealmTalk;
import io.scalac.degree33.R;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EFragment(R.layout.fragment_speaker)
public class SpeakerFragment extends BaseFragment {

    @FragmentArg
    TalkSpeakerApiModel speakerTalkModel;

    @FragmentArg
    String speakerDbUuid;

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
    SpeakersDataManager speakersDataManager;

    @Bean
    SlotsDataManager slotsDataManager;

    @Pref
    Settings_ settings;

    private RealmSpeaker realmSpeaker;

    @AfterViews
    void afterViews() {
        final String uuid;
        if (speakerTalkModel != null) {
            uuid = TalkSpeakerApiModel.getUuidFromLink(speakerTalkModel.link);
        } else {
            uuid = speakerDbUuid;
        }

        final Subscriber<RealmSpeaker> s = new Subscriber<RealmSpeaker>() {
            @Override
            public void onStart() {
                super.onStart();
                getMainActivity().showLoader();
            }

            @Override
            public void onCompleted() {
                getMainActivity().hideLoader();
                realmSpeaker = speakersDataManager.getByUuid(uuid);
                setupView();

                getMainActivity().invalidateToolbarTitle();
            }

            @Override
            public void onError(Throwable e) {
                getMainActivity().hideLoader();
            }

            @Override
            public void onNext(RealmSpeaker o) {

            }
        };

        speakersDataManager.fetchSpeaker(settings.activeConferenceCode().get(), uuid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(s);
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
        return speakerTalkModel != null ? speakerTalkModel.name :
                realmSpeaker != null ? realmSpeaker.getFirstName() : null;
    }

    private String determineImageUrl() {
        return speakerTalkModel != null ? speakerTalkModel.avatarURL :
                realmSpeaker != null ? realmSpeaker.getAvatarURL() : null;
    }

    private void setupView() {
        textName.setText(getTitleAsString());

        textBio.setText(Html.fromHtml(realmSpeaker.getBioAsHtml()));
        textBio.setMovementMethod(LinkMovementMethod.getInstance());
        if (realmSpeaker.getAcceptedTalks().size() > 0) {
            for (final RealmTalk talkModel : realmSpeaker.getAcceptedTalks()) {
                Button buttonItem = (Button) LayoutInflater.from(getActivity())
                        .inflate(R.layout.button_item, linearLayoutTalks, false);
                buttonItem.setText(talkModel.getTitle());
                buttonItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Optional<SlotApiModel> slotModel = slotsDataManager.
                                getSlotByTalkId(talkModel.getId());
                        if (slotModel.isPresent()) {
                            getMainActivity().replaceFragment(TalkFragment_.builder()
                                    .slotModel(slotModel.get()).build(), true);
                        } else {
                            Toast.makeText(getContext(), "No talk.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                linearLayoutTalks.addView(buttonItem);
            }
        } else {
            textViewTalks.setVisibility(View.GONE);
            linearLayoutTalks.setVisibility(View.GONE);
        }

        Glide.with(this).load(realmSpeaker.getAvatarURL())
                .asBitmap()
                .placeholder(R.drawable.th_background)
                .error(R.drawable.no_photo)
                .fallback(R.drawable.no_photo)
                .centerCrop()
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        final RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(
                                        imageView.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }
}
