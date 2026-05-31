package se.su.inlupp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RouteData {
    private String imagePath;
    private Map<String, double[]> nodes = new HashMap<>();
    private List<String[]> edges = new ArrayList<>();
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public Map<String, double[]> getNodes() {
        return nodes;
    }
    public List<String[]> getEdges(){
        return edges;
    }
}
