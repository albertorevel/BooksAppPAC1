package arevel.uoc.booksapppac1;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import arevel.uoc.booksapppac1.adapters.BookCoverRecyclerAdapter;
import arevel.uoc.booksapppac1.adapters.RecyclerAdapter;
import arevel.uoc.booksapppac1.custom_views.SpaceDecoration;
import arevel.uoc.booksapppac1.model.BookItem;
import arevel.uoc.booksapppac1.model.BookModel;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Esta clase define la actividad principal de la aplicación, que gestiona la lista de libros.
 */
public class BookListActivity extends AppCompatActivity {

    // Definimos la lista que contendrá los datos a mostrar
    // private final ArrayList<String> list = new ArrayList<>();

    // Definimos una variable que nos permitirá saber si nos encontramos con pantalla dividida
    public static boolean dualScreen = false;

    // Variables para autenticarse contra el servidor de Firebase
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseUser mUser;

    // Variables que permiten hacer la autenticación. En otra fase las rellenaría el usuario o se
    // recuperarían de las preferencias
    String userLogin = "arevel@uoc.edu";
    String userPassword = "pac2uoc18";

    // Variable que permite repetir un intento de conexión, evitando falsos negativos
    static boolean connectionTry = false;

    // Intent que dejamos almacenado si se piden permisos
    Intent intentToLaunch = null;

    /*
     *  PAC3. EJERCICIO 6. PATRONES DE DISEÑO.

     En la aplicación podemos encontrar distintos patrones de diseño que ya se usaban antes de ser
     pedidos, además del MVC se está usando manera el patrón Singleton (para la instancia de Realm
     o de Firebase).

     Se ha decidido aplicar el patrón de inyección de dependencias ya que permite tener un código más
     limpio y mantenible conforme la aplicación va creciendo. Se ha optado por el uso de la librería
     Butter Knife (pensada para la inyección de vistas principalmente, aunque tiene otros usos como
     la inyección de recursos o la asociación con eventos de clic en algún elemento).
     Se ha usado la librería Butter Knife para inyectar estas dependencias en las Activities,
     Fragments y RecyclerViewHolders.

     La librería Dagger, pese a ser interesante, consideraba que era algo innecesario para esta
     aplicación, al menos para el planteamiento que se le había dado y el alcance que tenía.
     */

    // Definimos los bindings de las vistas para Butter Knife

    // Toolbar que sobreescribirá el de Android
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    //AppBarLayout que contiene el toolbar
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;

    // Container que permite hacer el gesto de swipe para refrescar la lista
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout mSwipeContainer;

    // Layout que contiene el spinner que indica la carga, usado al crear la actividad
    @BindView(R.id.loadingPanel)
    RelativeLayout m_loadingPanel;

    // Obtenemos el RecyclerView que contiene la lista a mostrar
    @BindView(R.id.book_recyclerview)
    RecyclerView recyclerView;

    // Obtenemos el FrameLayout, que será null cuando no se trate de un tablet
    @Nullable
    @BindView(R.id.detail_framelayout)
    FrameLayout frameLayout;

