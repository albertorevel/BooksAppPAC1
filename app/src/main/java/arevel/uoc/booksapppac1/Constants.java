package arevel.uoc.booksapppac1;

public class Constants {

    // Tags usados por el modelo
    public static final String BOOK_ID = "BOOK_ID";
    // Tags usados en el log de la aplicación
    static final String LOG_FB_CON = "FIREBASE_CONN";
    static final String LOG_FB_CM = "FIREBASE_MESSAGING";
    // Tag usado para gestionar fragments
    static final String DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT_TAG";

    // Acciones de las notificaciones
    static final String ACTION_DELETE = "ACTION_DELETE";
    static final String ACTION_DETAIL = "ACTION_DETAIL";

    // Tag usado por las notificaciones
    static final String FIREBASE_BOOK_POSITION = "book_position";

    // Canales de notificaciones
    static final String CHANNEL_ID_0 = "BooksPacApp_Channel_0";
    static final String CHANNEL_ID_1 = "BooksPacApp_Channel_1";

    // Permission request
    static final int WRITE_EXTERNAL_STORAGE = 1;

    // Tags usados en las opciones del menú lateral
    enum DRAWER_ACTION {
        SHARE_OTHERAPPS,
        COPY_CLIPBOARD,
        SHARE_WHATSAPP,
        SORT_AUTHOR,
        SORT_TITLE
    }

    //
    // Url de las imágenes que aparecen ahora por defecto en el MenuDrawer. Así como de la página de
    // compra de libros. Se colocan aquí ya que esas partes no están desarrolladas al 100% y estos
    // datos son fijos por lo tanto.
    static final String DEFAULT_HEADER_URL = "https://images.unsplash.com/photo-1491841550275-ad7854e35ca6?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60\n";
    static final String DEFAULT_PROFILE_ICON_URL = "https://c.pxhere.com/images/c6/91/441b53afa0a69dc4acfbb01dd205-1447095.jpg!d";
    static final String URL_BOOKS_SHOP = "file:///android_asset/form.html";
}
