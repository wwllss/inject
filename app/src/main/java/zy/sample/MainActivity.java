package zy.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import zy.inject.Injector;
import zy.inject.annotation.BindView;
import zy.inject.annotation.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.hello)
    TextView hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Injector.inject(this);
        hello.setText("Inject Success");
    }
}
