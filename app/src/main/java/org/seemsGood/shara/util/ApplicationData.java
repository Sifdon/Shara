package org.seemsGood.shara.util;

import com.google.firebase.FirebaseApp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ApplicationData extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
//        Realm.init(this);
//        RealmConfiguration config = new RealmConfiguration.Builder()
//                .name("sharaData.realm")
//                .schemaVersion(0)
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        Realm.setDefaultConfiguration(config);
    }

}
