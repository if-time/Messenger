package net.qiujuer.italker.common.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.qiujuer.italker.common.R;
import net.qiujuer.italker.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author dong
 */
public class GalleyView extends RecyclerView {
    private static final int LOADER_ID = 0x0100;
    private static final int MAX_IMAGE_COUNT = 3;   // 最大选中图片数量
    private static final int MIN_IMAGE_FILE_SIZE = 10 * 1024;   // 最小的图片大小
    private LoaderCallback mLoaderCallback = new LoaderCallback();
    private Adapter mAdapter = new Adapter();
    private List<Images> mSelectedImages = new LinkedList<>();
    private SelectedChangeListener mListener;

    public GalleyView(Context context) {
        super(context);
        init();
    }

    public GalleyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new GridLayoutManager(getContext(), 4));

        setAdapter(mAdapter);

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Images>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Images image) {

                // Cell点击操作，如果我们的点击是允许的，那么更新对应的Cell的状态
                // 然后更新界面，同理；如果说不允许点击（已经达到最大的选中数量） 那么就刷新界面
                if (onItemSelectClick(image)) {
                    //noinspection unchecked
                    holder.updateData(image);
                }
            }

        });

    }
    /**
     * 初始化方法
     * @param loaderManager Loader管理器
     * @return 任何一个LOADER_ID，可用于Loader
     */
    public int setup(LoaderManager loaderManager , SelectedChangeListener listener) {
        mListener = listener;
        loaderManager.initLoader(LOADER_ID, null, mLoaderCallback);
        return LOADER_ID;
    }

    /**
     * Cell点击的具体逻辑
     * @param image
     * @return true 进行了数据更改，需要刷新，反之不刷新
     */
    private boolean onItemSelectClick(Images image) {
        // 是否需要进行刷新
        boolean notifyRefresh;
        if (mSelectedImages.contains(image)) {
            // 如果之前在现在就移除
            mSelectedImages.remove(image);
            image.isSelect = false;
            // 状态改变需要更新
            notifyRefresh = true;
        } else {
            if (mSelectedImages.size() >= MAX_IMAGE_COUNT) {
                notifyRefresh = false;
            } else {
                mSelectedImages.add(image);
                image.isSelect = true;
                notifyRefresh = true;
            }
        }
        // 如果数据有更改，需要通知外面的监听者 我们的数据选中改变了
        if (notifyRefresh) {
            notifySelectChanged();
        }
        return true;
    }

    /**
     * 得到选中的图片的全部地址
     * @return 返回一个数组
     */
    public String[] getSelectedPath() {
        String[] paths = new String[mSelectedImages.size()];
        int index = 0;

        for (Images image : mSelectedImages) {
            paths[index++] = image.path;
        }
        return paths;
    }

    /**
     * 进行清空选中的图片
     */
    public void clear() {
        for (Images image : mSelectedImages) {
            // 先重置状态
            image.isSelect = false;
        }
        mSelectedImages.clear();
        // 通知更新
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 通知选中状态改变
     */
    private void notifySelectChanged() {
        // 得到监听者，并判断是否有监听者，然后进行回调数量变化
        SelectedChangeListener listener = mListener;
        if (listener != null) {
            listener.onSelectedCountChnaged(mSelectedImages.size());
        }
    }

    /**
     * 通知Adapter数据更改的方法
     * @param images 新的数据
     */
    private void updateSource(List<Images> images) {
        mAdapter.replace(images);
    }

    /**
     * 用于实际的数据加载的Loader Callback
     */
    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_PROJECTION = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,   // 图片路径
                MediaStore.Images.Media.DATE_ADDED      // 图片创建时间
        };

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
            // 创建Loader
            if (id == LOADER_ID) {
                // 如果是我们的id，则进行初始化
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2] + " DESC"); // 倒序查询
            }
            return null;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
            // 当Laoder加载完成时
            List<Images> images = new ArrayList<>();
            // 判断是否有数据
            if (cursor != null) {
                int count = cursor.getCount();
                if (count > 0) {
                    // 移动游标到开始
                    cursor.moveToFirst();
                    // 得到对应的列的index坐标
                    int indexId = cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate = cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                    do {
                        // 循环读取，直到没有下一条数据
                        int id = cursor.getInt(indexId);
                        String path = cursor.getString(indexPath);
                        long datetime = cursor.getLong(indexDate);

                        File file = new File(path);
                        if (!file.exists() || file.length() < MIN_IMAGE_FILE_SIZE) {
                            // 如果没图片或者图片太小，则跳过
                            continue;
                        }

                        // 添加一条新的数据
                        Images image = new Images();
                        image.id = id;
                        image.path = path;
                        image.date = datetime;
                        images.add(image);

                    }while (cursor.moveToNext());
                }
            }
            updateSource(images);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            // 当Loader销毁或重置, 进行界面清空操作
            updateSource(null);
        }
    }


    /**
     * 内部的数据结构
     */
    private static class Images {
        int id;
        String path;
        long date;
        boolean isSelect;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Images image = (Images) o;
            return Objects.equals(path, image.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path);
        }
    }

    /**
     * 适配器
     */
    private class Adapter extends RecyclerAdapter<Images> {

        @Override
        protected int getItemViewType(int position, Images image) {
            return R.layout.cell_galley;
        }

        @Override
        protected ViewHolder<Images> onCreateViewHolder(android.view.View root, int viewType) {
            return new GalleyView.ViewHolder(root);
        }

        @Override
        public void update(Images image, ViewHolder<Images> holder) {

        }
    }

    /**
     * Cell对应的Holder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<Images> {

        private ImageView mPic;
        private View mShade;
        private CheckBox mSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mPic = itemView.findViewById(R.id.im_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelect = itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Images image) {

            Glide.with(getContext())
                    .load(image.path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)  // 不使用缓存，直接从原图加载
                    .centerCrop()
                    .placeholder(R.color.grey_200)
                    .into(mPic);

            mShade.setVisibility(image.isSelect ? VISIBLE : INVISIBLE);
            mSelect.setChecked(image.isSelect);
        }
    }

    /**
     * 对外的一个监听器
     */
    public interface SelectedChangeListener {
        void onSelectedCountChnaged(int count);
    }
}
