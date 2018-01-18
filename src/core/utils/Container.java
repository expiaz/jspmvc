package core.utils;

import core.FrontController;
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

    public void global(Object o) {
        this.singleton(o.getClass(), o);
    }

    public Object get(Class clazz) {
        try {
            return this.get(clazz.getName());
        } catch (ClassNotFoundException e) {
            FrontController.die(Container.class, e);
            return null;
        }
    }

    public Object get(String name) throws ClassNotFoundException {
        return this.get(name, false);
    }

    public Object get(String name, boolean factory) throws ClassNotFoundException {
        if (factory) {
            // doesn't exists
            if (! this.factories.containsKey(name)) {
                /*try {
                    this.factory(name, container -> {
                        try {
                            return container.resolve(Class.forName(name));
                        } catch (Exception e) {
                            FrontController.die(Container.class, e);
                            return null;
                        }
                    });
                } catch (Exception e) {
                    throw new ClassNotFoundException(name + " isn't a known factory\n\n" + e.getMessage());
                }*/
                throw new ClassNotFoundException(name + " isn't a known factory");
                // return null;
            }

            Factory f = this.factories.get(name);
            Object o = f.create(this);
            return o;
        }

        // key does not exists
        if (!this.singletons.containsKey(name)) {
            // if factories got it, create it from it
            if(this.factories.containsKey(name)) {
               this.singleton(name, this.get(name, true));
            } else { // try to resolve it from the given name
                try {
                    this.singleton(name, this.resolve(Class.forName(name)));
                } catch (Exception e) {
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

    public Object resolve(Class clazz, Map<String, Object> arguments)
            throws ClassNotFoundException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        for (Constructor constructor : clazz.getDeclaredConstructors()) {
            Object[] resolved = new Object[constructor.getParameterCount()];
            // number of the actual parameter
            int i = 0;
            // informations about the DI of the current parameter
            Inject infos;
            for (Parameter parameter : constructor.getParameters()) {

                if(! parameter.isAnnotationPresent(Inject.class)) {
                    break;
                }

                infos = parameter.getAnnotation(Inject.class);

                String key = infos.key().equals("__DEFAULT__")
                        ? parameter.getType().getName()
                        : infos.key();

                if(arguments.containsKey(key)) {
                    resolved[i++] = arguments.get(key);
                } else {
                    resolved[i++] = this.get(key, infos.factory());
                }

            }
            // resolved all the parameters (the else break will fail this test)
            if (i == constructor.getParameterCount()) {
                // return the instance
                return constructor.newInstance(resolved);
            }
        }
        // no constructor found => fail
        throw new ClassNotFoundException(clazz.getName() + " isn't possible to resolve class");
    }

}
