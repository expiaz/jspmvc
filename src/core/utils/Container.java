package core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Container {

    private Map<String, Object> singletons;
    private Map<String, Factory> factories;

    public Container(){
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
        if(factory) {
            if(! this.factories.containsKey(name)){
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

        if(! this.singletons.containsKey(name)) {
            try{
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
            InvocationTargetException, InstantiationException
    {
        List<Parameter> toInject;
        for(Constructor constructor : clazz.getDeclaredConstructors()) {
            toInject = new ArrayList<>();
            for(Parameter parameter : constructor.getParameters()) {
                if(parameter.isAnnotationPresent(Inject.class)) {
                    toInject.add(parameter);
                }
            }
            if(toInject.size() == constructor.getParameterCount()) {
                Object[] resolved = new Object[constructor.getParameterCount()];
                int i = 0;
                //resolve the constructor
                for (Parameter p : toInject) {
                    Inject infos = p.getAnnotation(Inject.class);
                    String key = infos.key();
                    if(key.equals("__DEFAULT__")) {
                        key = p.getType().getName();
                    }
                    resolved[i] = this.get(key, infos.factory());
                    i++;
                }
                // return the instance
                return constructor.newInstance(resolved);
            }
        }
        // no constructor found => fail
        throw new ClassNotFoundException(clazz.getName() + " isn't possible to resolve class");
    }

}
