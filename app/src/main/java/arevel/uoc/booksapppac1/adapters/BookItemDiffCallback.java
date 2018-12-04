package arevel.uoc.booksapppac1.adapters;

import android.support.v7.util.DiffUtil;

import java.util.List;

import arevel.uoc.booksapppac1.model.BookItem;

/**
 * Esta clase permitirá comparar dos listas de libros, que estarán contenidas en los Adapter
 * presentes en la aplicación. Esta comparación permite redibujar la manera de manera más eficiente,
 * comparando una lista de objetos en vez de rehaciendo todas las vistas, permitiendo que cambie
 * solamente aquellos elementos que son diferentes a la lista anterior, lo que permite aligerar
 * la ejecución de la aplicación.
 * Esta clase se añade como parte de la implementación del ejercicio 5 de la PAC3.
 */
public class BookItemDiffCallback extends DiffUtil.Callback {

    // Definimos dos objetos para almacenar la lista antigua y la nueva.
    private final List<BookItem> mOldBookItemList;
    private final List<BookItem> mNewBookItemList;

    // Constructor de la clase, donde se almacenan ambas listas.
    BookItemDiffCallback(List<BookItem> oldBookItemList, List<BookItem> newBookItemList) {
        this.mOldBookItemList = oldBookItemList;
        this.mNewBookItemList = newBookItemList;
    }

    // Devuelve el tamaño de la lista antigua.
    @Override
    public int getOldListSize() {
        return mOldBookItemList.size();
    }

    // Devuelve el tamaño de la lista nueva.
    @Override
    public int getNewListSize() {
        return mNewBookItemList.size();
    }

    // Compara si los objetos son los mismos. Devuelve true por un problema de con los objetos y sus referencias.
    // Hay que refactorizar el uso del DiffUtil o hacer una copia de los objetos en la lista para no
    // usar los objetos Realm.
    // Esta solución es temporal ya que aquí deberíamos comparar los Ids, y si no son iguales, devolver
    // false. La solución es semánticamente incorrecta y el efecto visual queda extraño, aunque se
    // opta por mantenerla en este punto del proyecto ya que realiza el comportamiento deseado.
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }

    // No puede cambiar el contenido en esta aplicación ahora mismo, por lo que devolverá siempre true.
    // No obstante, debido al problema indicado antes, la comparación se realiza aquí.
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        try {
            return mOldBookItemList.get(oldItemPosition).getId() == mNewBookItemList.get(
                    newItemPosition).getId();
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        }
        return false;
    }

}
