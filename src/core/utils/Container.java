package core.utils;

import core.annotations.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class Container {

    private Map<String, Object> singletons;
    private Map<String, Factory> factories;

    public Container() {
        this.singletons = new HashMap<>();
        this.factories = new HashMap<>();

        this.singleton(this.getClass(), this);
    }

    public void factory(String name, Factory factory) {
        this.factories.put(name, factory);
    }

    public void singleton(String name, Object o) {
        this.singletons.put(name, o);
    }

    public void factory(Class clazz, Factory factory) {
        this.factory(clazz.getName(), factory);
    }

    public void singleton(Class clazz, Object o) {
        this.singleton(clazz.getName(), o);
    }

    public Object get(Class clazz) {
        try {
            return this.get(clazz.getName());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public Object get(String name) throws ClassNotFoundException {
        return this.get(name, false);
    }

    public Object get(String name, boolean factory) throws ClassNotFoundException {
        if (factory) {
            if (!this.factories.containsKey(name)) {
                try {
                    this.factory(name, container -> {
                        try {
                            return container.resolve(Class.forName(name));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    });
                } catch (Exception e) {
                    throw new ClassNotFoundException(name + " isn't a known factory");
                }
            }

            return this.factories.get(factory).create(this);
        }

        if (!this.singletons.containsKey(name)) {
            try {
                this.singleton(name, this.get(name, true));
            } catch (ClassNotFoundException e) {
                try {
                    this.singleton(name, this.resolve(Class.forName(name)));
                } catch (Exception e2) {
                    throw new ClassNotFoundException(name + " isn't a known singleton");
                }
            }
        }

        return this.singletons.get(name);
    }

    public Object resolve(Class clazz)
            throws ClassNotFoundException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        return this.resolve(clazz, new HashMap<>());
    }

    public Object resolve(Class clazz, Map<Class, Object> arguments)
            throws ClassNotFoundException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        for (Constructor constructor : clazz.getDeclaredConstructors()) {
            Object[] resolved = new Object[constructor.getParameterCount()];
            int i = 0;
            Inject infos;
            for (Parameter parameter : constructor.getParameters()) {

                if(arguments.containsKey(parameter.getType())) {

                    resolved[i++] = arguments.get(parameter.getType());

                } else if(parameter.isAnnotationPresent(Inject.class)) {

                    infos = parameter.getAnnotation(Inject.class);
                    resolved[i++] = this.get(
                            infos.key().equals("__DEFAULT__")
                                    ? parameter.getType().getName()
                                    : infos.key(),
                            infos.factory()
                    );

                } else {
                    break;
                }
            }
            if (i == constructor.getParameterCount()) {
                // return the instance
                return constructor.newInstance(resolved);
            }
        }
        // no constructor found => fail
        throw new ClassNotFoundException(clazz.getName() + " isn't possible to resolve class");
    }

}
