package arevel.uoc.booksapppac1.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Clase que implementa el modelo de datos de libros de la aplicación
 */
public class BookModel {

    // Array que contendrá los datos
    private static final List<BookItem> ITEMS = new ArrayList<>();

    // Código estático que creará y  añadirá una vez los datos de ejemplo al listado de BookItems
    static {
        BookItem book1 = new BookItem(0,"Title1", "Author1", new Date(),
                "Description", null );
        BookItem book2 = new BookItem( 1, "Title2", "Author2", new Date(),
                "Description 2", null );
BookItem book3 = new BookItem(0,"Title3", "Author1", new Date(),
                "Descr<<<<<iption", null );
        BookItem book4 = new BookItem( 1, "Titlasdasdasdasdasdae4", "Author2", new Date(),
                "Desczzzzription 2", null );
BookItem book5 = new BookItem(0,"Title5", "Authasdor1", new Date(),
                "zxczxczx", null );
        BookItem book6 = new BookItem( 1, "Tasditle2", "Author2", new Date(),
                "Description zxc", null );
BookItem book7 = new BookItem(0,"Titlnnnne1", "Autasdhor1", new Date(),
                "Description", null );
        BookItem book8 = new BookItem( 1, "Titlasbasde2", "Authoasdr2", new Date(),
                "Description 2", null );

        ITEMS.add(book1);
        ITEMS.add(book2);
        ITEMS.add(book3);
        ITEMS.add(book2);
        ITEMS.add(book4);
        ITEMS.add(book5);
        ITEMS.add(book6);
        ITEMS.add(book7);
        ITEMS.add(book8);
        ITEMS.add(book4);
        ITEMS.add(book5);
        ITEMS.add(book6);
        ITEMS.add(book7);
    }

    // Devuelve la lista de BookItems que gestiona este modelo
    public static List<BookItem> getITEMS() {
        return ITEMS;
    }
}
