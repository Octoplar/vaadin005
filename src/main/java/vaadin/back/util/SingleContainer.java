package vaadin.back.util;

/**
 * Created by Octoplar on 14.05.2017.
 */
public class SingleContainer<T> {
    private T value;

    public SingleContainer(T value) {
        this.value = value;
    }

    public SingleContainer() {
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
