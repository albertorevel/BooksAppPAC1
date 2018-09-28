package arevel.uoc.booksapppac1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Utils {

    public static void startDetailsFragment(AppCompatActivity activity) {
    Bundle arguments = new Bundle();
    BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(arguments);
    activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_framelayout, fragment)
                .commit();
    }
}
