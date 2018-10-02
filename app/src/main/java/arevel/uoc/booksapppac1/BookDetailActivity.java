package arevel.uoc.booksapppac1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Definimos el layout que usaremos para mostrar la información gestionada
        setContentView(R.layout.book_detail);

        // Obtenemos el id del elemento seleccionado
        int id = getIntent().getIntExtra("SELECTED_ID",0);

        // Lanzamos el fragment mostrará el detalle en el frame layout, pasando el id del elemento
        ActivitiesUtils.startDetailsFragment(getSupportFragmentManager(), id);

    }
}
