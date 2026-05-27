package se.su.inlupp;

import java.util.*;

public class ListGraph<T> implements Graph<T> {
  private final Map<T, Set<Edge<T>>> nodeEdgeMap = new HashMap<>();

  @Override
  public void add(T node) {
    nodeEdgeMap.putIfAbsent(node, new HashSet<>());
  }

  @Override
  public void remove(T node) throws NoSuchElementException {
    if (!hasNode(node)) throw new NoSuchElementException();
    
    nodeEdgeMap.remove(node);
    //Behövs ett Set här i mappen?
    Map<T, Edge<T>> toBeRemoved = new HashMap<>();
    
    for (Map.Entry<T, Set<Edge<T>>> i : nodeEdgeMap.entrySet()) {
      for (Edge<T> edge : i.getValue()) {
        if (edge.getDestination().equals(node)) toBeRemoved.put(i.getKey(), edge);
      }
    }

    for(Map.Entry<T, Edge<T>> TBRSet : toBeRemoved.entrySet()) {
      nodeEdgeMap.get(TBRSet.getKey()).remove(TBRSet.getValue());
    }
  }

  @Override
  public boolean hasNode(T node) {
    return nodeEdgeMap.containsKey(node);
  }

  @Override
  public void connect(T node1, T node2, String name, int weight) throws NoSuchElementException, IllegalArgumentException, IllegalStateException {
    if (!hasNode(node1) || !hasNode(node2)) throw new NoSuchElementException();
    if (weight < 0) throw new IllegalArgumentException();
    if (node1.equals(node2)) throw new IllegalArgumentException();
    if (getEdgeBetween(node1, node2) != null) throw new IllegalStateException();

    Set<Edge<T>> node1Edges = nodeEdgeMap.get(node1);
    Set<Edge<T>> node2Edges = nodeEdgeMap.get(node2);

    node1Edges.add(new ListEdge<T>(node2, name, weight));
    node2Edges.add(new ListEdge<T>(node1, name, weight));
  }

  @Override
  public void disconnect(T node1, T node2) throws NoSuchElementException, IllegalStateException {
    if (!hasNode(node1) || !hasNode(node2)) throw new NoSuchElementException();
    if (getEdgeBetween(node1, node2) == null) throw new IllegalStateException();

    nodeEdgeMap.get(node1).remove(getEdgeBetween(node1, node2));;
    nodeEdgeMap.get(node2).remove(getEdgeBetween(node2, node1));

  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) throws NoSuchElementException, IllegalArgumentException, IllegalStateException {
    if (!hasNode(node1) || !hasNode(node2) || getEdgeBetween(node1, node2) == null) throw new NoSuchElementException();
    if (weight < 0) throw new IllegalArgumentException();

    getEdgeBetween(node1, node2).setWeight(weight);
    getEdgeBetween(node2, node1).setWeight(weight);
  }

  @Override
  public Set<T> getNodes() {
    return new HashSet<>(nodeEdgeMap.keySet());
  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) throws NoSuchElementException {
    if (nodeEdgeMap.get(node) == null) throw new NoSuchElementException();
    else return new HashSet<>(nodeEdgeMap.get(node));
  }

  @Override
  public Edge<T> getEdgeBetween(T node1, T node2) throws NoSuchElementException {
    if (!hasNode(node1) || !hasNode(node2)) throw new NoSuchElementException();
  
    Collection<Edge<T>> node1Edges = getEdgesFrom(node1);
    for (Edge<T> edge : node1Edges) {
      if (edge.getDestination().equals(node2)) {
        return edge;
      }
    }
    return null;
  }

  @Override
  public Iterator<T> iterator() {
    Iterator<T> it = getNodes().iterator();
    return it;
  }

  @Override
  public String toString() {
    Iterator<T> it = iterator();
    StringBuilder sB = new StringBuilder();
    while (it.hasNext()) {
      T nextNode = it.next();
      sB.append(nextNode);
      for (Edge<T> edge : getEdgesFrom(nextNode)) {
        sB.append(edge.toString() + "\n");
      }
    }
    return sB.toString();
  }
}

