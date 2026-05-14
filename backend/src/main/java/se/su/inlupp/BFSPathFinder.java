package se.su.inlupp;

import java.util.*;

public class BFSPathFinder<T> implements PathFinder<T> {

  @Override
  public Path<T> findPath(Graph<T> graph, T from, T to) {
    Map<T, T> connections = new HashMap<>();
    connections.put(from, null);
    LinkedList<T> queue = new LinkedList<>();

    queue.add(from);

    while (!queue.isEmpty()){
      T current = queue.poll();

      for ( Edge<T> edge : graph.getEdgesFrom(current)){
        T next = edge.getDestination();

        if (!connections.containsKey(next)) {
          connections.put(next, current);
          queue.add(next);
        }

      }
    }
    LinkedList<T> path = new LinkedList<>();
    T current = to;

    while (current != null && !current.equals(from)){
      path.addFirst(current);
      current = connections.get(current);
    }

    if (current == null) return null;
    path.addFirst(from);
    return new ListPath<>(path);
      
    };

  }
}

