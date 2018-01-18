package core.utils;

public interface Factory<T> {
    T create(Container container);
}
