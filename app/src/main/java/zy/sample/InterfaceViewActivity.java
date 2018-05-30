package zy.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import zy.inject.Injector;
import zy.inject.annotation.BindView;

/**
 * @author zhangyuan
 * @date 2018/5/30.
 */
public class InterfaceViewActivity extends AppCompatActivity {

    @BindView(R.id.interface_text_view)
    ViewInterface viewInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface);
        Injector.inject(this);
        if (viewInterface instanceof CustomTextView) {
            ((CustomTextView) viewInterface).setText("Interface View");
        }
    }
}
