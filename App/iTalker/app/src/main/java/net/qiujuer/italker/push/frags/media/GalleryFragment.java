package net.qiujuer.italker.push.frags.media;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.italker.common.tools.UiTool;
import net.qiujuer.italker.common.widget.GalleryView;
import net.qiujuer.italker.push.R;

/**
 * 图片选择Fragment
 */
public class GalleryFragment extends BottomSheetDialogFragment implements GalleryView.SelectedChangeListener {

    private GalleryView mGallery;
    private onSelectedListener mListener;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 先使用默认的
        return new TransStatusBottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // 获取我们的GalleryView
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        mGallery = root.findViewById(R.id.galleyView);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGallery.setup(getLoaderManager(), this);
    }

    @Override
    public void onSelectedCountChanged(int count) {
        // 如果选中一张图片
        if (count > 0) {
            //隐藏自己
            dismiss();
            if (mListener != null) {
                // 得到所有选中的图片路径
                String[] paths = mGallery.getSelectedPath();
                // 返回第一张
                mListener.onSelectedImage(paths[0]);
                // 取消和唤起者之间的引用，加快回收内存
                mListener = null;
            }
        }
    }

    /**
     * 设置事件监听，并返回自己
     * @param listener
     * @return
     */
    public GalleryFragment setListener(onSelectedListener listener) {
        mListener = listener;
        return this;
    }

    /**
     * 选中图片的监听器
     */
    public interface onSelectedListener {
        void onSelectedImage(String path);
    }

    private static class TransStatusBottomSheetDialog extends BottomSheetDialog {

        public TransStatusBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomSheetDialog(@NonNull Context context, int theme) {
            super(context, theme);
        }

        protected TransStatusBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final Window window = getWindow();
            if (window == null) {
                return;
            }

            // 得到屏幕的高度
//            int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
            int screenHeight = UiTool.getScreenHeight(getOwnerActivity());

            // 得到状态栏的高度
//            int statusHeight = (int) Ui.dipToPx(getContext().getResources(), 40);
            int statusHeight = UiTool.getStatusBarHeight(getOwnerActivity());

            Log.i("dong", "onCreate: screenHeight: " + screenHeight + " statusHeight: " + statusHeight);

            // 计算dialog的高度并设置
            int dialogHeight = screenHeight - statusHeight;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight <= 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);

        }
    }
}
