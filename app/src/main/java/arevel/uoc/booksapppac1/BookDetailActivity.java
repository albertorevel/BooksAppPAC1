package arevel.uoc.booksapppac1;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import arevel.uoc.booksapppac1.model.BookItem;
import arevel.uoc.booksapppac1.model.BookModel;

public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Definimos el layout que usaremos para mostrar la información gestionada
        setContentView(R.layout.book_detail);


        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Obtenemos el id del elemento seleccionado
        int id = getIntent().getIntExtra("SELECTED_ID",0);


        // Recuperamos el libro y asociamos la información a los elementos del fragment
        BookItem bookItem = BookModel.getITEMS().get(id);
        String str = bookItem.getTitle();

        // Cambiamos el título de la barra de la aplicación
        getSupportActionBar().setTitle(str);

        // Lanzamos el fragment mostrará el detalle en el frame layout, pasando el id del elemento
        ActivitiesUtils.startDetailsFragment(getSupportFragmentManager(), id);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if(getSupportFragmentManager().getBackStackEntryCount()>0)
                    getSupportFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
