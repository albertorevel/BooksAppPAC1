package arevel.uoc.booksapppac1.custom_views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Esta clase permite crear el efecto de fading cuando quedan elementos en la lista, evitando que se
 * cree el fading en la parte superior. El resto de métodos los hereda de RecyclerView
 */
public class FadingBottomRecyclerView extends RecyclerView {


    // Constructor de la clase
    public FadingBottomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    // Siempre devolverá un cero en este método, haciendo que el fading superior no se pinte.
    @Override
    protected float getTopFadingEdgeStrength() {
        return 0;
    }
}
