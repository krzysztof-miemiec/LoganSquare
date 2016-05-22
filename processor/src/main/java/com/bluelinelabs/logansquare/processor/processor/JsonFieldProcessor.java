package com.bluelinelabs.logansquare.processor.processor;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonIgnore.IgnorePolicy;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.processor.JsonFieldHolder;
import com.bluelinelabs.logansquare.processor.JsonObjectHolder;
import com.bluelinelabs.logansquare.processor.TextUtils;
import com.bluelinelabs.logansquare.processor.TypeUtils;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.lang.model.element.Modifier.PRIVATE;

public class JsonFieldProcessor extends Processor {

    public JsonFieldProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public Class getAnnotation() {
        return JsonField.class;
    }

    @Override
    public void findAndParseObjects(RoundEnvironment env, Map<String, JsonObjectHolder> jsonObjectMap, Elements elements, Types types) {
        for (Element element : env.getElementsAnnotatedWith(JsonField.class)) {
            try {
                processJsonFieldAnnotation(element, jsonObjectMap, elements, types);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));

                error(element, "Unable to generate injector for %s. Stack trace incoming:\n%s", JsonField.class, stackTrace.toString());
            }
        }
    }

    private void processJsonFieldAnnotation(Element element, Map<String, JsonObjectHolder> jsonObjectMap, Elements elements, Types types) {
        if (!isJsonFieldFieldAnnotationValid(element, elements)) {
            return;
        }

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        JsonObjectHolder objectHolder = jsonObjectMap.get(TypeUtils.getInjectedFQCN(enclosingElement, elements));
        JsonFieldHolder fieldHolder = objectHolder.fieldMap.get(element.getSimpleName().toString());

        if (fieldHolder == null) {
            fieldHolder = new JsonFieldHolder();
            objectHolder.fieldMap.put(element.getSimpleName().toString(), fieldHolder);

        }

        JsonField annotation = element.getAnnotation(JsonField.class);

        TypeMirror typeConverterType;
        try {
            typeConverterType = mProcessingEnv.getElementUtils().getTypeElement(annotation.typeConverter().getCanonicalName()).asType();
        } catch (MirroredTypeException mte) {
            typeConverterType = mte.getTypeMirror();
        }

        boolean isSetter = false, isGetter = false;

        if (element.getKind() == ElementKind.METHOD) {
            ExecutableElement executableElement = (ExecutableElement) element;
            isSetter = executableElement.getParameters().size() == 1;
            isGetter = executableElement.getParameters().size() == 0;
        }

        String[] fieldName = annotation.name();

        JsonIgnore ignoreAnnotation = element.getAnnotation(JsonIgnore.class);
        boolean shouldParse = !isGetter && (ignoreAnnotation == null || ignoreAnnotation.ignorePolicy() == IgnorePolicy.SERIALIZE_ONLY);
        boolean shouldSerialize = !isSetter && (ignoreAnnotation == null || ignoreAnnotation.ignorePolicy() == IgnorePolicy.PARSE_ONLY);
        boolean isKey = annotation.isKey();
        boolean inherits = annotation.inherits();

        String error = fieldHolder.fill(element, elements, types, fieldName, typeConverterType, objectHolder, shouldParse, shouldSerialize, isKey, inherits);
        if (!TextUtils.isEmpty(error)) {
            error(element, error);
        }

        ensureTypeConverterClassValid(typeConverterType, elements, types);
    }

    private boolean isJsonFieldFieldAnnotationValid(Element element, Elements elements) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        Annotation objectAnnotation = enclosingElement.getAnnotation(JsonObject.class);
        if (objectAnnotation == null) {
            error(enclosingElement, "%s: @%s fields can only be in classes annotated with @%s.", enclosingElement.getQualifiedName(), JsonField.class.getSimpleName(), JsonObject.class.getSimpleName());
            return false;
        }

        if (element.getKind() == ElementKind.FIELD && element.getModifiers().contains(PRIVATE) && (TextUtils.isEmpty(JsonFieldHolder.getGetter(element, elements)) || TextUtils.isEmpty(JsonFieldHolder.getSetter(element, elements)))) {
            error(element, "@%s annotation can only be used on private fields if both getter and setter are present.", JsonField.class.getSimpleName());
            return false;
        } else if (element.getKind() == ElementKind.METHOD) {
            ExecutableElement executableElement = (ExecutableElement) element;
            String[] names = element.getAnnotation(JsonField.class).name();
            if (names.length == 0) {
                error(element, "@%s annotations used on methods require name to be present.", JsonField.class.getSimpleName());
            }
            int parameterCount = executableElement.getParameters().size();
            if (parameterCount == 0) {
                if (executableElement.getReturnType().getKind() == TypeKind.VOID) {
                    error(element, "Getter methods annotated as @%s must return non-void value.", JsonField.class.getSimpleName());
                }
            } else if (parameterCount > 1) {
                error(element, "Setter methods annotated as @%s must have exactly one parameter.", JsonField.class.getSimpleName());
            }
        }

        return true;
    }

    private boolean ensureTypeConverterClassValid(TypeMirror typeConverterClassMirror, Elements elements, Types types) {
        TypeElement typeConverterElement = elements.getTypeElement(typeConverterClassMirror.toString());

        if (typeConverterElement != null) {
            boolean isTypeConverterType = false;
            TypeElement element = typeConverterElement;

            while (!isTypeConverterType && element != null) {
                for (TypeMirror iface : element.getInterfaces()) {
                    if (types.erasure(iface).toString().equals(TypeConverter.class.getCanonicalName())) {
                        isTypeConverterType = true;
                    }
                }

                TypeMirror superClassMirror = element.getSuperclass();
                if (superClassMirror != null) {
                    superClassMirror = types.erasure(superClassMirror);

                    element = elements.getTypeElement(superClassMirror.toString());
                } else {
                    element = null;
                }
            }

            if (!isTypeConverterType) {
                error(element, "TypeConverter elements must implement the TypeConverter interface or extend from one of the convenience helpers (ie StringBasedTypeConverter or DateTypeConverter).");
                return false;
            }

            boolean constructorIsDeclared = false;
            boolean hasAccessibleConstructor = false;
            List<? extends Element> enclosedElements = typeConverterElement.getEnclosedElements();
            for (Element enclosedElement : enclosedElements) {
                ElementKind enclosedElementKind = enclosedElement.getKind();
                if (enclosedElementKind == ElementKind.CONSTRUCTOR) {
                    constructorIsDeclared = true;
                    if (!enclosedElement.getModifiers().contains(Modifier.PRIVATE)) {
                        ExecutableElement executableElement = (ExecutableElement) enclosedElement;

                        if (executableElement.getParameters().size() == 0) {
                            hasAccessibleConstructor = true;
                        }
                    }
                }
            }

            if (constructorIsDeclared && !hasAccessibleConstructor) {
                error(element, "TypeConverter classes must have a non-private zero argument constructor.");
                return false;
            }
        }

        return true;
    }

}
