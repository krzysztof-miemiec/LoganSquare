package com.bluelinelabs.logansquare.processor;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Ignore;
import org.junit.Test;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class NonPrivateFieldsDetectionPolicyTest {

    @Test
    @Ignore
    public void generatedSource() {
        ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("model/good/NonPrivateFieldsFieldDetectionPolicyModel.java"))
                .processedWith(new JsonAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects.forResource("generated/NonPrivateFieldsFieldDetectionPolicyModel$$JsonObjectMapper.java"));
    }
}
