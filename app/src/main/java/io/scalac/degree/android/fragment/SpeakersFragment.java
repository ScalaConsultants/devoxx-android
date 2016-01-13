package io.scalac.degree.android.fragment;

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
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.Settings_;
import io.scalac.degree.data.manager.SpeakersDataManager;
import io.scalac.degree.data.model.RealmSpeakerShort;
import io.scalac.degree.utils.InfoUtil;
import io.scalac.degree33.R;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@EFragment(R.layout.items_list_view)
public class SpeakersFragment extends BaseFragment {

    private static final long SHOW_PROGRESS_DELAY_MS = 150;

    @Bean
    SpeakersDataManager speakersDataManager;

    @Bean
    RealmProvider realmProvider;

    @Bean
    InfoUtil infoUtil;

    @Pref
    Settings_ settings;

    @ViewById(R.id.listProgressBar)
    View progressBar;

    @ViewById(R.id.listView1)
    ListView listView;

    private ItemAdapter itemAdapter;

    @AfterInject
    void afterInject() {
        itemAdapter = new ItemAdapter();
    }

    @AfterViews
    void afterViews() {
        listView.setAdapter(itemAdapter);

        final Subscriber<Void> subscriber = new Subscriber<Void>() {
            @Override
            public void onCompleted() {
                populateList();
                hideProgress();
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

        speakersDataManager.fetchSpeakersShortInfo(settings.activeConferenceCode().get())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showProgress();
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        infoUtil.showToast(R.string.connection_error);
                    }
                })
                .subscribe(subscriber);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getMainActivity().replaceFragment(SpeakerFragment_.builder()
                        .speakerDbUuid(itemAdapter.getClickedItem(position).getUuid())
                        .build(), true);
            }
        });
    }

    private void populateList() {
        final List<RealmSpeakerShort> speakers =
                speakersDataManager.getAllShortSpeakers();

        final List<SpeakersGroup> list = Stream.of(speakers)
                .groupBy(new Function<RealmSpeakerShort, String>() {
                    @Override
                    public String apply(RealmSpeakerShort value) {
                        return value.getFirstName().substring(0, 1);
                    }
                })
                .map(new Function<Map.Entry<String, List<RealmSpeakerShort>>, SpeakersGroup>() {
                    @Override
                    public SpeakersGroup apply(Map.Entry<String, List<RealmSpeakerShort>> value) {
                        final List<RealmSpeakerShort> list =
                                Stream.of(value.getValue())
                                        .sortBy(new Function<RealmSpeakerShort, Comparable>() {
                                            @Override
                                            public Comparable apply(RealmSpeakerShort value) {
                                                return value.getFirstName();
                                            }
                                        })
                                        .collect(Collectors.<RealmSpeakerShort>toList());
                        return new SpeakersGroup(value.getKey(), list);
                    }
                })
                .sortBy(new Function<SpeakersGroup, Comparable>() {
                    @Override
                    public Comparable apply(SpeakersGroup value) {
                        return value.getGroupLetter();
                    }
                })
                .collect(Collectors.<SpeakersGroup>toList());

        itemAdapter.setSpeakers(list);
        itemAdapter.notifyDataSetChanged();
    }

    private Runnable showProgressRunnable = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.VISIBLE);
        }
    };

    private void showProgress() {
        progressBar.postDelayed(showProgressRunnable, SHOW_PROGRESS_DELAY_MS);
    }

    private void hideProgress() {
        progressBar.removeCallbacks(showProgressRunnable);
    }

    class ItemAdapter extends BaseAdapter {

        private List<SpeakersGroup> speakers = new ArrayList<>(0);
        private int size;

        public void setSpeakers(List<SpeakersGroup> speakers) {
            this.speakers.addAll(speakers);
            for (SpeakersGroup speaker : speakers) {
                final int groupSize = speaker.speaakersSize();
                speaker.setStartIndex(size);
                size += groupSize;
                speaker.setStopIndex(size - 1);
            }
        }

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public Object getItem(int position) {
            // Not used at all. See getItemGroup();
            return null;
        }

        @NonNull
        private SpeakersGroup getItemGroup(int position) {
            for (SpeakersGroup speaker : speakers) {
                final int startIndex = speaker.getStartIndex();
                final int stopIndex = speaker.getStopIndex();
                if (position >= startIndex && position <= stopIndex) {
                    return speaker;
                }
            }

            throw new IllegalStateException("Should not be here!");
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
                viewItem = getActivity().getLayoutInflater()
                        .inflate(R.layout.speakers_all_list_item, parent, false);
                holder = new ViewHolder();
                holder.textSpeaker = (TextView) viewItem.findViewById(R.id.textSpeaker);
                holder.textLetter = (TextView) viewItem.findViewById(R.id.textSpeakersGroupFirstLetter);
                holder.imageSpeaker = (ImageView) viewItem.findViewById(R.id.imageSpeaker);
                holder.divider = viewItem.findViewById(R.id.speakerItemDivider);
                viewItem.setTag(holder);
            } else {
                viewItem = convertView;
                holder = (ViewHolder) viewItem.getTag();
            }

            final SpeakersGroup group = getItemGroup(position);
            final RealmSpeakerShort speakerItem = group.getSpeakerByGlobalPosition(position);
            holder.textSpeaker.setText(String.format("%s %s",
                    speakerItem.getFirstName(), speakerItem.getLastName()));

            final boolean shouldLetterBeVisible = position == group.getStartIndex();
            holder.textLetter.setVisibility(shouldLetterBeVisible ? View.VISIBLE : View.INVISIBLE);
            holder.textLetter.setText(group.getGroupLetter());

            final boolean shouldDividerBeVisible = group.getStopIndex() == position;
            holder.divider.setVisibility(shouldDividerBeVisible ? View.VISIBLE : View.GONE);

            Glide.with(getMainActivity())
                    .load(speakerItem.getAvatarURL())
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

        public RealmSpeakerShort getClickedItem(int position) {
            return getItemGroup(position).getSpeakerByGlobalPosition(position);
        }

        private class ViewHolder {
            public TextView textSpeaker;
            public TextView textLetter;
            public ImageView imageSpeaker;
            public View divider;
        }
    }

    public static class SpeakersGroup {
        private String groupLetter;
        private List<RealmSpeakerShort> speakers;

        private int startIndex, stopIndex;

        public SpeakersGroup(String groupLetter, List<RealmSpeakerShort> speakers) {
            this.groupLetter = groupLetter;
            this.speakers = speakers;
        }

        public String getGroupLetter() {
            return groupLetter;
        }

        public int speaakersSize() {
            return speakers != null ? speakers.size() : 0;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public int getStopIndex() {
            return stopIndex;
        }

        public void setStopIndex(int stopIndex) {
            this.stopIndex = stopIndex;
        }

        @NonNull
        public RealmSpeakerShort getSpeakerByGlobalPosition(int globalPosition) {
            return speakers.get(globalPosition - startIndex);
        }
    }
}
