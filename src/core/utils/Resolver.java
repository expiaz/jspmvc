package core.utils;

import sun.reflect.Reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Resolver {

    public static List<Method> findByAnnotation(Class c, Class<? extends Annotation> a) {
        List<Method> methods = new ArrayList<>();
        for(Method m : c.getMethods()) {
            if(m.isAnnotationPresent(a)) {
                methods.add(m);
            }
        }
        return methods;
    }

}
