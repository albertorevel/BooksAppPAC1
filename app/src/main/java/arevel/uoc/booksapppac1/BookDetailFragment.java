package arevel.uoc.booksapppac1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class BookDetailFragment extends Fragment {

    // Id que usaremos para saber el detalle del libro seleccionado
    private int bookId = -1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Obtenemos el id seleccionado para mostrar sus detalles
        try {
            bookId = getArguments().getInt("BOOK_DETAIL_ID");

        } catch (NullPointerException npe) {
            Log.e("DATA", "Error retrieving selected id");
        }

        // asociamos el layout del fragment al ViewGroup
        View v = inflater.inflate(R.layout.book_detail_fragment, container, false);

        // Generamos el texto a mostrar con el id recuperado y lo asociamos al TextView que lo debe
        // mostrar
        String detailText = getResources().getString(R.string.list_detail_example) + " " + bookId;

        TextView primaryTextView = v.findViewById(R.id.primaryExampleTV);
        primaryTextView.setText(detailText);

        return v;
    }
}
