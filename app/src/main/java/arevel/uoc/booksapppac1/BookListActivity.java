package arevel.uoc.booksapppac1;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookListActivity extends AppCompatActivity {

    // Definimos la lista que contendrá los datos a mostrar
    private final ArrayList<String> list = new ArrayList<String>();

    // Definimos una variable que nos permitirá saber si nos encontramos con pantalla dividida
    private final boolean dualScreen = false;

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
            list.add("Item " + i);
        }

        // Creamos el adapter que gestionará el rellenado de la lista y el trato de su contenido,
        // añadiendo la lista creada de ejemplo y asociando este adapter posteriormente al listview
        // recuperado anteriormente.
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);

                // Start detail activity
                Intent i = new Intent(getApplicationContext(), BookDetailActivity.class);
                startActivity(i);

            }

        });

        // Existe la vista, por lo tanto, estamos en tablet
        if (detailFrameLayout != null) {

            Utils.startDetailsFragment(this);
        }
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        private StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
