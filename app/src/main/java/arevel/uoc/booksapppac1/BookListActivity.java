package arevel.uoc.booksapppac1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.List;

import arevel.uoc.booksapppac1.adapters.BookCoverRecyclerAdapter;
import arevel.uoc.booksapppac1.adapters.RecyclerAdapter;
import arevel.uoc.booksapppac1.custom_views.SpaceDecoration;
import arevel.uoc.booksapppac1.model.BookItem;
import arevel.uoc.booksapppac1.model.BookModel;
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


    // Definimos los bindings de las vistas para ButterKnife

    // Toolbar que sobreescribirá el de Android
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    // Container que permite hacer el gesto de swipe para refrescar la lista
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout mSwipeContainer;

    // Layout que contiene el spinner que indica la carga, usado al crear la actividad
    @BindView(R.id.loadingPanel)
    RelativeLayout m_loadingPanel;

    // Obtenemos el RecyclerView que contiene la lista a mostrar
    @BindView(R.id.book_recyclerview)
    RecyclerView recyclerView;

    @Nullable
    @BindView(R.id.detail_framelayout)
    FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Definimos el layout que usaremos para mostrar la información gestionada
        setContentView(R.layout.book_list);

        // Creamos los bindings definidos para Butterknife
        ButterKnife.bind(this);

        // Definimos la toolbar de la aplicación
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Buscamos el framelayout de detalles. Si existe, estamos en tablet y guardamos esta
        // información para poder usarla en la aplicación
        if (frameLayout != null) {
            dualScreen = true;
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(
                new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        task.getResult();
                    }
                }
        );

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

        // Creamos los adapter que contendrábn la lista (ahora vacía)
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
    }

    @Override
    protected void onNewIntent(Intent intent) {


        // Comprobamos si se ha de realizar alguna acción (actividad abierta desde las acciones de
        // una notificación, o si por el contrario hay que mostrar la actividad sin realizar ninguna
        // acción extra

        if (intent != null && intent.getAction() != null && intent.getExtras() != null) {

            Integer position = intent.getExtras().getInt(Constants.BOOK_ID);

            switch (intent.getAction()) {
                case Constants.ACTION_DELETE:
                    deleteBook(position);
                    break;

                case Constants.ACTION_DETAIL:

                    if (BookListActivity.dualScreen) {
                        FragmentManager supportFM = getSupportFragmentManager();
                        ActivitiesUtils.startDetailsFragment(supportFM, position);
                    } else {

                        Intent i = new Intent(this, BookDetailActivity.class);
                        i.putExtra(Constants.BOOK_ID, position);
                        startActivity(i);
                    }

                    break;
            }
        }
    }

    /**
     * Este método permite realizar la autenticación del usuario contra Firebase. En caso de que esta
     * autenticación sea correcta, intentará recuperar los datos del servidor Firebase.
     */
    public void firebaseAuth() {

        final StringBuilder logSb = new StringBuilder();
        logSb.append(getString(R.string.signInWithEmail_log));

        // Hacemos el login en el proyecto de Firebase
        mAuth.signInWithEmailAndPassword(userLogin, userPassword).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Se ha realizado el login de manera correcta y guardamos le usuario

                            mUser = mAuth.getCurrentUser();

                            logSb.append(getString(R.string.success_log));
                            Log.d("FIREBASE_CONN", logSb.toString());

                            // Se procede a comprobar la conexión y recuperar los datos
                            checkConnectionAndRetrieveData();

                        } else {
                            // Ha habido un error autenticando al usuario
                            logSb.append(getString(R.string.failure_log));
                            Log.w("FIREBASE_CONN", logSb.toString(), task.getException());

                            BookModel.setItemsFromDatabase();
                            recyclerListChanged();
                            Toast.makeText(BookListActivity.this,
                                    getString(R.string.authentication_error), Toast.LENGTH_SHORT)
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
        logSb.append(getString(R.string.checkConnection_log));

        // Realizamos la comprobación
        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean connected = false;

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

                    logSb.append(getString(R.string.success_log));
                    Log.d("FIREBASE_CONN", logSb.toString());

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
                    logSb.append(getString(R.string.failure_log));
                    Log.d("FIREBASE_CONN", logSb.toString());
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

                logSb.append(getString(R.string.failure_log));
                Log.d("FIREBASE_CONN", logSb.toString());
            }
        });

    }

    /**
     * Este método obtiene los libros de la base de datos creada en Firebase.
     */
    public void getBooksFromFirebase() {

        // Creamos las variables necesarias para realizar la llamada y mostrar el log
        final DatabaseReference ref = database.getReference();
        final StringBuilder logSb = new StringBuilder(getString(R.string.dataValueEventListener_log));

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

                logSb.append(getString(R.string.success_log));
                Log.d("FIREBASE_CONN", logSb.toString());

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

                logSb.append(getString(R.string.fireBaseDatabaseError));
                logSb.append(databaseError.getCode());
                Log.e("FIREBASE_CONN", logSb.toString());
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
            SpaceDecoration spaceDecoration = new SpaceDecoration(this.getResources()
                    .getDimensionPixelSize(R.dimen.gridOffSet), spanCount);

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
                Snackbar.make(mSwipeContainer, getString(R.string.noData),
                        Snackbar.LENGTH_LONG).show();
            }

            // Si es pantalla dividida
            if (dualScreen) {
                // Creamos el adapter que gestionará los datos de la lista, pasándole como parámetro el
                // conjunto de datos a mostrar y lo asociamos al RecyclerView
                RecyclerAdapter adapter = (RecyclerAdapter) recyclerView.getAdapter();

                if (adapter != null) {
                    adapter.setItems(bookList);
                    adapter.notifyDataSetChanged();
                }

            } else {
                // Si solamente tenemos la lista en la pantalla
                BookCoverRecyclerAdapter adapter =
                        (BookCoverRecyclerAdapter) recyclerView.getAdapter();

                if (adapter != null) {
                    adapter.setItems(bookList);
                    adapter.notifyDataSetChanged();
                }
            }
        }

        // Si se ha llamado tras el gesto de refrescar el listado, se informa de esta actualización
        // y se para la animación del SwipeContainer.
        if (mSwipeContainer != null && mSwipeContainer.isRefreshing()) {
            mSwipeContainer.setRefreshing(false);

            Snackbar.make(mSwipeContainer, getString(R.string.dataRefreshComplete),
                    Snackbar.LENGTH_LONG).show();

            // En caso de que se esté mostrando el detalle de un libro, se elimina. (El libro puede
            // haber cambiado o haber sido eliminado).
            if (dualScreen) {
                ActivitiesUtils.removeDetailsFragment(getSupportFragmentManager());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Ordenamos la lista según la opción seleccionada
        List<BookItem> sortedList = BookModel.getITEMS();
        ;
//TODO         List<BookItem> sortedList;
        switch (item.getItemId()) {


            case R.id.sortByAuthor_option:
                sortedList = BookModel.sortBy(BookModel.SORT_CRITERIA.AUTHOR);
                break;
            case R.id.sortByTitle_option:
                sortedList = BookModel.sortBy(BookModel.SORT_CRITERIA.TITLE);
                break;
            case R.id.delete0:
                deleteBook(0);
                break;
            case R.id.delete1:
                deleteBook(1);
                break;
            case R.id.delete4:
                deleteBook(4);
                break;
            default:
                sortedList = BookModel.getITEMS();
        }

        // Creamos un nuevo objeto adapter con la lista y notificamos del cambio para que se pueda
        // mostrar la nueva lista.
        if (sortedList != null) {

            recyclerListChanged();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Creamos el menú de la lista
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    private void deleteBook(Integer bookposition) {
        if (bookposition != null && bookposition >= 0) {

            BookModel.deleteBookAtDatabase(bookposition);

            BookModel.setItemsFromDatabase();
            recyclerListChanged();

        }
    }
}
