package S21_collection;

import java.util.NoSuchElementException;

public interface Collection<T> {
    void push(T value);
    T pop();
    T top();
    boolean isEmpty();
    int getSize();
}
