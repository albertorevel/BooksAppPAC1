package arevel.uoc.booksapppac1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import arevel.uoc.booksapppac1.model.BookItem;
import arevel.uoc.booksapppac1.model.BookModel;
import arevel.uoc.booksapppac1.web.MyWebViewClient;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Esta clase gestiona el fragment de detalle de un libro, usado tanto en la actividad de detalle,
 * cuando se trata de un móvil (pantalla de menos de 900 píxeles de ancho), cómo en la actividad de
 * lista.
 */
public class BookDetailFragment extends Fragment {

    // Hacemos el bind de las vistas que necesitamos definir
    @BindView(R.id.author_detail)
    TextView authorTextView;

    @BindView(R.id.publication_detail)
    TextView publicationTextView;

    @BindView(R.id.description_detail)
    TextView descriptionTextView;

    @Nullable
    @BindView(R.id.bookCover_image)
    ImageView coverImageView;

    @BindView(R.id.middle_guideline)
    Guideline guideline;

    @BindView(R.id.detail_webview)
    WebView webView;

    // Definimos los recursos que usa el fragment
    @BindString(R.string.noBookFound)
    String error_noBookFound;
    @BindString(R.string.noData)
    String error_noData;

    // Imagen de la barra de la aplicación
    ImageView headerImageView;

    // Unbinder necesario para gestionar las vistas enlazadas mediante Butter Knife al tratarse de
    // un fragmento.
    private Unbinder unbinder;

    // TODO
    private AppBarLayout appBarLayout = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Obtenemos el id seleccionado para mostrar sus detalles
        int bookId = getArguments() != null ? getArguments().getInt(Constants.BOOK_ID) : 0;

        // Asociamos el layout del fragment al ViewGroup
        View v = inflater.inflate(R.layout.book_detail_fragment, container, false);

        unbinder = ButterKnife.bind(this, v);

        // Este código comentado pertenece a una PAC anterior

        // Generamos el texto a mostrar con el id recuperado
        // String detailText = getResources().getString(R.string.list_detail_example) + " " + bookId;

        // Obtenemos el textview de detalles de ejemplo y lo asociamos al TextView que lo debe mostrar
        // TextView primaryTextView = v.findViewById(R.id.primaryExampleTV);
        // primaryTextView.setText(detailText);

        // Obtenemos el libro del que se debe mostrar el detalle.
        BookItem bookItem = BookModel.findBookById(bookId);

        if (bookItem != null) {

            // Modificamos el contenido de dichas vistas para que muestren la información deseada
            if (authorTextView != null) {
                authorTextView.setText(bookItem.getAuthor());
            }

            if (publicationTextView != null) {
                String pubDate = bookItem.getPublication_date();
                // String strDate = Utils.formatDate(pubDate);
                publicationTextView.setText(pubDate);
            }

            if (descriptionTextView != null) {
                descriptionTextView.setText(bookItem.getDescription());
            }

            // Si podemos recuoerar la imageview que contendrá la portada (en la toolbar), la cargamos.
            if (getActivity() != null && getActivity().getClass() == BookDetailActivity.class) {

                headerImageView = ((BookDetailActivity) getActivity()).headerImageView;

                if (headerImageView != null) {
                    Picasso.with(headerImageView.getContext())
                            .load(bookItem.getUrl_image()).into(headerImageView);
                    guideline.setGuidelinePercent(0);
                }
            }
            // En caso contrario, si existe una imageview para mostrarla, la mostramos
            // (como ocurrirá en caso de la ejecución en tablet)
            else if (coverImageView != null) {

                // Cargamos la imagen desde la URL proporcionada
                Picasso.with(coverImageView.getContext())
                        .load(bookItem.getUrl_image()).into(coverImageView);
                // Cuando la imagen venía de drawable
                // coverImageView.setImageResource(R.drawable.default_bookcover);

            }

            // PAC4. Definimos las propiedades del WebView
            if (this.webView != null) {

                this.webView.getSettings().setLoadWithOverviewMode(true);
                this.webView.getSettings().setUseWideViewPort(false);
                this.webView.getSettings().setSupportZoom(false);
            }
            setFABVisibility(true);

        } else {
            // Si el libro no ha podido ser encontrado, mostramos un mensaje y eliminamos el fragment
            int messageDuration = 3000;
            final FragmentManager fragmentManager = getFragmentManager();

            // Creamos el mensaje advirtiendo el usuario
            Snackbar.make(v, error_noBookFound, messageDuration).show();

            // Definimos un handler que ejecute el método finish de la actividad cuando haya pasado
            // el tiempo establecido. En este caso, será el mismo tiempo que se muestra el mensaje
            // Snackbar
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (fragmentManager != null) {
                        ActivitiesUtils.removeDetailsFragment(fragmentManager);
                    }
                }
            }, messageDuration);
            Snackbar.make(v, error_noData, Snackbar.LENGTH_LONG).show();
        }

        return v;
    }

    // Se debe hacer un unbind de las vistas por el ciclo diferente de vida que tienen los fragments
    // respecto de las actividades,
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        setFABVisibility(false);

        unbinder.unbind();
    }

    /**
     * Este método define la visibilidad del botón FAB, si la actividad es del tipo BookListActivity
     * y dicho botón existe.
     *
     * @param visible indica si se debe mostrar el botón o no.
     */
    private void setFABVisibility(boolean visible) {

        if (getActivity() instanceof BookListActivity) {
            FloatingActionButton fab = ((BookListActivity) getActivity()).fab;

            if (fab != null) {
                fab.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * Este método muestra el WebView contenido en el fragment cargando en él el formulario de compra
     * del libro del cual se está mostrando el detalle.
     *
     * @param appBarLayout AppBar de la aplicación que deberá ocultarse al mostrar la web.
     */
    public void showWebView(AppBarLayout appBarLayout) {

        this.appBarLayout = appBarLayout;

        // Colapsamos la AppBar y ocultamos el FAB.
        if (this.appBarLayout != null) {
            this.appBarLayout.setExpanded(false);
            this.setFABVisibility(false);
        }

        // Mostramos el webView y definimos la URL y el Cliente que debe escuchar las peticiones que
        // realice la página que se carga.
        this.webView.loadUrl(Constants.URL_BOOKS_SHOP);
        this.webView.setWebViewClient(new MyWebViewClient(this));
        this.webView.setVisibility(View.VISIBLE);
    }

    /**
     * Método que se llama al enviar el formulario de la web de compra del libro. Si se ha realizado
     * la compra de manera satisfactoria, cierra la vista de la web. Informa al usuario del resultado
     * en todos los casos.
     *
     * @param buyed indica si el libro se ha comprado correctamente (actualmente, si formulario se
     *              ha validado correctamente o no)
     */
    public void formSubmitted(boolean buyed) {

        // Expandimos la AppBar y mostramos el FAB. Ocultamos del mismo modo el WebView.
        if (buyed) {
            if (this.appBarLayout != null) {
                this.appBarLayout.setExpanded(true);
                this.setFABVisibility(true);
            }

            this.webView.setVisibility(View.GONE);
        }

        // Mostramos un mensaje con el resultado de la compra.

        int messageId = buyed ? R.string.buy_success : R.string.buy_error;
        int messageDuration = 3000;

        // Comprobamos que los métodos getView y getActivity devuelven algo distinto de null, para
        // para evitar posibles NullPointerException.
        View view = getView();
        Activity activity = getActivity();

        if (view != null && activity != null) {

            Snackbar.make(view, getActivity().getResources().getString(messageId),
                    messageDuration).show();
        }
    }
}
