package se.su.inlupp;

// PROG2 VT2026, Inlämningsuppgift, del 1
// Adam McCarthy - admc0801
// Joakim Lindé - joli3174
// Arvid Flodin - arfl0534

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphModel {
    private Graph<String> graph;
    private PathFinder<String> pathFinder;

    public GraphModel() {
        graph = new ListGraph<>();
        pathFinder = new BFSPathFinder<>();
    }
    public void setPathFinder(PathFinder<String> pf) {
        this.pathFinder = pf;
    }
    
    public Path<String> getPath(String start, String goal) {
        return pathFinder.findPath(graph, start, goal);
    }

    public void connectNodes(String node1, String node2, int edgeWeight, Map<Edge<String>, GuiLine> map, GuiLine line)  {
        graph.connect(node1, node2, (node1 + " till " + node2), edgeWeight);
        Edge<String> firstEdgeBetween = graph.getEdgeBetween(node1, node2);
        Edge<String> secondEdgeBetween = graph.getEdgeBetween(node2, node1);
        
        map.put(firstEdgeBetween, line);
        map.put(secondEdgeBetween, line);
    }

    public Set<String> getGraphNodes() {
        return graph.getNodes();
    }

    public void addNode(String node) {
        graph.add(node);
    }

    public Set<Edge<String>> deleteNode(String node) {
        Set<Edge<String>> toBeDeletedList = new HashSet<>();
        Collection<Edge<String>> nodeEdges = graph.getEdgesFrom(node);
        
        for (Edge<String> edge : nodeEdges) {
            toBeDeletedList.addAll(graph.getEdgesFrom(edge.getDestination()));
        }

        toBeDeletedList.addAll(graph.getEdgesFrom(node));
        
        graph.remove(node);
        return toBeDeletedList;
    }

    public Graph<String> getGraph() {
        return graph;
    }
    public void clearGraph() {
        graph = new ListGraph<>();
    }
}
