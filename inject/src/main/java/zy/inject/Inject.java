package zy.inject;

import android.app.Activity;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyuan
 * @date 2017/7/26.
 */

public final class Inject {

    private static final Map<String, Injector> CACHE_FINDER = new HashMap<>();

    private Inject() {
        throw new RuntimeException("no instance");
    }

    public static void inject(Activity activity) {
        inject(activity, activity, Finder.ACTIVITY);
    }

    public static void inject(Object host, View view) {
        inject(host, view, Finder.VIEW);
    }

    @SuppressWarnings("all")
    private static void inject(Object host, Object source, Finder finder) {
        String className = host.getClass().getName();
        try {
            Injector injector = CACHE_FINDER.get(className);
            if (injector == null) {
                Class<?> finderClass = Class.forName(className + "$$Injector");
                injector = (Injector) finderClass.newInstance();
                injector.inject(host, source, finder);
                CACHE_FINDER.put(className, injector);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

}
