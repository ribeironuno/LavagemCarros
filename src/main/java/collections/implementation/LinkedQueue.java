package collections.implementation;

import collections.interfaces.QueueADT;

import java.util.NoSuchElementException;

/**
 * Class that implements a LinkedQueue.
 */
public class LinkedQueue<T> implements QueueADT<T> {

    /**
     * First node of chain.
     */
    private Node<T> front;

    /**
     * Back node of chain.
     */
    private Node<T> rear;

    /**
     * Size of queue.
     */
    private int size;

    public LinkedQueue() {
        this.front = this.rear = null;
        this.size = 0;
    }

    @Override
    public void enqueue(T element) {
        Node<T> newNode = new Node<>(element);
        if (this.isEmpty()) {
            this.front = this.rear = newNode;
        } else {
            this.rear.setNext(newNode);
            this.rear = newNode;
        }
        this.size++;
    }

    @Override
    public T dequeue() throws NoSuchElementException {
        if (this.isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        T result = this.front.getData();
        if (this.size == 1) {
            this.front = this.rear = null;
        } else {
            this.front = this.front.getNext();
        }
        this.size--;
        return result;
    }

    @Override
    public T first() throws NoSuchElementException {
        if (this.isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return this.front.getData();
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public String toString() {
        String string = "";
        if (this.isEmpty()) {
            string = "Queue is empty!";
        } else {
            Node<T> current = this.front;
            while (current != null) {
                string += current.getData().toString() + " ";
                current = current.getNext();
            }
        }
        return string;
    }
}
