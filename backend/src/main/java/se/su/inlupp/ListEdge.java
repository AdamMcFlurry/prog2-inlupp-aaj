package se.su.inlupp;

public class ListEdge<T> implements Edge<T> {
    private T destination;
    private String name;
    private int weight;

    public ListEdge(T destination, String name, int weight) {
        this.destination = destination;
        this.name = name;
        this.weight = weight;
    }

    public T getDestination() {
        return destination;
    }

    public void setWeight(int weight) {
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
        return String.format("%s (%s: %f)", this.destination, this.name, this.weight);
    }
}
