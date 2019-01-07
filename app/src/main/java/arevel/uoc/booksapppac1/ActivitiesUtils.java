package arevel.uoc.booksapppac1;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import arevel.uoc.booksapppac1.Constants.DRAWER_ACTION;
import arevel.uoc.booksapppac1.model.BookModel;
import arevel.uoc.booksapppac1.tools.GenericFileProvider;

import static arevel.uoc.booksapppac1.Constants.DETAIL_FRAGMENT_TAG;
import static arevel.uoc.booksapppac1.Constants.DRAWER_ACTION.COPY_CLIPBOARD;
import static arevel.uoc.booksapppac1.Constants.DRAWER_ACTION.SHARE_OTHERAPPS;
import static arevel.uoc.booksapppac1.Constants.DRAWER_ACTION.SHARE_WHATSAPP;
import static arevel.uoc.booksapppac1.Constants.DRAWER_ACTION.SORT_AUTHOR;
import static arevel.uoc.booksapppac1.Constants.DRAWER_ACTION.SORT_TITLE;

/**
 * Esta clase contiene métodos comunes a varias actividades del proyecto. También contiene referencias
 * a fragments o vistas necesarias.
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

        Resources resources = activity.getResources();

        // Creamos la cabecera con los datos del perfil del usuario
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(resources.getString(R.string.default_profile_name))
                                .withEmail(resources.getString(R.string.default_profile_email))
                                .withIcon(Constants.DEFAULT_PROFILE_ICON_URL)
                )
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        // Definimos la imagen de fondo de la cabecera
        DrawerImageLoader.getInstance().setImage(headerResult.getHeaderBackgroundView(),
                Uri.parse(Constants.DEFAULT_HEADER_URL),
                resources.getString(R.string.header_image_tag));

        // Creamos las opciones del menú
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1)
                .withTag(SHARE_OTHERAPPS)
                .withName(activity.getResources().getString(R.string.share_otherapps))
                .withIcon(FontAwesome.Icon.faw_share_alt).withSelectable(false);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2)
                .withTag(COPY_CLIPBOARD)
                .withName(activity.getResources().getString(R.string.copy_clipboard))
                .withIcon(FontAwesome.Icon.faw_clipboard).withSelectable(false);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3)
                .withTag(SHARE_WHATSAPP)
                .withName(activity.getResources().getString(R.string.share_whatsapp))
                .withIcon(FontAwesome.Icon.faw_whatsapp).withSelectable(false);

        // Añadimos las opciones del menú anterior que se ha eliminado en la PAC4
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4)
                .withTag(SORT_AUTHOR)
                .withName(activity.getResources().getString(R.string.sortByAuthor))
                .withIcon(FontAwesome.Icon.faw_user).withSelectable(false);
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5)
                .withTag(SORT_TITLE)
                .withName(activity.getResources().getString(R.string.sortByTitle))
                .withIcon(FontAwesome.Icon.faw_align_center).withSelectable(false);


        // Creamos el menú con los elementos creados anteriormente y definimos un listener
        // que nos permitirá gestionar la opción seleccionada
        final Drawer drawer = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withSelectedItem(0)
                .withCloseOnClick(true)
                .addDrawerItems(
                        item1,
                        item2,
                        item3,
                        new DividerDrawerItem(),
                        item4,
                        item5
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        // Realizamos una acción u otra dependiendo del elemento seleccionado
                        try {
                            DRAWER_ACTION drawer_action = (DRAWER_ACTION) drawerItem.getTag();

                            switch (drawer_action) {
                                case SHARE_OTHERAPPS:
                                case SHARE_WHATSAPP:
                                    launchShareIntent(activity, drawer_action, view);
                                    break;
                                case COPY_CLIPBOARD:
                                    shareToClipBoard(activity, view);
                                    break;
                                case SORT_TITLE:
                                    if (activity instanceof BookListActivity) {
                                        ((BookListActivity) activity)
                                                .sortList(BookModel.SORT_CRITERIA.TITLE);
                                    }
                                    break;
                                case SORT_AUTHOR:
                                    if (activity instanceof BookListActivity) {
                                        ((BookListActivity) activity)
                                                .sortList(BookModel.SORT_CRITERIA.AUTHOR);
                                    }
                                    break;
                            }

                        } catch (ClassCastException exception) {
                            exception.printStackTrace();
                        }
                        // Devolvemos false para que se cierre el menú al procesar el click en uno
                        // de los elementos
                        return false;
                    }
                })
                .build();

        // Hacemos que no haya ninguna opción marcada al mostrar el Drawer
        if (drawer != null) {
            drawer.setSelection(-1);
        }
    }

    /**
     * Este método copia un texto predefinido al portapapeles e informa al usuario de si la copia
     * se ha podido realizar o no.
     *
     * @param activity desde la cual se ha lanzado la acción. Permite obtener los recursos.
     * @param view     vista que usamos para mostrar el Snackbar
     */
    private static void shareToClipBoard(Activity activity, View view) {

        // Obtenemos el ClipboardManager para poder copiar en él el texto deseado.
        ClipboardManager clipboard = (ClipboardManager)
                activity.getSystemService(Context.CLIPBOARD_SERVICE);

        Resources resources = activity.getResources();

        ClipData clip = ClipData.newPlainText(resources.getString(R.string.clipboardcopy_title),
                resources.getString(R.string.clipboardcopy_body));

        // Copiamos el texto
        int callbackMessageId = R.string.clipboardcopy_fail;

        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            callbackMessageId = R.string.clipboardcopy_ok;
        }

        // Informamos al usuario de si se ha podido realizar la copia. Solamente mostraremos el
        // mensaje de error si ha habido un problema obteniendo el ClipboardManager.
        Snackbar.make(view,
                resources.getString(callbackMessageId),
                Snackbar.LENGTH_LONG).show();

    }

    /**
     * Este método lanza un Intent que permite compartir un texto predefinido y el icono de la
     * aplicación con otras aplicaciones.
     *
     * @param activity      desde la cual se ha lanzado la acción. Permite mostrar el snackbar con el
     *                      resultado al finalizar, obtener recursos y gestionar permisos entre otros.
     * @param drawer_action acción a realizar (Compartir con otra app, Compartir con Whatsapp, etc.)
     * @param view          vista que permite mostrar el Snackbar
     */
    private static void launchShareIntent(Activity activity, DRAWER_ACTION drawer_action, View view) {

        // Ahora mismo solamente lo llamamos desde BookListActivity. Si necesitaramos llamar al
        // mismo método con distintas Activities, deberíamos comprobar que tuvieran el método o que
        // implementaran una interfaz con todos los métodos del Drawer.
        if (!(activity instanceof BookListActivity)) {
            return;
        }

        Resources resources = activity.getResources();

        Bitmap bitmap;
        Drawable m_icon;
        Uri iconUri = null;

        try {
            // Obtenemos el icono de la aplicación y lo transformamos en un objeto de tipo Bitmap
            m_icon = activity.getPackageManager().getApplicationIcon("arevel.uoc.booksapppac1");

            if (m_icon instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) m_icon).getBitmap();
            } else {
                bitmap = Utils.getBitmapFromDrawable(m_icon);
            }

            // Almacenamos el bitmap obtenido para poder compartirlo con otras aplicaciones.
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Share.png";
            File file = new File(path);
            try {
                OutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Obtenemos la URI de la imagen creada con el icono.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                path = file.getPath();
                iconUri = Uri.parse("file://" + path);
            } else {
                iconUri = GenericFileProvider.getUriForFile(activity,
                        activity.getApplicationContext().getPackageName()
                                + ".provider", file);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        // Creamos el Intent que contendrá toda la información que se desea compartir,
        // así como otra información adicional necesaria para realizar la acción
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.clipboardcopy_body));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Añadimos la imagen del icono si no ha ocurrido ningún problema
        if (iconUri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, iconUri);
            intent.setType("image/png");
        }

        switch (drawer_action) {

            // Las actividades (a través del intent creado), no se lanza aquí si no que se hace en
            // la actividad. Esto permite que se pueda observar la respuesta en caso de que se deban
            // solicitar permisos, evitando crear todavía más dependencias ya que en esta clase no
            // se manejan las variables que deberemos tener en la actividad para manejar este intent
            // y la solicitud de permisos.

            case SHARE_OTHERAPPS:

                // Si se comparte con otras apps sin saber cual, se deben mostrar todas aquellas
                // que permiten la compartición del tipo de datos que estamos compartiendo antes de
                // lanzar el intent creado.
                Intent chooser = Intent.createChooser(intent, resources.getString(R.string.share_chooser_title));

                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    ((BookListActivity) activity).launchIntentAndCheckPermission(chooser,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

                break;

            case SHARE_WHATSAPP:

                if (isAppInstalled("com.whatsapp", activity)) {
                    // En caso de que se comparta con Whatsapp, se lanzará la aplicación con el Intent creado.
                    intent.setPackage("com.whatsapp");
                    ((BookListActivity) activity).launchIntentAndCheckPermission(intent,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {

                    // Informamos al usuario de que la aplicación no existe en el dispositivo y que,
                    // por lo tanto no se puede realizar la acción.
                    Snackbar.make(view,
                            resources.getString(R.string.appnotinstalled),
                            Snackbar.LENGTH_LONG).show();
                }

        }
    }

    /**
     * Este método comprueba si una app existe en el dispositivo.
     *
     * @param uri      identificador de la aplicación a buscar
     * @param activity actividad necesaria para obtener ciertos datos.
     * @return true en caso de que exista la aplicación con el uri que se pasa por parámetros; false
     * en caso contrario.
     */
    static boolean isAppInstalled(String uri, Activity activity) {
        PackageManager pm = activity.getPackageManager();
        boolean app_installed;

        // Comprueba la existencia de la app.
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    /**
     * Este método muestra el WebView contenido en el fragment activo, si este es del tipo esperado.
     *
     * @param fragmentManager FragmentManager que nos permite obtener el Fragment activo.
     * @param appBarLayout    App Bar visible, que deberá colapsarse al mostrar la web.
     */
    static void openWeb(FragmentManager fragmentManager, AppBarLayout appBarLayout) {

        if (fragmentManager != null) {

            // Recuperamos el fragmentManager para hallar el Fragment activo
            Fragment activeFragment = fragmentManager.findFragmentByTag(DETAIL_FRAGMENT_TAG);

            // Comprobamos que exista el WebView y el tipo del fragment hallado.
            if (activeFragment instanceof BookDetailFragment &&
                    ((BookDetailFragment) activeFragment).webView != null) {

                // Realizamos una llamada al Fragment para que muestre la web de compra de libros,
                ((BookDetailFragment) activeFragment).showWebView(appBarLayout);
            }
        }
    }
}
