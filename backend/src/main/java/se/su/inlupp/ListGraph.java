package se.su.inlupp;

import java.util.*;

public class ListGraph<T> implements Graph<T> {

  private final Map<T, Set<Edge<T>>> nodeEdgeMap = new HashMap<>();

  @Override
  public void add(T node) {
    nodeEdgeMap.putIfAbsent(node, new HashSet<>());
  }

  @Override
  public void remove(T node) {
    if (!hasNode(node)) throw new NoSuchElementException();
    
    nodeEdgeMap.remove(node);

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
  public void connect(T node1, T node2, String name, int weight) {
    this.add(node1);
    this.add(node2);

    Set<Edge<T>> node1Edges = nodeEdgeMap.get(node1);
    Set<Edge<T>> node2Edges = nodeEdgeMap.get(node2);

    node1Edges.add(new ListEdge<T>(node2, name, weight));
    node2Edges.add(new ListEdge<T>(node1, name, weight));
  }

  @Override
  public void disconnect(T node1, T node2) {
    throw new UnsupportedOperationException("Unimplemented method 'disconnect'");
  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {
    throw new UnsupportedOperationException("Unimplemented method 'setConnectionWeight'");
  }

  @Override
  public Set<T> getNodes() {
    return nodeEdgeMap.keySet();
  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) throws NoSuchElementException {
    if (nodeEdgeMap.get(node) == null) throw new NoSuchElementException();
    else return nodeEdgeMap.get(node);
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
    //vrf funkar inte getNodes()
    Iterator<T> it = nodeEdgeMap.keySet().iterator();
    return it;
  }
}

