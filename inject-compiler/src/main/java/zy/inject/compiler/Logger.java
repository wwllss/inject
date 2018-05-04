package zy.inject.compiler;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * @author zhangyuan
 * @date 2018/5/4.
 */
public class Logger {

    private Messager messager;

    public Logger(Messager messager) {
        this.messager = messager;
    }

    public void i(String str) {
        messager.printMessage(Diagnostic.Kind.NOTE, str);
    }

    public void e(String str) {
        messager.printMessage(Diagnostic.Kind.ERROR, str);
    }

}
