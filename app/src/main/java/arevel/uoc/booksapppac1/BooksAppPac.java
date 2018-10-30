package arevel.uoc.booksapppac1;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;

public class BooksAppPac extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicialización de Realm
        Realm.init(this);

        // Inicialización de Stetho
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }
}
