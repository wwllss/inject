package zy.inject.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.Set;

import javax.lang.model.element.Modifier;

/**
 * @author zhangyuan
 * created on 2018/5/30.
 */
class JavaFileWriter {

    private static final ClassName UI_THREAD =
            ClassName.get("android.support.annotation", "UiThread");

    private static final ClassName VIEW =
            ClassName.get("android.view", "View");

    private static final ClassName INJECTOR_UTILS =
            ClassName.get("zy.inject", "InjectorUtils");

    private final ClassViewBinding binding;

    JavaFileWriter(ClassViewBinding binding) {
        this.binding = binding;
    }

    public JavaFile generate() {
        return JavaFile.builder(binding.getBindingClassName().packageName(), crateType())
                .addFileComment("Generated code. Do not modify!")
                .build();
    }

    private TypeSpec crateType() {
        TypeSpec.Builder result = TypeSpec.classBuilder(binding.getBindingClassName().simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addField(binding.getTargetType(), "target", Modifier.PRIVATE);
        if (binding.isActivity()) {
            result.addMethod(createConstructorForActivity());
        }
        result.addMethod(createConstructor());
        return result.build();
    }

    private MethodSpec createConstructorForActivity() {
        return MethodSpec.constructorBuilder()
                .addAnnotation(UI_THREAD)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(binding.getTargetType(), "target")
                .addStatement("this(target, target.getWindow().getDecorView())")
                .build();
    }

    private MethodSpec createConstructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addAnnotation(UI_THREAD)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(binding.getTargetType(), "target", Modifier.FINAL)
                .addParameter(VIEW, "source")
                .addStatement("this.target = target");
        addBindField(builder);
        return builder.build();
    }

    private void addBindField(MethodSpec.Builder builder) {
        Set<FieldViewBinding> fieldViewBindingList = binding.getFieldViewBindingList();
        for (FieldViewBinding fieldViewBinding : fieldViewBindingList) {
            builder.addStatement("target.$L = $T.findViewByIdAsType(source, $L, $T.class)",
                    fieldViewBinding.getSimpleName(),
                    INJECTOR_UTILS,
                    fieldViewBinding.getId(),
                    fieldViewBinding.getRawType());
        }
    }

}
