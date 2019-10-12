package net.qiujuer.italker.common.widget.recycler;

import android.support.v7.widget.RecyclerView;

/**
 * @author dong
 */
public interface AdapterCallback<Data> {
    void update(Data data, RecyclerAdapter.ViewHolder<Data> holder);
}
