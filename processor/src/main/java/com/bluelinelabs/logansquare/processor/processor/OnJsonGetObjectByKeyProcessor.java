package com.bluelinelabs.logansquare.processor.processor;

import com.bluelinelabs.logansquare.annotation.JsonGetByKey;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.processor.JsonObjectHolder;
import com.bluelinelabs.logansquare.processor.TypeUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class OnJsonGetObjectByKeyProcessor extends MethodProcessor {

    public OnJsonGetObjectByKeyProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public Class getAnnotation() {
        return JsonGetByKey.class;
    }

    @Override
    public void findAndParseObjects(RoundEnvironment env, Map<String, JsonObjectHolder> jsonObjectMap, Elements elements, Types types) {
        for (Element element : env.getElementsAnnotatedWith(JsonGetByKey.class)) {
            try {
                processOnPreJsonSerializeMethodAnnotation(element, jsonObjectMap, elements);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));

                error(element, "Unable to generate injector for %s. Stack trace incoming:\n%s", JsonGetByKey.class, stackTrace.toString());
            }
        }
    }

    private void processOnPreJsonSerializeMethodAnnotation(Element element, Map<String, JsonObjectHolder> jsonObjectMap, Elements elements) throws Exception {
        if (!isCallbackMethodAnnotationValid(element, JsonGetByKey.class.getSimpleName())) {
            return;
        }

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        ExecutableElement executableElement = (ExecutableElement) element;
        JsonObjectHolder objectHolder = jsonObjectMap.get(TypeUtils.getInjectedFQCN(enclosingElement, elements));
        objectHolder.getObjectByKeyCallback = executableElement.getSimpleName().toString();
    }

    @Override
    public boolean isCallbackMethodAnnotationValid(Element element, String annotationName) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        if (enclosingElement.getAnnotation(JsonObject.class) == null) {
            error(enclosingElement, "%s: @%s methods can only be in classes annotated with @%s.", enclosingElement.getQualifiedName(), annotationName, JsonObject.class.getSimpleName());
            return false;
        }

        ExecutableElement executableElement = (ExecutableElement) element;
        if (executableElement.getParameters().size() != 1) {
            error(element, "%s: @%s methods must take exactly one parameter.", enclosingElement.getQualifiedName(), annotationName);
            return false;
        }

        List<? extends Element> allElements = enclosingElement.getEnclosedElements();
        int methodInstances = 0;
        for (Element enclosedElement : allElements) {
            for (AnnotationMirror am : enclosedElement.getAnnotationMirrors()) {
                if (am.getAnnotationType().asElement().getSimpleName().toString().equals(annotationName)) {
                    methodInstances++;
                }
            }
        }
        if (methodInstances != 1) {
            error(element, "%s: There can only be one @%s method per class.", enclosingElement.getQualifiedName(), annotationName);
            return false;
        }

        return true;
    }
}
