package se.su.inlupp;

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
}
