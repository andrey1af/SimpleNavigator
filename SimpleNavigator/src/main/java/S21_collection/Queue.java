package S21_collection;
import java.util.NoSuchElementException;

public class Queue<T> implements Collection<T>{
    private Node<T> last;
    private Node<T> first;
    int size;
    public Queue() {
        last = null;
        first = null;
        size = 0;
    }
    @Override
    public void push(T value) {
        Node<T> node = new Node<>(value);
        if(first == null) {
            first = node;
        }
        if(last != null) {
            last.setNext(node);
        }
        size++;
        last = node;
    }
    @Override
    public T pop() {

        T data = top();
        first = first.getNext();
        if(first == null) {
            last = null;
        }
        size--;
        return data;
    }
    @Override
    public T top() {
        if(isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return first.getData();
    }

    public T front() {
        return top();
    }

    public T back() {
        if(isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return last.getData();
    }
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    @Override
    public int getSize() {
        return size;
    }
}
