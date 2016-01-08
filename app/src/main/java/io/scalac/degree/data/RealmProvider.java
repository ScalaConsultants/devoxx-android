package io.scalac.degree.data;

import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import io.realm.Realm;
import io.realm.RealmConfiguration;

@EBean(scope = EBean.Scope.Singleton)
public class RealmProvider {

    @RootContext
    Context context;

    public Realm getRealm() {
        final RealmConfiguration configuration =
                new RealmConfiguration.Builder(context)
                        .name("devoxx_db")
                        .schemaVersion(1)
                        .deleteRealmIfMigrationNeeded()
                        .build();
        return Realm.getInstance(configuration);
    }
}
