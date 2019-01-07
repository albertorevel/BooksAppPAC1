package arevel.uoc.booksapppac1.web;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import arevel.uoc.booksapppac1.BookDetailFragment;

/**
 * Esta clase hereda de la clase WebViewClient lo que permite manejar la navegación de la vista
 * WebView a la que le sea asociada. En este caso la usamos para manejar la WebView que se encuentra
 * dentro del fragment de detalle de un libro.
 */
public class MyWebViewClient extends WebViewClient {

    // Fragment que contiene el Webclient. Es necesario para informarle del resultado de las
    // operaciones. (Dejaría de serlo si se implementara, por ejemplo, un bus de eventos).
    private BookDetailFragment fragment;

    // Constructor de la clase
    public MyWebViewClient(BookDetailFragment fragment) {
        this.fragment = fragment;
    }

    // En este método capturaremos las peticiones realizadas desde ese WebView y podremos decidir
    // qué hacer con ellas.
    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, String url) {

        Uri uri = Uri.parse(url);

        // Comprobamos que la petición venga de la página que esperamos
        if (uri != null && "/android_asset/form.html".equals(uri.getPath())) {

            // Comprobamos que venga del submit del formulario definido
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

        // De momento devolvemos siempre false ya que no queremos que el navegador procese ninguna
        // llamada HTTP
        return false;
    }
}
