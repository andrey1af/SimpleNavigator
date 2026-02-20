package S21_collection;

public class Node<T> {
    private Node<T> next;
    T data;
    public Node(T value) {
        next = null;
        data = value;
    }
    public Node<T> getNext() {
        return next;
    }
    public void setNext(Node<T> node) {
        next = node;
    }
    public T getData() {
        return data;
    }
    public void setData(T value) {
        data = value;
    }

}
