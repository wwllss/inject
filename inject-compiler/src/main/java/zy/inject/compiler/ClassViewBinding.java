package zy.inject.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.google.auto.common.MoreElements.getPackage;
import static zy.inject.compiler.InjectProcessor.ACTIVITY_TYPE;
import static zy.inject.compiler.InjectProcessor.isSubtypeOfType;

/**
 * @author zhangyuan
 * @date 2018/5/30.
 */
public class ClassViewBinding {

    private final TypeName targetType;

    private final ClassName bindingClassName;

    private final boolean isActivity;

    private final Set<FieldViewBinding> fieldViewBindingList;

    public ClassViewBinding(TypeElement enclosingElement) {
        TypeMirror typeMirror = enclosingElement.asType();
        isActivity = isSubtypeOfType(typeMirror, ACTIVITY_TYPE);
        targetType = TypeName.get(typeMirror);
        String packageName = getPackage(enclosingElement).getQualifiedName().toString();
        String className = enclosingElement.getQualifiedName().toString().substring(
                packageName.length() + 1).replace('.', '$');
        bindingClassName = ClassName.get(packageName, className + "_ViewBinding");
        fieldViewBindingList = new LinkedHashSet<>();
    }

    public Set<FieldViewBinding> getFieldViewBindingList() {
        return fieldViewBindingList;
    }

    public void addFieldBinding(FieldViewBinding fieldViewBinding) {
        fieldViewBindingList.add(fieldViewBinding);
    }

    public TypeName getTargetType() {
        return targetType;
    }

    public ClassName getBindingClassName() {
        return bindingClassName;
    }

    public boolean isActivity() {
        return isActivity;
    }
}
