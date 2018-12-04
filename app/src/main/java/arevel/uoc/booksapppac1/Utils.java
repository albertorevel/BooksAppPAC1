package arevel.uoc.booksapppac1;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Esta clase contiene métodos generales, que pueden ser usados en cualquier parte de la aplicación.
 */
class Utils {

    // Patrón con el que se formatearán las fechas
    private static final String DATE_PATTERN = "dd/MM/yyyy";

    /**
     * Método que crea un String a partir de una fecha dada según el patrón definido en esta misma
     * clase.
     * <p>
     * En desuso desde el uso de REALM. Lo marcamos como deprecated por lo tanto, pudiendo eliminarlo
     * si volviese a ser necesario.
     */
    @Deprecated
    static String formatDate(Date dateToFormat) {

        String formattedDate;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.FRANCE);

        formattedDate = simpleDateFormat.format(dateToFormat);

        return formattedDate;
    }
}
