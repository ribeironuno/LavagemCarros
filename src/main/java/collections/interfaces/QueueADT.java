package collections.interfaces;

import java.util.NoSuchElementException;

public interface QueueADT<T> {
    /**
     * Adds one element to the rear of this queue.
     *
     * @param element the element to be added to
     *                the rear of this queue
     */
    public void enqueue(T element);

    /**
     * Removes and returns the element at the front of
     * this queue.
     *
     * @return the element at the front of this queue
     * @throws NoSuchElementException if queue is empty
     */
    public T dequeue() throws NoSuchElementException;

    /**
     * Returns without removing the element at the front of
     * this queue.
     *
     * @return the first element in this queue
     * @throws NoSuchElementException if queue is empty
     */
    public T first() throws NoSuchElementException;

    /**
     * Returns true if this queue contains no elements.
     *
     * @return true if this queue is empty
     */
    public boolean isEmpty();

    /**
     * Returns the number of elements in this queue.
     *
     * @return the integer representation of the size
     * of this queue
     */
    public int size();

    /**
     * Returns a string representation of this queue.
     *
     * @return the string representation of this queue
     */
    public String toString();
}
