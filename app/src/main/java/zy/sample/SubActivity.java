package zy.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
        hello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubActivity.this, InterfaceViewActivity.class);
                startActivity(intent);
            }
        });
    }
}
