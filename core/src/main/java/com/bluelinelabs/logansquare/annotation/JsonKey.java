package com.bluelinelabs.logansquare.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Declare a field that is a key for this Java object.
 * <pre><code>
 * {@literal @}JsonObject
 * public class MyClass {
 *     ...
 * }
 * </code></pre>
 *
 * @author Krzysztof Miemiec
 */
@Target(FIELD)
@Retention(CLASS)
public @interface JsonKey {
}
