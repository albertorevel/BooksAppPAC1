package arevel.uoc.booksapppac1;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    private static final String DATE_PATTERN = "dd/MM/yyyy";

    public static String formatDate(Date dateToFormat) {

        String formattedDate = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);

        formattedDate = simpleDateFormat.format(dateToFormat);

        return formattedDate;
    }
}
