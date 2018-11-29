package arevel.uoc.booksapppac1.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import arevel.uoc.booksapppac1.ActivitiesUtils;
import arevel.uoc.booksapppac1.BookDetailActivity;
import arevel.uoc.booksapppac1.BookListActivity;
import arevel.uoc.booksapppac1.Constants;
import arevel.uoc.booksapppac1.R;
import arevel.uoc.booksapppac1.model.BookItem;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Este adapter nos permitirá proveer a la lista del ejercicio 6 en móvil,
 * imágenes con todos los datos y manejarla correctamente
 */
public class BookCoverRecyclerAdapter
        extends RecyclerView.Adapter<BookCoverRecyclerAdapter.RecyclerViewHolder> {

    // Conjunto de datos que manejará el adapter
    private List<BookItem> dataSet;

    // Hacemos el bind de las vistas que necesitamos definir


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
        CardView view = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookcover_listitem, parent, false);

        ButterKnife.bind(this, view);


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

        // Cargamos la imagen desde la URL proporcionada
        Picasso.with(holder.coverImageView.getContext())
                .load(holder.bookItem.getUrl_image()).into(holder.coverImageView);
        //  holder.coverImageView.setImageResource(R.drawable.default_bookcover);


        // Definimos el las acciones a realizar cuando se produzca un click en el elemento
        holder.baseView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Obtenemos el identificador del libro mostrado en el elemento sobre el que se ha hecho click.
                int id = holder.bookItem.getId();

                if (BookListActivity.dualScreen) {
                    FragmentManager supportFM = ((AppCompatActivity)
                            view.getContext()).getSupportFragmentManager();
                    ActivitiesUtils.startDetailsFragment(supportFM, id);
                } else {

                    Intent i = new Intent(view.getContext(), BookDetailActivity.class);
                    i.putExtra(Constants.BOOK_ID, id);
                    view.getContext().startActivity(i);
                }
            }
        });
    }

    // Este método asocia una nueva lista al adapter y notifica del cambio a este para que la muestre
    public void updateItems(List<BookItem> dataSet) {
        final BookItemDiffCallback diffCallback = new BookItemDiffCallback(this.dataSet, dataSet);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.dataSet.clear();
        this.dataSet.addAll(dataSet);
        diffResult.dispatchUpdatesTo(this);
    }

    // Devolvemos el tamaño del conjunto de datos
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    // Clase que define un ViewHolder que permitirá referenciar las vistas de cada elemento de la lista
    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        // Vistas que contendrá el elemento
        CardView baseView;

        @BindView(R.id.title_textview)
        TextView titleTextView;

        @BindView(R.id.author_textview)
        TextView authorTextView;

        @BindView(R.id.bookCover_imageview)
        ImageView coverImageView;

        // Libro que contedrá el elemento
        BookItem bookItem;

        // Constructor de la clase donde asociamos las vistas que nos interesen a una serie de
        // atributos para poder manejarlas posteriormente
        RecyclerViewHolder(CardView cardview) {
            super(cardview);
            this.baseView = cardview;

            ButterKnife.bind(this, cardview);
        }
    }
}
