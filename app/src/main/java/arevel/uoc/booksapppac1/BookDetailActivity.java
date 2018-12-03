package arevel.uoc.booksapppac1;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import arevel.uoc.booksapppac1.model.BookItem;
import arevel.uoc.booksapppac1.model.BookModel;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Esta clase contiene la actividad de detalle de un libro, usada en la aplicación cuando se trata
 * de un móvil.
 */
public class BookDetailActivity extends AppCompatActivity {

    // Definimos los bindings de las vistas para ButterKnife

    // Toolbar que sobreescribirá el de Android
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;

    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;

    @BindView(R.id.detail_framelayout)
    NestedScrollView nestedScrollView;

    // Definimos aquí la headerImageView que usará el fragment para que pueda encontrarla al llamar
    // a método Butterknife.bind();
    @Nullable
    @BindView(R.id.headerImage)
    ImageView headerImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Definimos el layout que usaremos para mostrar la información gestionada
        setContentView(R.layout.book_detail);

        // Creamos los bindings definidos para Butterknife
        ButterKnife.bind(this);

        // Definimos la toolbar de la aplicación
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Obtenemos el id del elemento seleccionado
        int id = getIntent().getIntExtra(Constants.BOOK_ID, -1);


        // Recuperamos el libro y asociamos la información a los elementos del fragment
        //BookItem bookItem = BookModel.getITEMS().get(id);
        BookItem bookItem = BookModel.findBookById(id);

        if (bookItem != null) {
            String str = bookItem.getTitle();

            // Cambiamos el título de la barra de la aplicación
            getSupportActionBar().setTitle(str);

            // Lanzamos el fragment mostrará el detalle en el frame layout, pasando el id del elemento
            ActivitiesUtils.startDetailsFragment(getSupportFragmentManager(), id);
        } else {
            // Si el libro no ha podido ser encontrado, mostramos un mensaje y cerramos la actividad
            // volviendo al home.
            int messageDuration = 3000;
            final BookDetailActivity self = this;

            // Creamos el mensaje advirtiendo el usuario
            Snackbar.make(nestedScrollView, getString(R.string.noBookFound),
                    messageDuration).show();

            // Definimos un handler que ejecute el método finish de la actividad cuando haya pasado
            // el tiempo establecido. En este caso, será el mismo tiempo que se muestra el mensaje
            // Snackbar
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    self.finish();
                }
            }, messageDuration);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Capturamos el clic en el botón atrás de la Action Bar
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
