package arevel.uoc.booksapppac1.custom_views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Gracias a esta clase, podemos definir los márgenes entre elementos en el StaggeredLayout que se
 * utiliza en el paso 6 de la práctica.
 */
public class SpaceDecoration extends RecyclerView.ItemDecoration {

    // Margen que se aplicará y elementos por fila, necesario para no duplicar márgenes
    private int margin;
    private int spanCount;

    public SpaceDecoration(int margin, int spanCount) {
        this.margin = margin;
        this.spanCount = spanCount;
    }

    // Este método define los márgenes en el momento de ser llamado desde el RecyclerView que lo usa.
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        super.getItemOffsets(outRect, view, parent, state);
        // Definimos los márgenes laterales e inferior siempre;
        // el superior solamente si se trata de la primera fila de elementos.
        if (parent.getChildAdapterPosition(view) < this.spanCount) {
            outRect.set(margin, margin, margin, margin);
        } else {
            outRect.set(margin, 0, margin, margin);
        }
    }
}
