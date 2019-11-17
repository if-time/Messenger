package net.qiujuer.italker.push;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.qiujuer.italker.common.app.Activity;
import net.qiujuer.italker.push.activities.MainActivity;
import net.qiujuer.italker.push.frags.assist.PermissionFragment;

public class LaunchActivity extends Activity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PermissionFragment.haveAll(this, getSupportFragmentManager())) {
            MainActivity.show(this);
            finish();
        }
    }
}
