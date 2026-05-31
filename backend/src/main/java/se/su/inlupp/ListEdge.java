package se.su.inlupp;

// PROG2 VT2026, Inlämningsuppgift, del 1
// Adam McCarthy - admc0801
// Joakim Lindé - joli3174
// Arvid Flodin - arfl0534

public class ListEdge<T> implements Edge<T> {
    private final T destination;
    private final String name;
    private int weight;

    public ListEdge(T destination, String name, int weight) {
        this.destination = destination;
        this.name = name;
        this.weight = weight;
    }

    public T getDestination() {
        return destination;
    }

    public void setWeight(int weight) throws IllegalArgumentException {
        if (weight < 0) throw new IllegalArgumentException();
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format("till %s med %s tar %s", this.destination, this.name, this.weight);
    }
}
