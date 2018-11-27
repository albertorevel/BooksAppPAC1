package arevel.uoc.booksapppac1.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Clase que implementa el modelo de datos de libros de la aplicación
 */
public class BookModel {

    // Array que contendrá los datos
    private static List<BookItem> ITEMS = new ArrayList<>();

    // Comparadores que se usarán para la ordenación de la lista
    private static Comparator<BookItem> authorComparator = null;
    private static Comparator<BookItem> titleComparator = null;

    // Comparador en uso (útil para mantener ordenación de la lista al actualizarla)
    private static SORT_CRITERIA currentSortCriteria = SORT_CRITERIA.DEFAULT;

    static {

        /*

        // Perteneciente a la PAC1

        // Código estático que creará y  añadirá una vez los datos de ejemplo al listado de BookItems

        BookItem book1 = new BookItem(0, "A Title ", "ZZ Author", new Date(),
                "Description", null, R.drawable.other_bookcover2);
        BookItem book2 = new BookItem(1, "Title2", "Aa Author 2", new Date(),
                "Description 2", null, R.drawable.default_bookcover);
        BookItem book3 = new BookItem(2, "zTitle2", "Author", new Date(),
                "Description 3", null, R.drawable.default_bookcover);
        BookItem book4 = new BookItem(3, "Title", "Aa Author 2", new Date(),
                "Description 4", null, R.drawable.other_bookcover2);
        BookItem book5 = new BookItem(4, "Title5", "Another author", new Date(),
                "Description 5", null, R.drawable.other_bookcover1);
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
*/

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
     * ese criterio. Si el parámetro de entrada es null, reordena la lista con el último criterio
     * utilizado (o el criterio por defecto en caso de que no se haya llamado todavía);
     *
     * @param sortCriteria criterio de ordenación. Si el valor es null, usa el último conocido.
     * @return la lista ordenada
     */
    public static List<BookItem> sortBy(SORT_CRITERIA sortCriteria) {

        // Si llega un parámetro como null, reordena la lista con el último criterio de ordenación usado
        if (sortCriteria != null) {
            currentSortCriteria = sortCriteria;
        }

        switch (currentSortCriteria) {
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

    /**
     * Este método guarda en la lista de items de la clase, los libros recibidos en formato
     * DataSnapShot desde FireBase.
     * Además, comprueba si cada libro está almacenado en la base de datos local, y lo almacena en
     * caso de que no lo esté.
     *
     * @param dataSnapshot libros recibidos desde Firebase
     */
    public static void setItemsFromFireBase(DataSnapshot dataSnapshot) {

        // Creamos las variables que van a ser usadas para crear cada BookItem, así como la lista
        // que almacenaremos, donde se irán añadiendo estos objetos.
        List<BookItem> newList;

        // Obtenemos la lista de libros del objeto de tipo DataSnapshot
        DataSnapshot dataList = dataSnapshot.child("books");

        // Mediante un GenericTypeIndicator, realizamos el parse de los datos recibidos, obteniendo
        // así un listado de objetos BookItem que podemos utilizar.
        GenericTypeIndicator<ArrayList<BookItem>> genericTypeIndicator =
                new GenericTypeIndicator<ArrayList<BookItem>>() {
                };
        newList = dataList.getValue(genericTypeIndicator);

        if (newList != null) {
            for (int i = 0; i < newList.size(); i++) {

                // Añadimos el id del libro, que corresponde con la posición de la lista.
                final BookItem eachBookItem = newList.get(i);
                eachBookItem.setId(i);

                // Comprobamos la existencia del libro en la base de datos.
                if (!exists(eachBookItem.getTitle())) {
                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override

                        // Creamos el libro en la base de datos Realm de la aplicación
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(eachBookItem);
                        }
                    });
                }
            }

            ITEMS = newList;
        }
    }

    /**
     * Este método recupera los libros de la base de datos, almacenando como listado de BookItems el
     * resultado de esta operación.
     */
    public static void setItemsFromDatabase() {
        ITEMS = findAllBooks();
    }

    /*
     * MÉTODOS DE LA BASE DE DATOS
     */

    /**
     * Búsqueda de todos los libros existentes en la base de datos
     *
     * @return listado de objetos BookItem con la información de los libros recuperados.
     */
    private static List<BookItem> findAllBooks() {

        RealmResults<BookItem> books = Realm.getDefaultInstance().where(BookItem.class)
                .findAllAsync();

        return new ArrayList<>(books);
    }

    /**
     * Comprueba si un libro dado existe en la base de datos
     * @param bookTitle libro cuya existencia se desea comprobar
     * @return true si el libro existe en la base de datos, false en caso contrario.
     */
    private static boolean exists(String bookTitle) {

        BookItem retrievedBookItem = Realm.getDefaultInstance().where(BookItem.class)
                .like("title", bookTitle).findFirst();

        return retrievedBookItem != null;
    }

    /**
     * Devuelve la información de un libro en un objeto BookItem
     * @param bookId id del libro a buscar
     * @return libro cuyo id es igual al pasado por parámetros
     */
    public static BookItem findBookById(int bookId) {

        return Realm.getDefaultInstance().where(BookItem.class).equalTo("id", bookId)
                .findFirst();
    }


    /**
     * Este método borra un libro de la base de datos local cuyo id sea bookId
     */
    public static void deleteBookAtDatabase(final Integer bookId) {

        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override

            // Creamos el libro en la base de datos Realm de la aplicación
            public void execute(Realm realm) {
                realm.getDefaultInstance().where(BookItem.class).equalTo("id", bookId)
                        .findAll().deleteFirstFromRealm();
            }
        });
    }

    // Constantes que definen la ordenación de la lista
    public enum SORT_CRITERIA {
        DEFAULT,
        AUTHOR,
        TITLE
    }
}
