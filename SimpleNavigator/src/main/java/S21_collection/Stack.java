package S21_collection;

import java.util.NoSuchElementException;

public class Stack<T> implements Collection<T>{
  private Node<T> top;
  private int size;
  
  public Stack() {
	  top = null;
	  size = 0;
  }
  @Override
  public void push(T value) {
	  Node<T> node = new Node<>(value);
	  node.setNext(top);
	  top = node;
	  size++;
  }
  @Override
  public T pop() {

	  T value = top.getData();
	  top = top.getNext();
	  size--;
	  return value;
  }
  @Override
  public T top() {
	  if(isEmpty()) {
		  throw new NoSuchElementException("Stack is empty");
	  } 
	  return top.getData();
  }
  @Override
  public int getSize() {
	  return size;
  }
  @Override
  public boolean isEmpty() {
	return size == 0;
  }
}