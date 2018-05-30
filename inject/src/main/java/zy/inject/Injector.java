package zy.inject;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyuan
 * created on 2017/7/26.
 */

public final class Injector {

    private static final Map<Class<?>, Constructor<?>> BINDINGS = new HashMap<>();

    private Injector() {
        throw new RuntimeException("Stub");
    }

    @UiThread
    public static void inject(@NonNull Activity activity) {
        doInject(activity, activity.getWindow().getDecorView());
    }

    @UiThread
    public static void inject(@NonNull Object target, @NonNull View view) {
        doInject(target, view);
    }

    private static void doInject(Object target, View source) {
        Class<?> hostClass = target.getClass();
        Constructor<?> constructor = findBindingConstructorForClass(hostClass);
        if (constructor == null) {
            return;
        }
        try {
            constructor.newInstance(target, source);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create binding instance.", cause);
        }
    }

    private static Constructor<?> findBindingConstructorForClass(Class<?> hostClass) {
        Constructor<?> constructor = BINDINGS.get(hostClass);
        if (constructor != null) {
            return constructor;
        }
        String className = hostClass.getName();
        if (className.startsWith("android.") || className.startsWith("java.")) {
            return null;
        }
        try {
            Class<?> bindingClass = hostClass.getClassLoader().loadClass(className + "_ViewBinding");
            constructor = bindingClass.getConstructor(hostClass, View.class);
        } catch (ClassNotFoundException e) {
            constructor = findBindingConstructorForClass(hostClass.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + hostClass, e);
        }
        BINDINGS.put(hostClass, constructor);
        return constructor;
    }

}
