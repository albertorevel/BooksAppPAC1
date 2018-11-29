package arevel.uoc.booksapppac1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Servicio que gestiona los mensajes remotos enviados desde Firebase a la aplicación
 */
public class BooksAppFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseNotification";

    /**
     * Método llamado cuando se recibe un mensaje remoto
     *
     * @param remoteMessage Mensaje recibido de Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Mostrar una notificación al recibir un mensaje de Firebase
        Map<String, String> map = remoteMessage.getData();
        if (map != null && map.size() > 0) {
            sendNotification(remoteMessage.getData().get(Constants.FIREBASE_BOOK_POSITION));
        }
    }

    /**
     * Crea y muestra una notificación al recibir un mensaje de Firebase
     *
     * @param bookPosition Datos recibidos de FireBase
     */
    public void sendNotification(String bookPosition) {

        // Definimos los intent que gestionarán las interacciones del usuario con la notificación
        // Intent que lleva a la Activity principal al pulsar sobre la notificación
//        Intent intent = new Intent(this, BookListActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntentMain = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);

        int bookId = 0;
        if (bookPosition != null) {
            bookId = Integer.parseInt(bookPosition);
        }

        // Intent para el borrado de un libro
        Intent intent = new Intent(this, BookListActivity.class);
        intent.setAction(Constants.ACTION_DELETE);
        intent.putExtra(Constants.BOOK_ID, bookId);
        PendingIntent pendingIntentDelete = PendingIntent.getActivity(this,
                (int) System.currentTimeMillis(), intent, 0);

        // Intent para ver el detalle de un libro
        Intent intent2 = new Intent(this, BookListActivity.class);
        intent2.setAction(Constants.ACTION_DETAIL);
        intent2.putExtra(Constants.BOOK_ID, bookId);
        PendingIntent pendingIntentDetail = PendingIntent.getActivity(this,
                (int) System.currentTimeMillis(), intent2, 0);

        // Dependiendo de la posición, variarán una serie de elementos de la notificación.
        // En las notificaciones de libros con posición par, se mostrará un led azul y un sonido;
        // en las que se refieran a una posición impar, un led rojo y un sonido diferente.

        int color = 0;
        int sound = 0;

        String baseSoundUri = "android.resource://" + getPackageName() + "/";


        if (bookId % 2 == 0) {
            color = getResources().getColor(R.color.led1);
            sound = R.raw.definite;
        } else {
            color = getResources().getColor(R.color.led2);
            sound = R.raw.appointed;
        }
        Uri notificationSound = Uri.parse(baseSoundUri + sound);

        // Creamos la notificación
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_book)
                .setContentTitle("Ejemplo Firebase" + System.currentTimeMillis())
                .setContentText("Ejemplo ejemploizante ejemplizador")
                .setLights(color, 1000, 500)
                .setSound(notificationSound)
                .setVibrate(new long[]{1000, 1000})
                .setLights(0xFFFF00, 300, 100)
                .addAction(R.mipmap.ic_launcher, getString(R.string.delete), pendingIntentDelete)
                .addAction(R.mipmap.ic_launcher, getString(R.string.detail), pendingIntentDetail)
                .setAutoCancel(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        }

        // Mostramos la notificación
        Notification notification = notificationBuilder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0, notification);
        }
    }
}