package entities;

import java.util.ArrayList;
import java.util.List;

// A Cluster class to store centroid and elements.
public class Cluster {
    private Element centroid;
    private List<Element> elements = new ArrayList<>();

    public Element getCentroid() {
        return centroid;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setCentroid(Element centroid) {
        this.centroid = centroid;
    }

    public void addElement(Element element) {
        elements.add(element);
    }

    public void clearElements() {
        elements = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "centroid=" + centroid +
                '}';
    }
}
