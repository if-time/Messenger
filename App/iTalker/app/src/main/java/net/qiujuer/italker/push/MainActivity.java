package net.qiujuer.italker.push;

import android.widget.TextView;

import net.qiujuer.italker.common.app.Activity;

import butterknife.BindView;

public class MainActivity extends Activity {
    @BindView(R.id.txt_test)
    TextView textView;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        textView.setText("Test Hello.");
    }
}
