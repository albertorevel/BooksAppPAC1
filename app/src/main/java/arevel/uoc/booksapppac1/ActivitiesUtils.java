package arevel.uoc.booksapppac1;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import arevel.uoc.booksapppac1.Constants.DRAWER_ACTION;

import static arevel.uoc.booksapppac1.Constants.DETAIL_FRAGMENT_TAG;
import static arevel.uoc.booksapppac1.Constants.DRAWER_ACTION.COPY_CLIPBOARD;
import static arevel.uoc.booksapppac1.Constants.DRAWER_ACTION.SHARE_OTHERAPPS;
import static arevel.uoc.booksapppac1.Constants.DRAWER_ACTION.SHARE_WHATSAPP;

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
    // TODO comentar y ver si metemos opciones ordenación
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

                        Intent intent;

                        // ******************

                        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
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
                        path = file.getPath();
                        Uri bmpUri = Uri.parse("file://" + path);


                        // ******************
//
//                        Uri path = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
//                                + activity.getResources().getResourcePackageName(R.drawable.ic_launcher)
//                        +"/" + activity.getResources().getResourceTypeName(R.drawable.ic_launcher)
//                        + "/" + activity.getResources().getResourceEntryName(R.drawable.ic_launcher)+"");
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, "Aplicación Android de libros");
                        intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
//                        intent.setType("image/*");
                        intent.setType("image/png");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        switch ((DRAWER_ACTION) drawerItem.getTag()) {
                            case SHARE_OTHERAPPS:


                                Intent chooser = Intent.createChooser(intent, "Compartir con...");
                                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                                    activity.startActivity(chooser);
                                }
                                break;
                            case COPY_CLIPBOARD:

                                ClipboardManager clipboard = (ClipboardManager)
                                        activity.getSystemService(Context.CLIPBOARD_SERVICE);

                                ClipData clip = ClipData.newPlainText("Text", "Aplicación Android de libros");

                                if (clipboard != null) {
                                    clipboard.setPrimaryClip(clip);
                                }

                                Snackbar.make(activity.getWindow().getDecorView().getRootView(), "Copiado",
                                        Snackbar.LENGTH_LONG).show();
                                break;

                            case SHARE_WHATSAPP:


                                intent.setPackage("com.whatsapp");

                                activity.startActivity(intent);

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

    // TODO
    static void openWeb(FragmentManager fragmentManager, AppBarLayout appBarLayout) {

        if (fragmentManager != null) {
            // Recuperamos el fragmentManager para
            Fragment activeFragment = fragmentManager.findFragmentByTag(DETAIL_FRAGMENT_TAG);

            if (activeFragment instanceof BookDetailFragment &&
                    ((BookDetailFragment) activeFragment).webView != null) {

                ((BookDetailFragment) activeFragment).showWebView(appBarLayout);
            }
        }
    }
}
