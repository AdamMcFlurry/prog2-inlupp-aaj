package se.su.inlupp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListPath<T> implements Path<T> {
    private final List<Edge<T>> edgeList = new ArrayList<>();
    private final T start;

    public ListPath(T start) {
        this.start = start;
    }

    //får man ha en till public method här?
    public void addEdge(Edge<T> edge) {
        edgeList.add(edge);
    }

    @Override
    public T getStart() {
        // return edgeList.get(0).getDestination();
        return start;
    }

    @Override
    public T getEnd() {
        int lastIndex = edgeList.size()-1;
        return edgeList.get(lastIndex).getDestination();
    }

    @Override
    public int getTotalWeight() {
        int totalWeight = 0;
        //edgeList.forEach((e)->totalWeight += e.getWeight());
        for (Edge<T> edge : edgeList) {
            totalWeight += edge.getWeight();
        }
        return totalWeight;
    }

    @Override
    public List<Edge<T>> getEdges() {
        return edgeList;
    }

    @Override
    public List<T> getNodes() {
        List<T> allNodes = new ArrayList<>(List.of(start));
        for (Edge<T> edge : edgeList) {
            allNodes.add(edge.getDestination());
        }
        return allNodes;
    }
    
    @Override
    public Iterator<Edge<T>> iterator() {
        Iterator<Edge<T>> it = getEdges().iterator();
        return it;
    }
    
    @Override
    public String toString() {
        StringBuilder sB = new StringBuilder();
        Iterator<Edge<T>> it = iterator();
        sB.append(getStart() + " ");
        while (it.hasNext()) {
            sB.append(it.next().toString());
        }
        return sB.toString();
    }
}
