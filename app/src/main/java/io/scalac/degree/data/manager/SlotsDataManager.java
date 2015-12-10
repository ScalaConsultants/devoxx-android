package io.scalac.degree.data.manager;

import android.support.annotation.Nullable;

import com.annimon.stream.Collector;
import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.BiConsumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.annimon.stream.function.Supplier;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.dao.SlotDao;
import io.scalac.degree.data.downloader.SlotsDownloader;
import io.scalac.degree.utils.Logger;
import io.scalac.degree.utils.SingleTuple;
import io.scalac.degree.utils.Tuple;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 28/10/2015
 */
@EBean(scope = EBean.Scope.Singleton)
public class SlotsDataManager extends AbstractDataManager<SlotApiModel> {

    @Bean
    SlotsDownloader slotsDownloader;
    @Bean
    SlotDao slotDao;

    private List<SlotApiModel> allSlots = new ArrayList<>();
    private List<SlotApiModel> talks = new ArrayList<>();

    @AfterInject
    void afterInject() {
        allSlots.clear();
        allSlots.addAll(slotDao.getAllSlots());

        talks.clear();
        talks.addAll(Stream.of(allSlots)
                .filter(new Predicate<SlotApiModel>() {
                    @Override
                    public boolean test(SlotApiModel value) {
                        return value.isTalk() && !value.isBreak();
                    }
                })
                .collect(Collectors.<SlotApiModel>toList()));
    }

    public Optional<SlotApiModel> getSlotByTalkId(final String talkId) {
        return Stream.of(allSlots).filter(new Predicate<SlotApiModel>() {
            @Override
            public boolean test(SlotApiModel value) {
                return value.isTalk() && !value.isBreak()
                        && value.talk.id.equals(talkId);
            }
        }).findFirst();
    }

    public List<SlotApiModel> getLastTalks() {
        return talks;
    }

    public int getInitialDatePosition() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        long currentDateMS = cal.getTimeInMillis();

