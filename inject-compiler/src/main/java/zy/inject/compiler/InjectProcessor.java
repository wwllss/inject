package zy.inject.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import zy.inject.annotation.BindView;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;

@AutoService(Processor.class)
public class InjectProcessor extends AbstractProcessor {

    static final String ACTIVITY_TYPE = "android.app.Activity";

    private static final String VIEW_TYPE = "android.view.View";

    private Logger logger;

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        logger = new Logger(processingEnvironment.getMessager());
        filer = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<TypeElement, ClassViewBinding> classMap = new LinkedHashMap<>();
        for (Element element : roundEnvironment.getElementsAnnotatedWith(BindView.class)) {
            parseBindView(element, classMap);
        }
        for (Map.Entry<TypeElement, ClassViewBinding> entry : classMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            ClassViewBinding binding = entry.getValue();
            TypeElement parentType = findParentType(typeElement, classMap);
            if (parentType != null) {
                binding.setParentBinding(classMap.get(parentType));
            }
            JavaFile javaFile = new JavaFileWriter(binding).generate();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                error("generate file error for " + typeElement);
            }
        }
        return false;
    }

    private void parseBindView(Element element, Map<TypeElement, ClassViewBinding> classMap) {
        if (hasError(element)) {
            return;
        }
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        ClassViewBinding classViewBinding = classMap.get(enclosingElement);
        if (classViewBinding == null) {
            classMap.put(enclosingElement, classViewBinding = new ClassViewBinding(enclosingElement));
        }
        classViewBinding.addFieldBinding(new FieldViewBinding(element));
    }

    private boolean hasError(Element element) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.PRIVATE)) {
            error("%s.%s must not be static or private",
                    enclosingElement.getQualifiedName(), element.getSimpleName());
            return true;
        }
        if (enclosingElement.getKind() != CLASS) {
            error("@%s %s may only be contained in classes. (%s.%s)",
                    enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            return true;
        }
        if (enclosingElement.getModifiers().contains(Modifier.PRIVATE)) {
            error("annotation may not be contained in private class. (%s.%s)",
                    enclosingElement.getQualifiedName(), element.getSimpleName());
            return true;
        }
        TypeMirror elementType = element.asType();
        if (!isSubtypeOfType(elementType, VIEW_TYPE) && !isInterface(elementType)) {
            if (elementType.getKind() != TypeKind.ERROR) {
                error("@%s fields must extend from View or be an interface. (%s.%s)",
                        enclosingElement.getQualifiedName(), element.getSimpleName());
                return true;
            }
        }
        return false;
    }

    private TypeElement findParentType(TypeElement typeElement, Map<TypeElement, ClassViewBinding> classMap) {
        TypeMirror type;
        while (true) {
            type = typeElement.getSuperclass();
            if (type.getKind() == TypeKind.NONE) {
                return null;
            }
            typeElement = (TypeElement) ((DeclaredType) type).asElement();
            if (classMap.containsKey(typeElement)) {
                return typeElement;
            }
        }
    }

    static boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
        if (isTypeEqual(typeMirror, otherType)) {
            return true;
        }
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() > 0) {
            StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
            typeString.append('<');
            for (int i = 0; i < typeArguments.size(); i++) {
                if (i > 0) {
                    typeString.append(',');
                }
                typeString.append('?');
            }
            typeString.append('>');
            if (typeString.toString().equals(otherType)) {
                return true;
            }
        }
        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if (isSubtypeOfType(superType, otherType)) {
            return true;
        }
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (isSubtypeOfType(interfaceType, otherType)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInterface(TypeMirror typeMirror) {
        return typeMirror instanceof DeclaredType
                && ((DeclaredType) typeMirror).asElement().getKind() == INTERFACE;
    }

    private static boolean isTypeEqual(TypeMirror typeMirror, String otherType) {
        return otherType.equals(typeMirror.toString());
    }

    private void info(String msg, Object... args) {
        logger.i(String.format(msg, args));
    }

    private void error(String msg, Object... args) {
        logger.e(String.format(msg, args));
    }
}
