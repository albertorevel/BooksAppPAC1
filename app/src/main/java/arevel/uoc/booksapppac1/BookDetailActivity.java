package arevel.uoc.booksapppac1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Definimos la layout que usaremos para mostrar la información gestionada
        setContentView(R.layout.book_detail);

        Utils.startDetailsFragment(this);

    }
}
