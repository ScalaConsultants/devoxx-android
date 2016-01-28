package com.devoxx.data.dao;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.devoxx.data.RealmProvider;
import com.devoxx.data.model.RealmSlotsAggregate;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import com.devoxx.connection.model.SlotApiModel;

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
        realm.close();
    }

    public List<SlotApiModel> getAllSlots() {
        final Realm realm = realmProvider.getRealm();
        final RealmSlotsAggregate aggModel = realm
                .where(RealmSlotsAggregate.class).findFirst();
        final String rawData = aggModel != null ? aggModel.getRawData() : "";
        realm.close();

        final List<SlotApiModel> result = new ArrayList<>();
        if (!TextUtils.isEmpty(rawData)) {
            final Type listType = new TypeToken<List<SlotApiModel>>() {
            }.getType();
            final List<SlotApiModel> list = gson.fromJson(rawData, listType);
            result.addAll(list);
        }

        return result;
    }

    public void clearData() {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        realm.allObjects(RealmSlotsAggregate.class).clear();
        realm.commitTransaction();
        realm.close();
    }
}
