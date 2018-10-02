package arevel.uoc.booksapppac1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BookDetailFragment extends Fragment {

    // Id que usaremos para saber el detalle del libro seleccionado
    private int bookId = -1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Obtenemos el id seleccionado para mostrar sus detalles
        bookId = getArguments().getInt("BOOK_DETAIL_ID");

        // Generamos el texto a mostrar con el id recuperado y lo asociamos al TextView que lo debe
        // mostrar
        String detailText = getResources().getString(R.string.list_detail_example) + " " + bookId;

        TextView primaryTextView = getActivity().findViewById(R.id.primaryExampleTV);
        primaryTextView.setText(detailText);

        // asociamos el layout del fragment al ViewGroup
        return inflater.inflate(R.layout.book_detail_fragment, container, false);
    }
}
