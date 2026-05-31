package se.su.inlupp;

// PROG2 VT2026, Inlämningsuppgift, del 1
// Adam McCarthy - admc0801
// Joakim Lindé - joli3174
// Arvid Flodin - arfl0534

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DFSPathFinder<T> implements PathFinder<T> {

  @Override
  public Path<T> findPath(Graph<T> graph, T from, T to) {
    Map<T, T> connections = new HashMap<>();
    List<Edge<T>> edges = new ArrayList<Edge<T>>();

    connect(from, null, connections, graph);

    T current = to;

    while (current != null && !current.equals(from) && connections.containsKey(to)) {
      T next = connections.get(current);
      Edge<T> edge = graph.getEdgeBetween(next, current);
      edges.add(edge);
      current = next;
    }
    
    Collections.reverse(edges);

    return edges.size() == 0 ? null : new ListPath<>(from, edges);
  }


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

