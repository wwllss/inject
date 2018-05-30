package zy.sample;

import android.os.Bundle;

import zy.inject.Injector;

/**
 * @author zhangyuan
 * @date 2018/5/30.
 */
public class SubActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Injector.inject(this);
        hello.setText("Sub Inject Success");
    }
}
