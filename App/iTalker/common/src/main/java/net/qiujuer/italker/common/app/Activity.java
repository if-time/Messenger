package net.qiujuer.italker.common.app;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import butterknife.ButterKnife;

/**
 * @author dong
 */
public abstract class Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ActivityCompat.requestPermissions(this, new String[]{
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);
//        }

        // 在界面未初始化之前调用的初始化窗口
        initWindows();

        if (initArgs(getIntent().getExtras())) {
            // 得到界面ID并设置到Activity界面中
            int layId = getContentLayoutId();
            setContentView(layId);
            initWidget();
            initData();
        } else {
            finish();
        }

    }

    /**
     * 初始化窗口
     */
    protected void initWindows() {

    }

    /**
     * 初始化相关参数
     * @param bundle 参数bundle
     * @return 如果参数正确返回True，错误返回False
     */
    protected boolean initArgs(Bundle bundle) {

        return true;
    }

    /**
     * 得到当前界面的资源文件Id
     * @return 资源文件Id
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        // 当点击界面导航返回时，Finish当前界面
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // 得到当前Activity下的所有Fragment
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                // 判断是否为我们能够处理的Fragment类型
                if (fragment instanceof net.qiujuer.italker.common.app.Fragment) {
                    // 判断是否拦截了返回按钮
                    if (((net.qiujuer.italker.common.app.Fragment) fragment).onBackPressed()) {
                        // 如果有直接Return
                        return;
                    }
                }

            }
        }
        super.onBackPressed();
        finish();
    }
}
