package arevel.uoc.booksapppac1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Servicio que gestiona los mensajes remotos enviados desde Firebase a la aplicación
 */
public class BooksAppFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Método llamado cuando se recibe un mensaje remoto
     *
     * @param remoteMessage Mensaje recibido de Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        // Se ha elegido el id del libro en vez de la posición para tratar la notificación porque es
        // el dato que se utilizaba ya para gestionar el modelo de la aplicación. No obstante se ha
        // mantenido que la clave del dato enviado desde el servidor fuera "book_position".

        // Si se reciben datos con el mensaje de firebase, buscamos el id y llamamos al método
        // que creará la notificación.
        Map<String, String> map = remoteMessage.getData();
        if (map != null && map.size() > 0) {

            try {
                // Buscamos el valor "book_position", aunque tratemos ids.
                int bookId = Integer.parseInt(remoteMessage.getData().get(Constants.FIREBASE_BOOK_POSITION));
                sendNotification(bookId);
            } catch (NumberFormatException nfe) {
                StringBuilder sb = new StringBuilder(getString(R.string.fireBaseMessageError));
                sb.append(remoteMessage.getData().get(Constants.FIREBASE_BOOK_POSITION));
                Log.e(Constants.LOG_FB_CM, sb.toString());
            }

        }
    }

    /**
     * Crea y muestra una notificación al recibir un mensaje de Firebase
     *
     * @param bookId Id del libro recibidos de FireBase
     */
    public void sendNotification(int bookId) {

        // Definimos los intent que gestionarán las interacciones del usuario con la notificación

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

        // Dependiendo del id, variarán una serie de elementos de la notificación.
        // En las notificaciones de libros con posición par, se mostrará un led azul y un sonido;
        // en las que se refieran a una posición impar, un led rojo y un sonido diferente.
        // A partir de Oreo, deben usarse dos canales de notificación distintos para que funcione.
        int color;
        int sound;
        String channelID;

        String baseSoundUri = "android.resource://" + getPackageName() + "/";


        if (bookId % 2 == 0) {
            color = getResources().getColor(R.color.led1);
            sound = R.raw.definite;
            channelID = Constants.CHANNEL_ID_0;
        } else {
            color = getResources().getColor(R.color.led2);
            sound = R.raw.appointed;
            channelID = Constants.CHANNEL_ID_1;
        }

        Uri notificationSound = Uri.parse(baseSoundUri + sound);

        // Creamos la notificación
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.drawable.ic_book)
                .setContentTitle(getString(R.string.notificationTitle))
                .setContentText(getString(R.string.notificationBody) + " " + bookId)
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

        // Construimos la notificación y buscamos el Notification Manager
        Notification notification = notificationBuilder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {

            // Lanzamos la notificación creada, con un Id igual al recibido. Este id permitirá
            // gestionar la notificación más adelante y mostrar más de una.
            notificationManager.notify(bookId, notification);
        }
    }
}