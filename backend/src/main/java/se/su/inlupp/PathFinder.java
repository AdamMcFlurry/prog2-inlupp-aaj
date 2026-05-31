package se.su.inlupp;

// PROG2 VT2026, Inlämningsuppgift, del 1
// Adam McCarthy - admc0801
// Joakim Lindé - joli3174
// Arvid Flodin - arfl0534

public interface PathFinder<T> {

  Path<T> findPath(Graph<T> graph, T from, T to);
}

