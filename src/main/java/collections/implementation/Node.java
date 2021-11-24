package collections.implementation;

/**
 * Class that represents a single node.
 *
 * @param <T> Type being stored.
 */
public class Node<T> {

    /**
     * Next node.
     */
    private Node<T> next;

    /**
     * Reference to element's data.
     */
    private T data;

    /**
     * Creates an instance of Node.
     *
     * @param next Next node.
     * @param data Data being stored.
     */
    public Node(Node<T> next, T data) {
        this.next = next;
        this.data = data;
    }

    /**
     * Creates an instance of Node with next node null.
     *
     * @param data Data being stored.
     */
    public Node(T data) {
        this(null, data);
    }

    /**
     * Update data.
     *
     * @param data New data.
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Update next node.
     *
     * @param next New next node.
     */
    public void setNext(Node<T> next) {
        this.next = next;
    }

    /**
     * Returns element's data.
     *
     * @return Value stored.
     */
    public T getData() {
        return this.data;
    }

    /**
     * Returns next node.
     *
     * @return Next node.
     */
    public Node<T> getNext() {
        return this.next;
    }
}
