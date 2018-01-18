package core.utils;

public interface Fetcher<T extends Fetchable> {
    T fetch(Object value);
}
