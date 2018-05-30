package zy.inject;

import android.support.annotation.IdRes;
import android.view.View;

/**
 * @author zhangyuan
 * created on 2018/5/30.
 */
public class InjectorUtils {

    public static <T> T findViewByIdAsType(View source, @IdRes int id, Class<T> clazz) {
        View view = findViewById(source, id);
        return castView(view, clazz);
    }

    private static View findViewById(View source, @IdRes int id) {
        return source.findViewById(id);
    }

    private static <T> T castView(View source, Class<T> clazz) {
        return clazz.cast(source);
    }
}
