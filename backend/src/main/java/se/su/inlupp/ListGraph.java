package se.su.inlupp;

import java.util.*;

public class ListGraph<T> implements Graph<T> {

  private Map<T, Set<Edge<T>>> nodeEdgeMap = new HashMap<>();

  @Override
  public void add(T node) {
    nodeEdgeMap.putIfAbsent(node, new HashSet<>());
  }

  @Override
  public void remove(T node) {
    try {
      nodeEdgeMap.remove(node);
      nodeEdgeMap.forEach((k, v) -> {
        if (v.contains(node)) {
          v.remove(node);
        }
      });
    } catch (NoSuchElementException e) {
      e.printStackTrace();
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

