package io.scalac.degree.android.fragment;

import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.Settings_;
import io.scalac.degree.data.manager.SpeakersDataManager;
import io.scalac.degree.data.model.RealmSpeaker;
import io.scalac.degree.utils.Logger;
import io.scalac.degree33.R;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@EFragment(R.layout.items_list_view)
public class SpeakersFragment extends BaseFragment {

    @Bean
    SpeakersDataManager speakersDataManager;

    @Bean
    RealmProvider realmProvider;

    @Pref
    Settings_ settings;

    private ListView listView;
    private ItemAdapter itemAdapter;


    @AfterViews
    void afterViews() {
        final Subscriber<Void> subscriber = new Subscriber<Void>() {
            @Override
            public void onCompleted() {
                populateList();
            }

            @Override
            public void onError(Throwable e) {
                // Nothing.
            }

            @Override
            public void onNext(Void aVoid) {
                // Nothing.
            }
        };

        speakersDataManager.fetchSpeakers(settings.activeConferenceCode().get()).
                subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).
                doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(getMainActivity(), "Connection error!", Toast.LENGTH_SHORT).show();
                    }
                }).
                subscribe(subscriber);

        listView = (ListView) getView();
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getMainActivity().replaceFragment(SpeakerFragment_.builder()
                        .speakerDbUuid(itemAdapter.getClickedItem(position).getUuid())
                        .build(), true);
            }
        });
    }

    @Override
    public int getTitle() {
        return R.string.drawer_menu_speakers_label;
    }

    @Override
    public boolean needsToolbarSpinner() {
        return false;
    }

    private void populateList() {
        final Realm realm = realmProvider.getRealm();
        final RealmResults<RealmSpeaker> realmList = realm.allObjects(RealmSpeaker.class);

        final List<RealmSpeaker> finalResult = Stream.of(realmList).
                sortBy(new Function<RealmSpeaker, Comparable>() {
                    @Override
                    public Comparable apply(RealmSpeaker value) {
                        return value.getLastName();
                    }
                }).collect(Collectors.<RealmSpeaker>toList());

        Logger.l("Speakers to show: " + finalResult.size());

        itemAdapter = new ItemAdapter(finalResult);
        listView.setAdapter(itemAdapter);
    }

    class ItemAdapter extends BaseAdapter {

        private List<RealmSpeaker> speakers = new ArrayList<>(0);

        ItemAdapter(List<RealmSpeaker> speakers) {
            this.speakers.addAll(speakers);
        }

        @Override
        public int getCount() {
            return speakers.size();
        }

        @Override
        public RealmSpeaker getItem(int position) {
            return speakers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View viewItem;
            final ViewHolder holder;

            if (convertView == null) {
                viewItem = getActivity().getLayoutInflater().inflate(R.layout.speakers_all_list_item, parent, false);
                holder = new ViewHolder();
                holder.textSpeaker = (TextView) viewItem.findViewById(R.id.textSpeaker);
                holder.textBio = (TextView) viewItem.findViewById(R.id.textBio);
                holder.imageSpeaker = (ImageView) viewItem.findViewById(R.id.imageSpeaker);
                viewItem.setTag(holder);
            } else {
                viewItem = convertView;
                holder = (ViewHolder) viewItem.getTag();
            }

            final RealmSpeaker speakerItem = getItem(position);
            holder.textSpeaker.setText(String.format("%s %s",
                    speakerItem.getFirstName(), speakerItem.getLastName()));
            holder.textBio.setText(speakerItem.getCompany());

            Glide.with(getMainActivity()).load(speakerItem.getAvatarURL())
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.th_background)
                    .error(R.drawable.no_photo)
                    .fallback(R.drawable.no_photo)
                    .into(new BitmapImageViewTarget(holder.imageSpeaker) {
                        @Override
                        public void onResourceReady(
                                Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            final RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(
                                            holder.imageSpeaker.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            holder.imageSpeaker.setImageDrawable(circularBitmapDrawable);
                        }
                    });

            return viewItem;
        }

        public RealmSpeaker getClickedItem(int position) {
            return speakers.get(position);
        }

        private class ViewHolder {
            public TextView textSpeaker;
            public TextView textBio;
            public ImageView imageSpeaker;
        }
    }
}
