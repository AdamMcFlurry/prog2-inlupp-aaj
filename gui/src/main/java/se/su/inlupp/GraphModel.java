package se.su.inlupp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GraphModel {
    private Graph<String> graph;

    public GraphModel() {
        graph = new ListGraph<>();
    }

    public Edge<String> connectNodes(String node1, String node2, int edgeWeight)  {
        graph.connect(node1, node2, (node1 + " till " + node2), edgeWeight);
        Edge<String> edgeBetween = graph.getEdgeBetween(node1, node2);
        return edgeBetween;
    }

    public ObservableList<String> getGraphNodes() {
        return FXCollections.observableArrayList(graph.getNodes());
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
}
