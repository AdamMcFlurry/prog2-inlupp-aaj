package se.su.inlupp;

import java.util.*;

public class BFSPathFinder<T> implements PathFinder<T> {

  @Override
  public Path<T> findPath(Graph<T> graph, T from, T to) {
    Map<T, T> connections = new HashMap<>();
    List<Edge<T>> edgeList = new ArrayList<>();

    LinkedList<T> queue = new LinkedList<>();
    
    connections.put(from, null);
    queue.add(from);
    
    while (!queue.isEmpty()){
      T current = queue.poll();
      
      for (Edge<T> edge : graph.getEdgesFrom(current)){
        T next = edge.getDestination();
        
        if (!connections.containsKey(next)) {
          connections.put(next, current);
          queue.add(next);
        }
        
      }
    }
    if (!connections.containsKey(to)) return null;

    T current = to;
    
    while (current != null && !current.equals(from)) {
      T next = connections.get(current);
      Edge<T> edge = graph.getEdgeBetween(next, current);
      edgeList.add(edge);
      current = next;
    }
    Collections.reverse(edgeList);

    return new ListPath<>(from, edgeList);

  }

}
