package core.utils;

public interface Fetcher<T> {
    T fetch(Object value);
}
