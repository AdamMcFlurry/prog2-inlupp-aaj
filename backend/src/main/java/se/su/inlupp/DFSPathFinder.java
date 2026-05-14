package se.su.inlupp;

import java.util.HashMap;
import java.util.Map;

public class DFSPathFinder<T> implements PathFinder<T> {

  @Override
  public Path<T> findPath(Graph<T> graph, T from, T to) {
    Map<T, T> connections = new HashMap<>();
    ListPath<T> path = new ListPath<>(from);

    connect(null, from, connections, graph);

    T current = from;
    while (current != null && !current.equals(to) && connections.containsValue(to)) {
      T next = connections.get(current);
      Edge<T> edge = graph.getEdgeBetween(current, next);
      path.addEdge(edge);
      current = next;
    }

    return path.getEdges().isEmpty() ? null : path;
  }

  // får man ha med privata methoder
  private void connect(T from, T to, Map<T, T> connections, Graph<T> graph) {
    connections.put(from, to);
    for (Edge<T> edge : graph.getEdgesFrom(to)) {
      T destination = edge.getDestination();
      if (!connections.containsValue(destination)) {
        connect(to, destination, connections, graph);
      }
    }
  }
}

