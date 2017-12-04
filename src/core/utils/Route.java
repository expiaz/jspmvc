package core.utils;

import core.http.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    String name() default "__DEFAULT__";
    String path() default "/";
    HttpMethod[] methods() default {HttpMethod.GET};
}
