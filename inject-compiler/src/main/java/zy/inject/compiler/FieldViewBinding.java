package zy.inject.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;

import zy.inject.annotation.BindView;

/**
 * @author zhangyuan
 * @date 2018/5/30.
 */
public class FieldViewBinding {

    private final String simpleName;

    private final TypeName type;

    private final int id;

    public FieldViewBinding(Element element) {
        this.simpleName = element.getSimpleName().toString();
        this.id = element.getAnnotation(BindView.class).value();
        this.type = TypeName.get(element.asType());
    }

    public ClassName getRawType() {
        return (ClassName) type;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public TypeName getType() {
        return type;
    }

    public int getId() {
        return id;
    }
}