        final List<Date> dates = createDateList();
        for (int i = 0; i < dates.size(); i++) {
            if (dates.get(i).getTime() == currentDateMS)
                return i;
        }
        return 0;
    }

    public List<Date> createDateList() {
        return Stream.of(allSlots).collect(
                new Collector<SlotApiModel, HashSet<Date>, List<Date>>() {
                    final HashSet<Date> set = new HashSet<>();
                    final Calendar cal = Calendar.getInstance();

                    @Override
                    public Supplier<HashSet<Date>> supplier() {
                        return new Supplier<HashSet<Date>>() {
                            @Override
                            public HashSet<Date> get() {
                                return set;
                            }
                        };
                    }

                    @Override
                    public BiConsumer<HashSet<Date>, SlotApiModel> accumulator() {
                        return new BiConsumer<HashSet<Date>, SlotApiModel>() {
                            @Override
                            public void accept(HashSet<Date> longs, SlotApiModel slotApiModel) {
                                cal.setTimeInMillis(slotApiModel.fromTimeMillis);
                                cal.setTimeZone(TimeZone.getTimeZone("UTC"));
                                cal.set(Calendar.MILLISECOND, 0);
                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MINUTE, 0);
                                cal.set(Calendar.HOUR, 0);
                                cal.set(Calendar.HOUR_OF_DAY, 0);
                                longs.add(new Date(cal.getTimeInMillis()));
                            }
                        };
                    }

                    @Override
                    public Function<HashSet<Date>, List<Date>> finisher() {
                        return new Function<HashSet<Date>, List<Date>>() {
                            @Override
                            public List<Date> apply(HashSet<Date> value) {
                                return Stream.of(new ArrayList<>(value))
                                        .sortBy(new Function<Date, Comparable>() {
                                            @Override
                                            public Comparable apply(Date value) {
                                                return value.getTime();
                                            }
                                        })
                                        .collect(Collectors.<Date>toList());
                            }
                        };
                    }
                });
    }

    @Background
    public void fetchTalks(
            final String confCode,
            @Nullable IDataManagerListener<SlotApiModel> dataListener) {
        try {
            notifyAboutStart(dataListener);

            allSlots.clear();
            allSlots = slotsDownloader.downloadTalks(confCode);
            slotDao.saveSlots(allSlots);

            final List<SlotApiModel> talks = Stream.of(allSlots)
                    .filter(new Predicate<SlotApiModel>() {
                        @Override
                        public boolean test(SlotApiModel value) {
                            return value.talk != null && !value.isBreak();
                        }
                    })
                    .collect(Collectors.<SlotApiModel>toList());
            this.talks.clear();
            this.talks.addAll(talks);

            notifyAboutSuccess(dataListener, this.talks);
        } catch (IOException e) {
            Logger.exc(e);
            notifyAboutFailed(dataListener);
        }
    }

    public List<SlotApiModel> extractTimeLabelsForDate(final Date requestedDate) {
        return Stream.of(allSlots)
                .filter(new SameDayPredicate(requestedDate.getTime()))
                .map(new Function<SlotApiModel, Tuple<String, String, SlotApiModel>>() {
                    @Override
                    public Tuple<String, String, SlotApiModel> apply(SlotApiModel value) {
                        return new Tuple<>(value.fromTime, value.toTime, value);
                    }
                })
                .distinct()
                .map(new Function<Tuple<String, String, SlotApiModel>, SlotApiModel>() {
                    @Override
                    public SlotApiModel apply(Tuple<String, String, SlotApiModel> value) {
                        return value.object;
                    }
                })
                .sortBy(new Function<SlotApiModel, Comparable>() {
                    @Override
                    public Comparable apply(SlotApiModel value) {
                        return value.fromTimeMillis;
                    }
                })
                .collect(Collectors.<SlotApiModel>toList());
    }

    public List<SlotApiModel> getBreaksListBySlot(final SlotApiModel slotApiModel) {
        return Stream.of(allSlots).filter(new Predicate<SlotApiModel>() {
            @Override
            public boolean test(SlotApiModel value) {
                return value.isBreak() && value.slotId.equals(slotApiModel.slotId);
            }
        }).collect(Collectors.<SlotApiModel>toList());
    }

    public List<SlotApiModel> getTalksForSpecificTime(final long requestedDate) {
        return Stream.of(talks).filter(new Predicate<SlotApiModel>() {
            @Override
            public boolean test(SlotApiModel value) {
                return value.isTalk() && requestedDate == value.fromTimeMillis;
            }
        }).collect(Collectors.<SlotApiModel>toList());
    }

    public List<SlotApiModel> extractRoomLabelsForDate(Date requestedDate) {
        return Stream.of(allSlots)
                .filter(new SameDayPredicate(requestedDate.getTime()))
                .map(new Function<SlotApiModel, SingleTuple<String, SlotApiModel>>() {
                    @Override
                    public SingleTuple<String, SlotApiModel> apply(SlotApiModel value) {
                        return new SingleTuple<>(value.roomId, value);
                    }
                })
                .distinct()
                .map(new Function<SingleTuple<String, SlotApiModel>, SlotApiModel>() {
                    @Override
                    public SlotApiModel apply(SingleTuple<String, SlotApiModel> value) {
                        return value.object;
                    }
                })
                .sorted(new Comparator<SlotApiModel>() {
                    final Collator collator = Collator.getInstance(Locale.getDefault());

                    @Override
                    public int compare(SlotApiModel lhs, SlotApiModel rhs) {
                        return collator.compare(lhs.roomName, rhs.roomName);
                    }
                })
                .collect(Collectors.<SlotApiModel>toList());
    }

    public List<SlotApiModel> getTalksForSpecificTimeAndRoom(final String roomID, long dateMs) {
        final DateTime rqDt = new DateTime(dateMs);
        final DateTime dT = new DateTime();
        final DateTimeComparator dtCmp = DateTimeComparator.getDateOnlyInstance();
        return Stream.of(allSlots)
                .filter(new Predicate<SlotApiModel>() {
                    @Override
                    public boolean test(SlotApiModel value) {
                        final boolean correctDate =
                                dtCmp.compare(
                                        rqDt, dT.withMillis(
                                                value.fromTimeMillis)) == 0;
                        final boolean correctRoom = value.roomId.equals(roomID);
                        return correctDate && correctRoom && value.isTalk();
                    }
                })
                .collect(Collectors.<SlotApiModel>toList());
    }

    class SameDayPredicate implements Predicate<SlotApiModel> {

        private final DateTime inputDate;

        private final DateTime tmp = new DateTime();
        private final DateTimeComparator dTCmp =
                DateTimeComparator.getDateOnlyInstance();

        SameDayPredicate(long time) {
            this.inputDate = new DateTime(time);
        }

        @Override
        public boolean test(SlotApiModel value) {
            return dTCmp.compare(inputDate,
                    tmp.withMillis(value.fromTimeMillis)) == 0;
        }
    }
}
