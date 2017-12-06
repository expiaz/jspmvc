package core.utils;

import java.lang.reflect.InvocationTargetException;

public interface Factory {
    Object create(Container container);
}
