package arevel.uoc.booksapppac1.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import arevel.uoc.booksapppac1.ActivitiesUtils;
import arevel.uoc.booksapppac1.BookDetailActivity;
import arevel.uoc.booksapppac1.BookListActivity;
import arevel.uoc.booksapppac1.R;
import arevel.uoc.booksapppac1.model.BookItem;

/**
 *  Este adapter nos permitirá proveer a la lista de tipo RecyclerView
 *  de todos los datos y manejarla correctamente
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    // Constantes que nos permitirán distinguir elementos pares e impares
    private final static int EVEN = 0;
    private final static int ODD = 1;
    // Conjunto de datos que manejará el adapter
    private List<BookItem> dataSet;

    // Constructor del adapter, al que le pasamos el conjunto de datos
    public RecyclerAdapter(List<BookItem> dataSet) {
        this.dataSet = dataSet;
    }

    // En este método creamos un ViewHolder y le asociamos el layout que nos interese,
    // que dependerá del viewType que llegue como parámetro
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int mLayout;

        // Elegimos el layout en función del viewtype que llega
        switch (viewType) {
            case EVEN:
                mLayout = R.layout.even_listitem;
                break;

            default:
                mLayout = R.layout.odd_listitem;
                break;
        }

        // Asociamos el layout deseado al viewgroup que se pasa por parámetros y creamos un nuevo
        // Viewholder para esa vista que hemos asociado.
        ConstraintLayout view = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(mLayout, parent, false);
        return new RecyclerAdapter.RecyclerViewHolder(view);
    }

    // En este método asociamos un elemento del conjunto de datos a la vista gestionada por el
    // objeto ViewHolder que llega por parámetro
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {

        // Asociamos los datos
        holder.bookItem = dataSet.get(position);
        holder.titleTextView.setText(holder.bookItem.getTitle());
        holder.authorTextView.setText(holder.bookItem.getAuthor());

        // Definimos el las acciones a realizar cuando se produzca un click en el elemento
        holder.baseConstraintLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int id = holder.bookItem.getId();

                // Si es tablet, crearemos el fragment sin cambiar de actividad.
                if (BookListActivity.dualScreen) {
                    FragmentManager supportFM = ((AppCompatActivity)
                            view.getContext()).getSupportFragmentManager();
                    ActivitiesUtils.startDetailsFragment(supportFM, id);
                }

                // Si es móvil, crearemos una nueva actividad de detalle.
                else {

                    Intent i = new Intent(view.getContext(), BookDetailActivity.class);
                    i.putExtra("SELECTED_ID", id);
                }
            }
        });
    }

    // Este método asocia una nueva lista al adapter y notifica del cambio a este para que la muestre
    public void setItems(List<BookItem> dataSet) {
        this.dataSet = dataSet;
        this.notifyDataSetChanged();
    }

    // Devolvemos el tamaño del conjunto de datos
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    // Método que permitirá distinguir si un elemento de la lista es par o impar
    public int getItemViewType(int position) {

        // Devolvemos un valor dependiendo de si es par o impar. Podria devolverse solamente el
        // resto (position % 2) ya que coincide con las constantes declaradas en la clase, pero
        // se opta por mantenerlo así por si cambiara la codificación
        return position % 2 == 0 ? EVEN : ODD;
    }

    // Clase que define un ViewHolder que permitirá referenciar las vistas de cada elemento de la lista
    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        // Vistas que contendrá el elemento
        ConstraintLayout baseConstraintLayout;
        TextView titleTextView;
        TextView authorTextView;
        BookItem bookItem;

        // Constructor de la clase donde asociamos las vistas que nos interesen a una serie de
        // atributos para poder manejarlas posteriormente
        RecyclerViewHolder(ConstraintLayout baseConstraintLayout) {
            super(baseConstraintLayout);
            this.baseConstraintLayout = baseConstraintLayout;
            this.titleTextView = baseConstraintLayout.findViewById(R.id.title_textview);
            this.authorTextView = baseConstraintLayout.findViewById(R.id.author_textview);
        }
    }
}
