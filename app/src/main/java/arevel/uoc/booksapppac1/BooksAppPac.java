package arevel.uoc.booksapppac1;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;

import com.facebook.stetho.Stetho;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;

/**
 * Clase que extiende de Application, creada para inicializar Stetho y Realm.
 */
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

        // Creamos e inicializamos el objeto DrawerImageLoader que nos permitirá cargar imágenes en el
        // menú lateral con una Uri dada mediante la librería Picasso.
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }

        });

        createNotificationChannel();
    }

    private void createNotificationChannel() {

        // Creamos los canales de notificaciones para las aplicaciones que se ejecuten en dispositivos
        // cuya API sea  mayor igual que 26. Creamos dos canales para los dos tipos de notificaciones.
        // Debemos definir aquí las propiedades de las notificaciones de los canales cuando se envíen
        // a través de estos. En un futuro refactor debería unificarse con la versión pre API 26 para
        // evitar la uplicación de código.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            // Definimos el primer canal.
            NotificationChannel channel0 = new NotificationChannel(Constants.CHANNEL_ID_0,
                    name + " 0", importance);
            String baseSoundUri = "android.resource://" + getPackageName() + "/";

            channel0.setDescription(description + " 0");
            channel0.setLightColor(getColor(R.color.led1));
            channel0.setSound(Uri.parse(baseSoundUri + R.raw.definite), null);

            // Definimos el segundo canal.
            NotificationChannel channel1 = new NotificationChannel(Constants.CHANNEL_ID_1,
                    name + " 1", importance);
            channel1.setDescription(description + " 1");
            channel1.setLightColor(getColor(R.color.led2));
            channel0.setSound(Uri.parse(baseSoundUri + R.raw.appointed), null);

            // Registramos los canales en el sistema.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel0);
                notificationManager.createNotificationChannel(channel1);
            }
        }
    }
}
