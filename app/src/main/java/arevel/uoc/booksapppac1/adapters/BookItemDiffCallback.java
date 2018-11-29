package arevel.uoc.booksapppac1.adapters;

import android.support.v7.util.DiffUtil;

import java.util.List;

import arevel.uoc.booksapppac1.model.BookItem;

public class BookItemDiffCallback extends DiffUtil.Callback {

    private final List<BookItem> mOldBookItemList;
    private final List<BookItem> mNewBookItemList;

    public BookItemDiffCallback(List<BookItem> oldBookItemList, List<BookItem> newBookItemList) {
        this.mOldBookItemList = oldBookItemList;
        this.mNewBookItemList = newBookItemList;
    }

    @Override
    public int getOldListSize() {
        return mOldBookItemList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewBookItemList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }

    // No puede cambiar el contenido en esta aplicación ahora mismo.
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        boolean result = false;

        try {
            return mOldBookItemList.get(oldItemPosition).getId() == mNewBookItemList.get(
                    newItemPosition).getId();
        } catch (IllegalStateException ise) {
            result = false;
        }

        return result;
    }

}
