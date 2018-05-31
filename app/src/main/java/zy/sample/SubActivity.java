package zy.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import zy.inject.Injector;
import zy.inject.annotation.BindView;

/**
 * @author zhangyuan
 * created on 2018/5/30.
 */
public class SubActivity extends MainActivity {

    @BindView(R.id.hello2)
    TextView hello2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
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
