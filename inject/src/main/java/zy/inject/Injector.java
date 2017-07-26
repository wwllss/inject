package zy.inject;

/**
 * @author zhangyuan
 * @date 2017/7/26.
 */

public interface Injector<T> {

    void inject(T host, Object source, Finder finder);

}
