package arevel.uoc.booksapppac1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import arevel.uoc.booksapppac1.model.BookItem;
import arevel.uoc.booksapppac1.model.BookModel;

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

        // Ejercicios anteriores
        //Obtenemos el textview de detalles de ejemplo y lo asociamos al TextView que lo debe mostrar
//        TextView primaryTextView = v.findViewById(R.id.primaryExampleTV);
//        primaryTextView.setText(detailText);

        BookItem bookItem = BookModel.getITEMS().get(bookId);

        if (bookItem != null) {
            TextView authorTextView = v.findViewById(R.id.author_detail);
            TextView publicationTextView = v.findViewById(R.id.publication_detail);
            TextView descriptionTextView = v.findViewById(R.id.description_detail);

            if (authorTextView != null) {
                authorTextView.setText(bookItem.getAuthor());
            }

            if (publicationTextView != null) {
                Date pubDate = bookItem.getPublicationDate();
                String strDate = Utils.formatDate(pubDate);
                publicationTextView.setText(strDate);
            }

            if (descriptionTextView != null) {
                descriptionTextView.setText(bookItem.getDescription());
            }
        }

        return v;
    }
}
