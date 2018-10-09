package arevel.uoc.booksapppac1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import arevel.uoc.booksapppac1.adapters.RecyclerAdapter;
import arevel.uoc.booksapppac1.model.BookItem;
import arevel.uoc.booksapppac1.model.BookModel;

public class BookListActivity extends AppCompatActivity {

    // Definimos la lista que contendrá los datos a mostrar
    private final ArrayList<String> list = new ArrayList<>();

    // Definimos una variable que nos permitirá saber si nos encontramos con pantalla dividida
    public static boolean dualScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Definimos el layout que usaremos para mostrar la información gestionada
        setContentView(R.layout.book_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
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

        /*
         * ********************
         * *** Ejercicio 2 ****
         * ********************
         * */

        // Obtenemos el RecyclerView que contiene la lista a mostrar
        RecyclerView recyclerView = findViewById(R.id.book_recyclerview);

        // Creamos el adapter que gestionará los datos de la lista, pasándole como parámetro el
        // conjunto de datos a mostrar y lo asociamos al RecyclerView
        RecyclerAdapter adapter = new RecyclerAdapter(BookModel.getITEMS());
        recyclerView.setAdapter(adapter);

        // Creamos un LinearLayoutManager y lo asociamos al RecyclerView
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

//        getSupportActionBar().setDisplayOptions(
//                ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);


        // Buscamos el framelayout de detalles. Si existe, estamos en tablet y guardamos esta
        // información para poder usarla en la aplicación
        if (findViewById(R.id.detail_framelayout) != null) {
            dualScreen = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list , menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        List<BookItem> sortedList = null;

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

        if (sortedList != null) {

            RecyclerView recyclerView = findViewById(R.id.book_recyclerview);
            RecyclerAdapter recyclerAdapter = new RecyclerAdapter(sortedList);

            recyclerView.setAdapter(recyclerAdapter);
            recyclerAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }
}
