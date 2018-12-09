package arevel.uoc.booksapppac1;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import static arevel.uoc.booksapppac1.Constants.DETAIL_FRAGMENT_TAG;

/**
 * Esta clase contiene métodos comunes a varias actividades del proyecto.
 */
public class ActivitiesUtils {

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

        // Recuperamos el fragment que se está mostrando y lo eliminamos
        Fragment activeFragment = fragmentManager.findFragmentByTag(DETAIL_FRAGMENT_TAG);

        if (activeFragment != null) {
            fragmentManager.beginTransaction().remove(activeFragment).commit();
        }
    }

    /**
     * Este método crea un Drawer (menú lateral) para la aplicación. Se encuentra aquí para que pueda
     * ser usado por todas las actividades que lo necesiten, aunque ahora mismo solamente se use en
     * la actividad principal.
     *
     * @param activity la actividad donde se mostrará el drawer
     * @param toolbar  la toolbar que usa la actividad
     */
    static void createDrawer(final Activity activity, Toolbar toolbar) {

        // Creamos la cabecera con los datos del perfil del usuario
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .addProfiles(
                        new ProfileDrawerItem().withName("Profile Name").withEmail("arevel@uoc.edu")
                                .withIcon("https://c.pxhere.com/images/c6/91/441b53afa0a69dc4acfbb01dd205-1447095.jpg!d")
                )

                .withSelectionListEnabledForSingleProfile(false)
                .build();


        DrawerImageLoader.getInstance().setImage(headerResult.getHeaderBackgroundView(),
                Uri.parse("https://images.unsplash.com/photo-1491841550275-ad7854e35ca6?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"), "Header");

        // Creamos las opciones del menú
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1)
                .withName(activity.getResources().getString(R.string.share_otherapps))
                .withIcon(FontAwesome.Icon.faw_share_alt).withSelectable(false);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2)
                .withName(activity.getResources().getString(R.string.copy_clipboard))
                .withIcon(FontAwesome.Icon.faw_clipboard).withSelectable(false);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3)
                .withName(activity.getResources().getString(R.string.share_whatsapp))
                .withIcon(FontAwesome.Icon.faw_whatsapp).withSelectable(false);

        // Creamos el menú con los elementos creados anteriormente y definimos un listener
        // que nos permitirá gestionar la opción seleccionada
        Drawer drawer = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withSelectedItem(0)
                .addDrawerItems(
                        item1,
                        item2,
                        item3
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {


                        switch (position) {
                            case 1:

                                break;
                            case 2:
                                break;
                            case 3:

                                break;
                        }

                        return true;
                    }
                })
                .build();

        if (drawer != null) {
            drawer.setSelection(-1);
        }
    }
}
