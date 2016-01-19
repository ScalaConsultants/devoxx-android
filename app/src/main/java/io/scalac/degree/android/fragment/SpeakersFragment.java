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
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.app.SearchManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

    @ViewById(R.id.listView)
    ListView listView;

    @ViewById(R.id.listViewContainer)
    View container;

    @SystemService
    SearchManager searchManager;

    @SystemService
    InputMethodManager inputMethodManager;

    private ItemAdapter itemAdapter;

    private Runnable showProgressRunnable = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.VISIBLE);
        }
    };

    @AfterInject
    void afterInject() {
        itemAdapter = new ItemAdapter();
    }

    @AfterViews
    void afterViews() {
        setHasOptionsMenu(true);

        listView.setAdapter(itemAdapter);

        final Subscriber<Void> subscriber = new Subscriber<Void>() {
            @Override
            public void onCompleted() {
                final List<RealmSpeakerShort> speakers =
                        speakersDataManager.getAllShortSpeakers();
                populateList(speakers);
                hideProgress();
            }

            @Override
            public void onError(Throwable e) {
                hideProgress();
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

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    inputMethodManager.hideSoftInputFromWindow(listView.getWindowToken(), 0);
                }
                return false;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.speakers_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    onSearchQuery(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    onSearchQuery(s);
                    return false;
                }
            });
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void onSearchQuery(String query) {
        final List<RealmSpeakerShort> speakers = speakersDataManager.
                getAllShortSpeakersWithFilter(query);
        populateList(speakers);
    }

    private void populateList(List<RealmSpeakerShort> speakers) {
        final List<SpeakersGroup> list = Stream.of(speakers)
                .groupBy(speakerFirstLetterGroupping())
                .map(speakersGroupMapper())
                .sortBy(speakersGroupSorter())
                .collect(Collectors.<SpeakersGroup>toList());

        itemAdapter.setSpeakers(list);
        itemAdapter.notifyDataSetChanged();
    }

    private void showProgress() {
        progressBar.postDelayed(showProgressRunnable, SHOW_PROGRESS_DELAY_MS);
    }

    private void hideProgress() {
        progressBar.removeCallbacks(showProgressRunnable);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private static Function<SpeakersGroup, Comparable> speakersGroupSorter() {
        return new Function<SpeakersGroup, Comparable>() {
            @Override
            public Comparable apply(SpeakersGroup value) {
                return value.getGroupLetter();
            }
        };
    }

    private static Function<Map.Entry<String, List<RealmSpeakerShort>>, SpeakersGroup> speakersGroupMapper() {
        return new Function<Map.Entry<String, List<RealmSpeakerShort>>, SpeakersGroup>() {
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
        };
    }

    private static Function<RealmSpeakerShort, String> speakerFirstLetterGroupping() {
        return new Function<RealmSpeakerShort, String>() {
            @Override
            public String apply(RealmSpeakerShort value) {
                return value.getFirstName().substring(0, 1);
            }
        };
    }

    class ItemAdapter extends BaseAdapter {

        private List<SpeakersGroup> speakers;
        private int size;

        public void setSpeakers(List<SpeakersGroup> speakers) {
            this.speakers = new ArrayList<>(speakers.size());
            this.speakers.addAll(speakers);
            rebuildIndexes();
        }

        private void rebuildIndexes() {
            size = 0;
            for (SpeakersGroup speaker : speakers) {
                final int groupSize = speaker.speakersSize();
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

            setupView(position, holder);

            return viewItem;
        }

        private void setupView(int position, final ViewHolder holder) {
            final SpeakersGroup group = getItemGroup(position);
            final RealmSpeakerShort speakerItem = group.getSpeakerByGlobalPosition(position);
            holder.textSpeaker.setText(String.format("%s %s",
                    speakerItem.getFirstName(), speakerItem.getLastName()));

            final boolean shouldLetterBeVisible = position == group.getStartIndex();
            holder.textLetter.setVisibility(shouldLetterBeVisible ? View.VISIBLE : View.INVISIBLE);
            holder.textLetter.setText(group.getGroupLetter());

            final boolean shouldDividerBeVisible = group.getStopIndex() == position;
            holder.divider.setVisibility(shouldDividerBeVisible ? View.VISIBLE : View.GONE);

            setupImage(holder, speakerItem);
        }

        private void setupImage(final ViewHolder holder, RealmSpeakerShort speakerItem) {
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

    private static class SpeakersGroup {
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

        public int speakersSize() {
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
