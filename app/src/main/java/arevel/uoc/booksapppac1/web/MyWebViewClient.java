package arevel.uoc.booksapppac1.web;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import arevel.uoc.booksapppac1.BookDetailFragment;

public class MyWebViewClient extends WebViewClient {

    private BookDetailFragment fragment;

    public MyWebViewClient(BookDetailFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, String url) {

        Uri uri = Uri.parse(url);

        if ("/android_asset/form.html".equals(uri.getPath())) {

            if ("Submit".equals(uri.getQueryParameter("buy"))) {

                boolean buyed;

                // Guardamos en diferentes variables los datos recibidos. Si se implementara la
                // compra más adelante, serían necesarios.
                String name = uri.getQueryParameter("name");
                String num = uri.getQueryParameter("num");
                String date = uri.getQueryParameter("date");

                // Comprobamos que hayan llegado informados todos los datos esperados
                buyed = name != null && name.length() > 0 &&
                        num != null && num.length() > 0 &&
                        date != null && date.length() > 0;

                // Informamos al fragment que contiene la WebView del resultado de la compra.
                // En el estado actual de la aplicación no realiza ninguna compra, solamente
                // comprueba que todos los valores esperados hayan sido informados correctamente.
                fragment.formSubmitted(buyed);
            }
        }

        // De momento devolvemos siempre false ya que no queremos acceder a ninguna URL
        return false;
    }
}
