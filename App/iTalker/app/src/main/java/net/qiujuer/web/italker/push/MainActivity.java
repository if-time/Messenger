package net.qiujuer.web.italker.push;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.qiujuer.web.italker.common.Common;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Common();
    }
}
