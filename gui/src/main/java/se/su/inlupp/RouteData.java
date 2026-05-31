package se.su.inlupp;

// PROG2 VT2026, Inlämningsuppgift, del 2
// Adam McCarthy - admc0801
// Joakim Lindé - joli3174
// Arvid Flodin - arfl0534

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
