package arevel.uoc.booksapppac1;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

/*
 * Esta clase contiene métodos comunes a varias actividades del poryecto.
 */
public class ActivitiesUtils {

    /**
     * Este método permite iniciar el fragments de detalle, informando del id del elemento seleccionado
     */
    public static void startDetailsFragment(FragmentManager fragmentManager, int id) {

        // Creamos el objeto que pasaremos al fragment para infornar del id que se ha seleccionado
        Bundle arguments = new Bundle();
        arguments.putInt("BOOK_DETAIL_ID", id);

        // Creamos el fragment y lo asociamos al framelayout de la vista
        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(arguments);
        fragmentManager.beginTransaction()
                .replace(R.id.detail_framelayout, fragment)
                .commit();
    }
}
