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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import arevel.uoc.booksapppac1.ActivitiesUtils;
import arevel.uoc.booksapppac1.BookDetailActivity;
import arevel.uoc.booksapppac1.BookListActivity;
import arevel.uoc.booksapppac1.R;
import arevel.uoc.booksapppac1.model.BookItem;

/**
 * Este adapter nos permitirá proveer a la lista del ejercicio 6 en móvil,
 * imágenes con todos los datos y manejarla correctamente
 */
public class BookCoverRecyclerAdapter extends RecyclerView.Adapter<BookCoverRecyclerAdapter.RecyclerViewHolder> {

    // Constantes que nos permitirán distinguir elementos pares e impares
    private final static int FIRST = 0;
    private final static int SECOND = 1;
    private final static int THIRD = 2;

    // Conjunto de datos que manejará el adapter
    private List<BookItem> dataSet;

    // Constructor del adapter, al que le pasamos el conjunto de datos
    public BookCoverRecyclerAdapter(List<BookItem> dataSet) {
        this.dataSet = dataSet;
    }

    // En este método creamos un ViewHolder y le asociamos el layout que nos interese,
    // que dependerá del viewType que llegue como parámetro
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Asociamos el layout deseado al viewgroup que se pasa por parámetros y creamos un nuevo
        // Viewholder para esa vista que hemos asociado.
        ConstraintLayout view = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookcover_listitem, parent, false);
        return new BookCoverRecyclerAdapter.RecyclerViewHolder(view);
    }

    // En este método asociamos un elemento del conjunto de datos a la vista gestionada por el
    // objeto ViewHolder que llega por parámetro
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {

        // Asociamos los datos
        holder.bookItem = dataSet.get(position);
        holder.titleTextView.setText(holder.bookItem.getTitle());
        holder.authorTextView.setText(holder.bookItem.getAuthor());
        holder.coverImageView.setImageResource(holder.bookItem.getDrawableId());


        // Definimos el las acciones a realizar cuando se produzca un click en el elemento
        holder.baseConstraintLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int currentPos = holder.getAdapterPosition();

                if (BookListActivity.dualScreen) {
                    FragmentManager supportFM = ((AppCompatActivity)
                            view.getContext()).getSupportFragmentManager();
                    ActivitiesUtils.startDetailsFragment(supportFM, currentPos);
                } else {

                    Intent i = new Intent(view.getContext(), BookDetailActivity.class);
                    i.putExtra("SELECTED_ID", currentPos);
                    view.getContext().startActivity(i);
                }
            }
        });
    }

    // Devolvemos el tamaño del conjunto de datos
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    // Clase que define un ViewHolder que permitirá referenciar las vistas de cada elemento de la lista
    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        // Vistas que contendrá el elemento
        ConstraintLayout baseConstraintLayout;
        TextView titleTextView;
        TextView authorTextView;
        ImageView coverImageView;
        BookItem bookItem;

        // Constructor de la clase donde asociamos las vistas que nos interesen a una serie de
        // atributos para poder manejarlas posteriormente
        RecyclerViewHolder(ConstraintLayout baseConstraintLayout) {
            super(baseConstraintLayout);
            this.baseConstraintLayout = baseConstraintLayout;
            this.titleTextView = baseConstraintLayout.findViewById(R.id.title_textview);
            this.authorTextView = baseConstraintLayout.findViewById(R.id.author_textview);
            this.coverImageView = baseConstraintLayout.findViewById(R.id.bookCover_imageview);

        }
    }
}
