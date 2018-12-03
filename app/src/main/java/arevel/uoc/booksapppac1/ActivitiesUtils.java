package arevel.uoc.booksapppac1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Esta clase contiene métodos comunes a varias actividades del proyecto.
 */
public class ActivitiesUtils {

    private static final String DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT_TAG";
    /**
     * Este método permite iniciar el fragments de detalle, informando del id del elemento seleccionado
     */
    public static void startDetailsFragment(FragmentManager fragmentManager, int id) {

        // Creamos el objeto que pasaremos al fragment para infornar del id que se ha seleccionado
        Bundle arguments = new Bundle();
        arguments.putInt(Constants.BOOK_ID, id);

        // Creamos el fragment y lo asociamos al framelayout de la vista
        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(arguments);
        fragmentManager.beginTransaction()
                .replace(R.id.detail_framelayout, fragment, DETAIL_FRAGMENT_TAG)
                .commit();
    }

    /**
     * Este método permite eliminar el fragment de detalle que se muestra actualmente.
     */
    static void removeDetailsFragment(FragmentManager fragmentManager) {

        // recuperamos el fragment que se está mostrando y lo eliminamos
        Fragment activeFragment = fragmentManager.findFragmentByTag(DETAIL_FRAGMENT_TAG);

        if (activeFragment != null) {
            fragmentManager.beginTransaction().remove(activeFragment).commit();
        }
    }
}
