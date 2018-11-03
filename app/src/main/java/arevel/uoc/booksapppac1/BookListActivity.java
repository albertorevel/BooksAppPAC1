package arevel.uoc.booksapppac1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;

import arevel.uoc.booksapppac1.adapters.BookCoverRecyclerAdapter;
import arevel.uoc.booksapppac1.adapters.RecyclerAdapter;
import arevel.uoc.booksapppac1.custom_views.SpaceDecoration;
import arevel.uoc.booksapppac1.model.BookItem;
import arevel.uoc.booksapppac1.model.BookModel;

/**
 * Esta clase define la actividad principal de la aplicación, que gestiona la lista de libros.
 */
public class BookListActivity extends AppCompatActivity {

    // Definimos la lista que contendrá los datos a mostrar
    private final ArrayList<String> list = new ArrayList<>();

    // Definimos una variable que nos permitirá saber si nos encontramos con pantalla dividida
    public static boolean dualScreen = false;

    // Variables para autenticarse contra el servidor de Firebase
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseUser mUser;

    // Variables que permiten hacer la autenticación. En otra fase las rellenaría el usuario o se
    // recuperarían de las preferencias
    private String userLogin = "arevel@uoc.edu";
    private String userPassword = "pac2uoc18";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Definimos el layout que usaremos para mostrar la información gestionada
        setContentView(R.layout.book_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Buscamos el framelayout de detalles. Si existe, estamos en tablet y guardamos esta
        // información para poder usarla en la aplicación
        if (findViewById(R.id.detail_framelayout) != null) {
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

        // Inicializamos las clases necesarias para la comunicación con Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        final StringBuilder logSb = new StringBuilder();
        logSb.append(getString(R.string.signInWithEmail_log));

        // Hacemos el login en el proyecto de Firebase
        mAuth.signInWithEmailAndPassword(userLogin, userPassword).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Se ha realizado el login de manera correcta
                            logSb.append(getString(R.string.success_log));
                            Log.d("FIREBASE_CONN", logSb.toString());

                            mUser = mAuth.getCurrentUser();

                            getBooksFromBackEnd();

                        } else {
                            // Ha habido un error autenticando al usuario
                            logSb.append(getString(R.string.failure_log));
                            Log.w("FIREBASE_CONN", logSb.toString(), task.getException());

                            Toast.makeText(BookListActivity.this,
                                    getString(R.string.authentication_error), Toast.LENGTH_SHORT)
                                    .show();
                        }

                    }
                }
        );


    }

    public void getBooksFromBackEnd() {

        DatabaseReference ref = database.getReference();
        final StringBuilder logSb = new StringBuilder(getString(R.string.dataValueEventListener_log));

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                BookModel.populateFromFirebase(dataSnapshot);

                defineBookList();

                logSb.append(getString(R.string.success_log));
                Log.d("FIREBASE_CONN", logSb.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                logSb.append(getString(R.string.failure_log));
                logSb.append(getString(R.string.databaseError));
                logSb.append(getString(R.string.databaseError));
                logSb.append(databaseError.getCode());
                Log.e("DATA_", logSb.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Creamos el menú de la lista
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Ordenamos la lista según la opción seleccionada
        List<BookItem> sortedList;

        switch (item.getItemId()) {

            case R.id.sortByAuthor_option:
                sortedList = BookModel.sortBy(BookModel.SORT_CRITERIA.AUTHOR);
                break;
            case R.id.sortByTitle_option:
                sortedList = BookModel.sortBy(BookModel.SORT_CRITERIA.TITLE);
                break;
            default:
                sortedList = BookModel.getITEMS();
        }

        // Creamos un nuevo objeto adapter con la lista y notificamos del cambio para que se pueda
        // mostrar la nueva lista.
        if (sortedList != null) {

            RecyclerView recyclerView = findViewById(R.id.book_recyclerview);

            defineBookList();

        }

        return super.onOptionsItemSelected(item);
    }

    // Este método crea un adapter a partir de una lista de libros. Es usado en el ejercicio 6.
    private void populateCoverBookList(RecyclerView recyclerView, List<BookItem> bookList, boolean initial) {

        // Creamos el adapter a partir de la lista y se lo asociamos a la lista RecyclerView que
        // contiene la vista.
        BookCoverRecyclerAdapter bookCoverRecyclerAdapter = new BookCoverRecyclerAdapter(bookList);
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
        if (initial) {

            SpaceDecoration spaceDecoration = new SpaceDecoration(this.getResources()
                    .getDimensionPixelSize(R.dimen.gridOffSet), spanCount);

            recyclerView.addItemDecoration(spaceDecoration);
        }

    }

    private void defineBookList() {

        // Obtenemos el RecyclerView que contiene la lista a mostrar
        RecyclerView recyclerView = findViewById(R.id.book_recyclerview);

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
            // ejercicio 6.
            populateCoverBookList(recyclerView, BookModel.getITEMS(), true);

        }
    }

}
