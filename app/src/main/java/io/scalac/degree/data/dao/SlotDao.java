package io.scalac.degree.data.dao;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.model.RealmSlotsAggregate;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 05/11/2015
 */
@EBean
public class SlotDao {

    @RootContext
    Context context;
    @Bean
    RealmProvider realmProvider;

    private Gson gson = new Gson();

    public void saveSlots(List<SlotApiModel> slots) {
        final Realm realm = realmProvider.getRealm();
        final RealmSlotsAggregate aggModel = new RealmSlotsAggregate();
        final Type listType = new TypeToken<List<SlotApiModel>>() {
        }.getType();
        aggModel.setRawData(gson.toJson(slots, listType));

        realm.beginTransaction();
        realm.allObjects(RealmSlotsAggregate.class).clear();
        realm.copyToRealm(aggModel);
        realm.commitTransaction();
    }

    public List<SlotApiModel> getAllSlots() {
        final Realm realm = realmProvider.getRealm();
        final RealmSlotsAggregate aggModel = realm
                .where(RealmSlotsAggregate.class).findFirst();
        final String rawData = aggModel != null ? aggModel.getRawData() : "";

        final List<SlotApiModel> result = new ArrayList<>();
        if (!TextUtils.isEmpty(rawData)) {
            final Type listType = new TypeToken<List<SlotApiModel>>() {
            }.getType();
            final List<SlotApiModel> list = gson.fromJson(rawData, listType);
            result.addAll(list);
        }

        return result;
    }
}
