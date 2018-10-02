package arevel.uoc.booksapppac1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity {

    // Definimos la lista que contendrá los datos a mostrar
    private final ArrayList<String> list = new ArrayList<>();

    // Definimos una variable que nos permitirá saber si nos encontramos con pantalla dividida
    private static boolean dualScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Definimos el layout que usaremos para mostrar la información gestionada
        setContentView(R.layout.book_list);

        // Recuperamos la ListView que contiene la lista de libros
        final ListView listview = findViewById(R.id.book_listview);
        final FrameLayout detailFrameLayout = findViewById(R.id.detail_framelayout);

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

        // Existe la vista, por lo tanto, estamos en tablet
        if (detailFrameLayout != null) {
            dualScreen = true;
        }
    }
}
