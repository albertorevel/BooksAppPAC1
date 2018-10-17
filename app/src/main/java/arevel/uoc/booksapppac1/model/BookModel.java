package arevel.uoc.booksapppac1.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import arevel.uoc.booksapppac1.R;

/**
 * Clase que implementa el modelo de datos de libros de la aplicación
 */
public class BookModel {

    // Array que contendrá los datos
    private static final List<BookItem> ITEMS = new ArrayList<>();

    private static Comparator<BookItem> authorComparator = null;
    private static Comparator<BookItem> titleComparator = null;

    // Código estático que creará y  añadirá una vez los datos de ejemplo al listado de BookItems
    static {
        BookItem book1 = new BookItem(0, "A Title ", "ZZ Author", new Date(),
                "Description", null, R.drawable.other_bookcover1);
        BookItem book2 = new BookItem(1, "Title2", "Aa Author 2", new Date(),
                "Description 2", null, R.drawable.default_bookcover);
        BookItem book3 = new BookItem(2, "zTitle2", "Author", new Date(),
                "Description 3", null, R.drawable.default_bookcover);
        BookItem book4 = new BookItem(3, "Title", "Aa Author 2", new Date(),
                "Description 4", null, R.drawable.other_bookcover1);
        BookItem book5 = new BookItem(4, "Title5", "Another author", new Date(),
                "Description 5", null, R.drawable.other_bookcover2);
        BookItem book6 = new BookItem(5, "Title 99", "Author", new Date(),
                "Description 6", null, R.drawable.default_bookcover);
        BookItem book7 = new BookItem(6, "Another title", "William", new Date(),
                "Description 7", null, R.drawable.other_bookcover2);
        BookItem book8 = new BookItem(7, "El Quijote", "Cervantes", new Date(),
                "Description 8", null, R.drawable.other_bookcover1);

        ITEMS.add(book1);
        ITEMS.add(book2);
        ITEMS.add(book3);
        ITEMS.add(book4);
        ITEMS.add(book5);
        ITEMS.add(book6);
        ITEMS.add(book7);
        ITEMS.add(book8);


        // Creamos los comparadores que se utilizarán en la ordenación de la lista.
        createComparators();
    }

    private static void createComparators() {
        // Comparador por autor
        authorComparator = new Comparator<BookItem>() {
            @Override
            public int compare(BookItem bookItem1, BookItem bookItem2) {

                int result = 0;

                // Comparamos, evitando NullPointerExceptions
                if (bookItem1 != null && bookItem1.getAuthor() != null && bookItem2 != null) {
                    result = bookItem1.getAuthor().compareToIgnoreCase(bookItem2.getAuthor());
                }

                // Devolvemos el resultado
                return result;
            }
        };

        // Comparador por título
        titleComparator = new Comparator<BookItem>() {
            @Override
            public int compare(BookItem bookItem1, BookItem bookItem2) {

                int result = 0;

                // Comparamos, evitando NullPointerExceptions
                if (bookItem1 != null && bookItem1.getTitle() != null && bookItem2 != null) {
                    result = bookItem1.getTitle().compareToIgnoreCase(bookItem2.getTitle());
                }

                // Devolvemos el resultado
                return result;
            }
        };
    }

    /**
     * Devuelve la lista ordenada según el parámetro especificado. La lista se queda almacenada con
     * ese criterio.
     *
     * @param sortCriteria criterio de ordenación
     * @return la lista ordenada
     */
    public static List<BookItem> sortBy(SORT_CRITERIA sortCriteria) {

        Comparator<BookItem> currentComparator = null;

        switch (sortCriteria) {
            case AUTHOR:
                Collections.sort(getITEMS(), authorComparator);
                break;
            case TITLE:
                Collections.sort(getITEMS(), titleComparator);
                break;

        }
        return getITEMS();
    }

    // Devuelve la lista de BookItems que gestiona este modelo
    public static List<BookItem> getITEMS() {
        return ITEMS;
    }

    // Constantes que definen la ordenación de la lista
    public enum SORT_CRITERIA {
        //DEFAULT, // Ahora mismo no hay opción de volver a la lista original, esta constante serviría para ello
        AUTHOR,
        TITLE
    }
}
