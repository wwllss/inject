package zy.inject;

import android.app.Activity;
import android.view.View;

/**
 * @author zhangyuan
 * @date 2017/7/26.
 */

public enum Finder {

    ACTIVITY() {
        @Override
        View findViewById(Object source, int id) {
            return ((Activity) source).findViewById(id);
        }
    },
    VIEW() {
        @Override
        View findViewById(Object source, int id) {
            return ((View) source).findViewById(id);
        }
    };

    View findViewById(Object source, int id) {
        return null;
    }
}
