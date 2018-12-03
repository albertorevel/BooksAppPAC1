package arevel.uoc.booksapppac1;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import arevel.uoc.booksapppac1.model.BookItem;
import arevel.uoc.booksapppac1.model.BookModel;
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

    // Id que usaremos para saber el detalle del libro seleccionado
    private int bookId = -1;

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


    // Definimos los recursos que usa el fragment
    @BindString(R.string.noBookFound)
    String error_noBookFound;
    @BindString(R.string.noData)
    String error_noData;


    ImageView headerImageView;

    private Unbinder unbinder;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Obtenemos el id seleccionado para mostrar sus detalles
        bookId = getArguments() != null ? getArguments().getInt(Constants.BOOK_ID) : 0;

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

            if (getActivity().getClass() == BookDetailActivity.class) {

                headerImageView = ((BookDetailActivity) getActivity()).headerImageView;

                if (headerImageView != null) {
                    Picasso.with(headerImageView.getContext())
                            .load(bookItem.getUrl_image()).into(headerImageView);
                    guideline.setGuidelinePercent(0);
                }
            } else if (coverImageView != null) {

                // Cargamos la imagen desde la URL proporcionada
                Picasso.with(coverImageView.getContext())
                        .load(bookItem.getUrl_image()).into(coverImageView);
                // Cuando la imagen venía de drawable
                // coverImageView.setImageResource(R.drawable.default_bookcover);

            }
        } else {
            // Si el libro no ha podido ser encontrado, mostramos un mensaje y eliminamos el fragment
            int messageDuration = 3000;
            final FragmentManager fragmentManager = getFragmentManager();
            final BookDetailFragment self = this;

            // Creamos el mensaje advirtiendo el usuario
            Snackbar.make(v, error_noBookFound,
                    messageDuration).show();

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
        unbinder.unbind();
    }
}