    // Floating Action Button que abrirá formulario de compra
    @Nullable
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.adView)
    AdView m_adView;

    // Hacemos los bind de los diferentes recursos
    // Logs
    @BindString(R.string.success_log)
    String log_success;
    @BindString(R.string.failure_log)
    String log_failure;
    @BindString(R.string.signInWithEmail_log)
    String log_signwithmail;
    @BindString(R.string.checkConnection_log)
    String log_checkConnectiom;
    @BindString(R.string.dataValueEventListener_log)
    String log_dataValueEvent;

    // Errores
    @BindString(R.string.authentication_error)
    String error_authentication;
    @BindString(R.string.noDeleted)
    String error_nodeleted;
    @BindString(R.string.noData)
    String error_noData;
    @BindString(R.string.fireBaseDatabaseError)
    String error_firedatabase;

    // Otros
    @BindString(R.string.dataRefreshComplete)
    String msg_dataRefreshComplete;
    @BindDimen(R.dimen.gridOffSet)
    int grid_offset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Definimos el layout que usaremos para mostrar la información gestionada
        setContentView(R.layout.book_list);

        // Creamos los bindings definidos para Butter Knife
        ButterKnife.bind(this);

        // Definimos la toolbar de la aplicación
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Buscamos el framelayout de detalles. Si existe, estamos en tablet y guardamos esta
        // información para poder usarla en la aplicación
        if (frameLayout != null) {
            dualScreen = true;
        }

        /*
        * ********************
        * *** Ejercicio 1 ****
        * ********************

        // Recuperamos la ListView que contiene la lista de libros
        final ListView listview = findViewById(R.id.book_listview);


        // Creamos la lista que mostraremos en la listview con datos de ejemplo
        for (int i = 0; i < 20; ++i) {
            list.add(getResources().getString(R.string.list_item_example) + " " + i);
        }

        // Creamos el adapter que gestionará el rellenado de la lista y el trato de su contenido,
        // añadiendo la lista creada de ejemplo y asociando este adapter posteriormente al listview
        // recuperado anteriormente.
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        // Definimos un listener para gestionar el click sobre en un elemento de la lista
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                // Con el elemento seleccionado, mostramos el detalle (bien a través de una actividad
                // si tenemos el layout con el master y el detail por separado; bien en el frame
                // layout directamente si se traa de una tablet
                if (BookListActivity.dualScreen) {
                    ActivitiesUtils.startDetailsFragment(getSupportFragmentManager(), position);
                }
                else {
                    Intent i = new Intent(getApplicationContext(), BookDetailActivity.class);
                    i.putExtra("SELECTED_ID", position);
                    startActivity(i);
                }
            }
        });

        */

        // ***** PAC2 ****** //

        // La lista ya no se llenará automáticamente al empezar, ahora es necesario esperar a la
        // respuesta del servidor, obteniendo de ahí la información; en caso de no poder recuperar
        // esta información, se obtendrá la última recibida que se encuentra en la base de datos.
        // defineBookList();

        // Creamos los adapter que contendrán la lista (ahora vacía)
        createAdapters();

        // Inicializamos las clases necesarias para la comunicación con Firebase y realizamos
        // la autenticación
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Mostramos el spinner de carga (disponible en la primera carga)
        if (m_loadingPanel != null) {
            m_loadingPanel.setVisibility(View.VISIBLE);
        }
        firebaseAuth();

        // Definimos el método que se usará al realizar el gesto de actualizar la lista
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkConnectionAndRetrieveData();
            }
        });

        // Añadimos el menú lateral a la aplicación
        ActivitiesUtils.createDrawer(this, toolbar, this.mSwipeContainer);

        // Definimos el FAB que se mostrará en el detalle
        if (fab != null) {
            fab.setVisibility(View.GONE);
            Drawable fabIcon = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_star1)
                    .color(Color.WHITE);
            fab.setImageDrawable(fabIcon);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivitiesUtils.openWeb(getSupportFragmentManager(), appBarLayout);
                }
            });
        }

        // Comprobamos los permisos de la aplicación para pedir los permisos en caso de que no se tengan.
        // Se encuentra en este punto la comprobación ya que la gestión de permisos al seleccionar
        // una opción del menú implicaba unos desarrollos que se ha considerado que se escapaban
        // de esta práctica.

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//
//            return;
//        }

        // Inicializamos AdMob con el ID de prueba de Google
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        AdRequest adRequest = new AdRequest.Builder().build();
        m_adView.loadAd(adRequest);


        // Comprobamos si se ha de realizar alguna acción (actividad abierta desde las acciones de
        // una notificación), o si por el contrario se ha iniciado de manera normal
        Intent mIntent = getIntent();
        if (mIntent != null && mIntent.getAction() != null && mIntent.getExtras() != null) {
            onNewIntent(mIntent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        // Comprobamos si se ha de realizar alguna acción (actividad abierta desde las acciones de
        // una notificación, o si por el contrario hay que mostrar la actividad sin realizar ninguna
        // acción extra

        if (intent != null && intent.getAction() != null && intent.getExtras() != null) {

            // Recuperamos el identificador envíado con la notificación y la acción a realizar.
            Integer bookId = intent.getExtras().getInt(Constants.BOOK_ID);

            switch (intent.getAction()) {

                // En caso de que la acción sea la de borrado, prodecemos al borrado del libro indicado.
                case Constants.ACTION_DELETE:
                    deleteBook(bookId);
                    break;
                // En caso de que se trate de la acción de detalle, mostramos el detalle del libro
                case Constants.ACTION_DETAIL:

                    if (BookListActivity.dualScreen) {
                        FragmentManager supportFM = getSupportFragmentManager();
                        ActivitiesUtils.startDetailsFragment(supportFM, bookId);
                    } else {
                        Intent i = new Intent(this, BookDetailActivity.class);
                        i.putExtra(Constants.BOOK_ID, bookId);
                        startActivity(i);
                    }
                    break;
            }

            // Borramos, si es posible, la notificación ahora que está gestionada
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.cancel(bookId);
            }
        }
    }

    /**
     * Este método permite realizar la autenticación del usuario contra Firebase. En caso de que esta
     * autenticación sea correcta, intentará recuperar los datos del servidor Firebase.
     */
    public void firebaseAuth() {

        final StringBuilder logSb = new StringBuilder();
        logSb.append(log_signwithmail);

        // Hacemos el login en el proyecto de Firebase
        mAuth.signInWithEmailAndPassword(userLogin, userPassword).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Se ha realizado el login de manera correcta y guardamos le usuario

                            mUser = mAuth.getCurrentUser();

                            logSb.append(log_success);
                            Log.d(Constants.LOG_FB_CON, logSb.toString());

                            // Se procede a comprobar la conexión y recuperar los datos
                            checkConnectionAndRetrieveData();

                        } else {
                            // Ha habido un error autenticando al usuario
                            logSb.append(log_failure);
                            Log.w(Constants.LOG_FB_CON, logSb.toString(), task.getException());

                            BookModel.setItemsFromDatabase();
                            recyclerListChanged();
                            Toast.makeText(BookListActivity.this, error_authentication,
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                }
        );
    }

    /**
     * Este método comprueba la conexión con Firebase, y en caso de que esta exista, recupera el
     * listado de libros disponible
     */
    public void checkConnectionAndRetrieveData() {

        final StringBuilder logSb = new StringBuilder();
        logSb.append(log_checkConnectiom);

        // Realizamos la comprobación
        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean connected;

                // Eliminamos el listener. En futuras implementaciones podría fijarse un timeout para
                // evitar que siempre nos devuelva un false esta llamada la primera vez que se realiza
                // (o si se realiza tras un tiempo sin conexiones con Firebase).
                connectedRef.removeEventListener(this);

                // Obtenemos la respuesta del servidor
                try {
                    connected = snapshot.getValue(Boolean.class);
                } catch (NullPointerException npe) {
                    connected = false;
                }

                // Si la respuesta es positiva, significa que hay conexión con Firebase, por lo que
                // llamamos al método que realizará la petición de libros.
                if (connected) {

                    connectionTry = false;

                    logSb.append(log_success);
                    Log.d(Constants.LOG_FB_CON, logSb.toString());

                    // Obtenemos los libros de FireBase
                    getBooksFromFirebase();

                    // Eliminamos el listener ya que la actualización de datos no tiene que ser
                    // en tiempo real
                    connectedRef.removeEventListener(this);
                }

                // La respuesta es negativa, no hay conexión
                else {
                    // Si no es el primer intento, se obtiene la lista de la base de datos.
                    if (connectionTry) {
                        connectionTry = false;
                        BookModel.setItemsFromDatabase();
                        recyclerListChanged();

                        // Eliminamos el listener ya que la actualización de datos no tiene que ser
                        // en tiempo real
                        connectedRef.removeEventListener(this);
                    }
                    // Si es el primer intento, se lanza una nueva petición tras tres segundos (para
                    // evitar falsos negativos)
                    else {
                        connectionTry = true;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                checkConnectionAndRetrieveData();
                            }
                        }, 3000);
                    }
                    logSb.append(log_failure);
                    Log.d(Constants.LOG_FB_CON, logSb.toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // Si hay un problema con la petición, recuperamos los libros de la base de datos
                // por si era la primera vez que se trataba de recuperar la lista (es decir, no era
                // una actualización tras un swipe).
                BookModel.setItemsFromDatabase();
                recyclerListChanged();

                // Eliminamos el listener ya que la actualización de datos no tiene que ser
                // en tiempo real
                connectedRef.removeEventListener(this);

                logSb.append(log_failure);
                Log.d(Constants.LOG_FB_CON, logSb.toString());
            }
        });

    }

    /**
     * Este método obtiene los libros de la base de datos creada en Firebase.
     */
    public void getBooksFromFirebase() {

        // Creamos las variables necesarias para realizar la llamada y mostrar el log
        final DatabaseReference ref = database.getReference();
        final StringBuilder logSb = new StringBuilder(log_dataValueEvent);

        // Creamos el listener que nos permitirá obtener la respuesta.
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Creamos la lista en el modelo de la aplicación al recibir una respuesta.
                BookModel.setItemsFromFireBase(dataSnapshot);
                recyclerListChanged();

                // Eliminamos el listener ya que los datos deben actualizarse tras ciertas interacciones
                // del usuario.
                ref.removeEventListener(this);

                logSb.append(log_success);
                Log.d(Constants.LOG_FB_CON, logSb.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // Si hay un problema con la petición, recuperamos los libros de la base de datos
                // por si era la primera vez que se trataba de recuperar la lista (es decir, no era
                // una actualización tras un swipe).

                BookModel.setItemsFromDatabase();
                recyclerListChanged();

                // Eliminamos el listener ya que la actualización de datos no tiene que ser
                // en tiempo real
                ref.removeEventListener(this);

                logSb.append(error_firedatabase);
                logSb.append(databaseError.getCode());
                Log.e(Constants.LOG_FB_CON, logSb.toString());
            }
        };

        ref.addValueEventListener(dataListener);
    }

    /**
     * Método que crea los adapter para los diferentes tipos de lista
     */
    private void createAdapters() {

        // Si es pantalla dividida (aparece simultáneamente el listado y el detalle), usamos
        // un tipo de listado.
        if (dualScreen) {
            // Creamos el adapter que gestionará los datos de la lista, pasándole como parámetro el
            // conjunto de datos a mostrar y lo asociamos al RecyclerView
            RecyclerAdapter adapter = new RecyclerAdapter(BookModel.getITEMS());
            recyclerView.setAdapter(adapter);

            // Creamos un LinearLayoutManager y lo asociamos al RecyclerView
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);

        } else {
            // Si solamente tenemos la lista en la pantalla, usamos la lista implementada en el
            // ejercicio 6. Creamos el adapter a partir de la lista y se lo asociamos a la lista
            // RecyclerView que contiene la vista.
            BookCoverRecyclerAdapter bookCoverRecyclerAdapter =
                    new BookCoverRecyclerAdapter(BookModel.getITEMS());
            recyclerView.setAdapter(bookCoverRecyclerAdapter);

            // Creamos un StaggeredGridLayout que nos permitirá mostrar varios elementos por lista,
            // de distinto tamaño.
            int spanCount = 2;
            StaggeredGridLayoutManager mStaggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);

            // Definimos el StaggeredLayoutManager en la lista.
            recyclerView.setLayoutManager(mStaggeredGridLayoutManager);

            // El decorator que nos permite definir la separación de elementos solamente debe crearse
            // una única vez
            SpaceDecoration spaceDecoration = new SpaceDecoration(grid_offset, spanCount);

            recyclerView.addItemDecoration(spaceDecoration);

        }
    }

    // Método que es ejecutado para cambiar la lista de libros de un adapter (que será diferente
    // según el tamaño de la pantalla). Este método cambia la lista que usa el adapter y le notifica
    // del cambio. Además, ordena la lista según el criterio aplicado antes de este cambio de listado.
    private void recyclerListChanged() {

        // Si existe el spinner de carga, se oculta y se elimina la referencia, evitando que el programa
        // continue cambiando la visibilidad a "GONE" ya que no volverá a ser visible.
        if (m_loadingPanel != null) {
            m_loadingPanel.setVisibility(View.GONE);
            m_loadingPanel = null;
        }

        if (recyclerView != null) {

            // Llamamos a la ordenación con el parámetro null para que la ordene según el criterio
            // de ordenación que tenía almacenado
            List<BookItem> bookList = BookModel.sortBy(null);

            if (bookList.size() <= 0) {
                Snackbar.make(mSwipeContainer, error_noData,
                        Snackbar.LENGTH_LONG).show();
            }

            // Si es pantalla dividida
            if (dualScreen) {
                // Creamos el adapter que gestionará los datos de la lista, pasándole como parámetro el
                // conjunto de datos a mostrar y lo asociamos al RecyclerView
                RecyclerAdapter adapter = (RecyclerAdapter) recyclerView.getAdapter();

                if (adapter != null) {
                    adapter.updateItems(bookList);
                }

            } else {
                // Si solamente tenemos la lista en la pantalla
                BookCoverRecyclerAdapter adapter =
                        (BookCoverRecyclerAdapter) recyclerView.getAdapter();

                if (adapter != null) {
                    adapter.updateItems(bookList);
                }
            }
        }

        // Si se ha llamado tras el gesto de refrescar el listado, se informa de esta actualización
        // y se para la animación del SwipeContainer.
        if (mSwipeContainer != null && mSwipeContainer.isRefreshing()) {
            mSwipeContainer.setRefreshing(false);

            Snackbar.make(mSwipeContainer, msg_dataRefreshComplete,
                    Snackbar.LENGTH_LONG).show();
        }

        // En caso de que se esté mostrando el detalle de un libro, se elimina. (El libro puede
        // haber cambiado o haber sido eliminado).
        if (dualScreen) {
            ActivitiesUtils.removeDetailsFragment(getSupportFragmentManager());
        }
    }

    /*
    **************************
    ********* PAC 4 **********
    **************************

    Ya no se usa este menú en la PAC4.


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Creamos el menú de la lista
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }


    --> Además, cambiamos el método onOptionsItemSelected para llamarlo desde el nuevo menú.



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ...
    */

    public void sortList(BookModel.SORT_CRITERIA sortCriteria) {

        // Ordenamos la lista según la opción seleccionada
        List<BookItem> sortedList = BookModel.sortBy(sortCriteria);


        recyclerListChanged();

    }

    /**
     * Este método realiza una llamada al modelo de datos para que borre un libro y tras esto,
     * indica al modelo que se deben usar los datos de la base de datos local (para poder ver el
     * cambio reflejado, ya que en la base de datos remota no se borrará el libro).
     *
     * @param bookId id del libro a borrar
     */
    private void deleteBook(Integer bookId) {

        if (bookId != null && bookId >= 0) {

            // Realizamos la llamada que permitirá borrar el libro de la base de datoss.
            boolean deleted = BookModel.deleteBookAtDatabase(bookId);

            // Si el borrado ha podido realizarse de manera correcta, hacemos que los cambios queden
            // reflejados en la lista. En caso contrario, informamos al usuario del error.
            if (deleted) {
                BookModel.setItemsFromDatabase();
                recyclerListChanged();
            } else {
                Snackbar.make(mSwipeContainer, error_nodeleted,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    // TODO comment
    public void launchIntentAndCheckPermission(Intent intent) {

        intentToLaunch = intent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.WRITE_EXTERNAL_STORAGE);
        } else {
            this.startActivity(intentToLaunch);
        }
    }

    // TODO comment
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case Constants.WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && intentToLaunch != null) {

                    this.startActivity(intentToLaunch);

                } else {
                    // Informamos al usuario de que no se puede realizar la acción por falta de permisos y
                    // salimos del método
                    Snackbar.make(this.mSwipeContainer,
                            getResources().getString(R.string.share_no_permission),
                            Snackbar.LENGTH_LONG).show();
                }
            }

        }
    }


}
