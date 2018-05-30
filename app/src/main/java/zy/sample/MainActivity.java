package zy.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import zy.inject.Injector;
import zy.inject.annotation.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.hello)
    TextView hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Injector.inject(this);
        hello.setText("Inject Success");
        hello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.startActivity(MainActivity.this,
                        new Intent(MainActivity.this, SubActivity.class), null);
            }
        });
    }
}
