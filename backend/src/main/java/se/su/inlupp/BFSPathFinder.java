package se.su.inlupp;

import java.util.*;

public class BFSPathFinder<T> implements PathFinder<T> {

  @Override
  public Path<T> findPath(Graph<T> graph, T from, T to) {
    Map<T, T> connections = new HashMap<>();
    LinkedList<T> queue = new LinkedList<>();
    ListPath<T> path = new ListPath<T>(from);
    List<Edge<T>> edgeList = new ArrayList<>();
    
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

    T current = to;
    
    while (current != null && !current.equals(from)) {
      T next = connections.get(current);

      Edge<T> edge = graph.getEdgeBetween(next, current);
      edgeList.add(edge);
      current = connections.get(current);
    }

    int index = edgeList.size()-1;
    while (index >= 0) {
      path.addEdge(edgeList.get(index));
      index--;
    }

    return path;  
  };

}
