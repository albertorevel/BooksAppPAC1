package arevel.uoc.booksapppac1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.Guideline;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import arevel.uoc.booksapppac1.model.BookItem;
import arevel.uoc.booksapppac1.model.BookModel;

/**
 * Esta clase gestiona el fragment de detalle de un libro, usado tanto en la actividad de detalle,
 * cuando se trata de un móvil (pantalla de menos de 900 píxeles de ancho), cómo en la actividad de
 * lista.
 */
public class BookDetailFragment extends Fragment {

    // Id que usaremos para saber el detalle del libro seleccionado
    private int bookId = -1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Obtenemos el id seleccionado para mostrar sus detalles
        bookId = getArguments() != null ? getArguments().getInt("BOOK_DETAIL_ID") : 0;

        // Asociamos el layout del fragment al ViewGroup
        View v = inflater.inflate(R.layout.book_detail_fragment, container, false);

        // Generamos el texto a mostrar con el id recuperado
        String detailText = getResources().getString(R.string.list_detail_example) + " " + bookId;

        // Este código comentado pertenece al ejercicio 1

        //Obtenemos el textview de detalles de ejemplo y lo asociamos al TextView que lo debe mostrar
        // TextView primaryTextView = v.findViewById(R.id.primaryExampleTV);
        // primaryTextView.setText(detailText);

        // Obtenemos el libro del que se debe mostrar el detalle.
        BookItem bookItem = BookModel.findBookById(bookId);

        if (bookItem != null) {

            // Una vez obtenido el libro, accedemos a las vistas que componen la pantalla.
            TextView authorTextView = v.findViewById(R.id.author_detail);
            TextView publicationTextView = v.findViewById(R.id.publication_detail);
            TextView descriptionTextView = v.findViewById(R.id.description_detail);
            ImageView coverImageView = v.findViewById(R.id.bookCover_image);
            Guideline guideline = v.findViewById(R.id.middle_guideline);

            final ImageView headerImageView;
            if (getActivity() != null && getActivity().findViewById(R.id.app_bar) != null) {
                headerImageView = getActivity().findViewById(R.id.app_bar).findViewById(R.id.headerImage);
            } else {
                headerImageView = null;
            }
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


            if (headerImageView != null) {
                Picasso.with(headerImageView.getContext())
                        .load(bookItem.getUrl_image()).into(headerImageView);
                guideline.setGuidelinePercent(0);
            } else if (coverImageView != null) {

                // Cargamos la imagen desde la URL proporcionada
                Picasso.with(coverImageView.getContext())
                        .load(bookItem.getUrl_image()).into(coverImageView);
                // Cuando la imagen venía de drawable
                // coverImageView.setImageResource(R.drawable.default_bookcover);

            }


        }

        return v;
    }
}
