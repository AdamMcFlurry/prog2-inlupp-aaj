package se.su.inlupp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DFSPathFinder<T> implements PathFinder<T> {

  @Override
  public Path<T> findPath(Graph<T> graph, T from, T to) {
    Map<T, T> connections = new HashMap<>();
    connect(from, null, connections, graph);
    LinkedList<Edge<T>> path = new LinkedList<>();

    T current = to;
    while (current != null && !current.equals(from)) {
      T next = connections.get(current);
      Edge<T> edge = graph.getEdgeBetween(next, current);
      path.addFirst(edge);
      current = next;
    }

    return path;
  }

  // får man ha med privata methoder
  private void connect(T to, T from, Map<T, T> connections, Graph<T> graph) {
    connections.put(to, from);
    for (Edge<T> edge : graph.getEdgesFrom(to)) {
      T destination = edge.getDestination();
      if (!connections.containsKey(destination)) {
        connect(destination, to, connections, graph);
      }
    }
  }
}

