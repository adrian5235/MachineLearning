package machineLearnings;

import entities.Element;
import entities.Cluster;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class WithoutTeacher {

    private final List<Element> elements = new ArrayList<>();
    private List<Cluster> clusters;
    private int theHighestValueOfAFeature = Integer.MIN_VALUE;
    private int theLowestValueOfAFeature = Integer.MAX_VALUE;
    private final int numberOfElementFeatures = 9;
    private final int k;

    public WithoutTeacher(String filePath, int k) throws FileNotFoundException {
        this.k = k;
        loadAllElements(filePath);
        System.out.println("Dataset size = " + elements.size());
        cluster(k);
        verify();
    }

    // Method to load all elements from file and store them in a list.
    // Method also finds and sets the highest and the lowest value of a feature in the set.
    public void loadAllElements(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner sc = new Scanner(file);
        String line;
        String[] lineSplitted;
        int[] features;

        while (sc.hasNext()) {
            features = new int[numberOfElementFeatures];
            line = sc.nextLine();
            lineSplitted = line.split(";");
            for (int i = 0; i < lineSplitted.length; i++) {
                features[i] = Integer.parseInt(lineSplitted[i]);
                if (features[i] > theHighestValueOfAFeature) {
                    theHighestValueOfAFeature = features[i];
                }
                if (features[i] < theLowestValueOfAFeature) {
                    theLowestValueOfAFeature = features[i];
                }
            }
            elements.add(new Element(features));
        }
    }

    // Method to cluster elements set by using k-averages method that uses no teacher.
    // Given k parameter determines how many clusters elements set is divided into.
    public void cluster(int k) {
        initializeClusters(k);

        for (Cluster cluster : clusters) {
            cluster.setCentroid(pickRandomElement());
        }

        for (Element element : elements) {
            assignElementToTheNearestCluster(element);
        }

        for (Cluster cluster : clusters) {
            calculateCentroid(cluster);
        }

        // if centroid didn't change after clustering the specific cluster then
        // the clustering for this cluster is done
        List<Cluster> clustersForClustering = new ArrayList<>(clusters);
        while (!clustersForClustering.isEmpty()) {
            for (Cluster cluster : clustersForClustering) {
                Element previousCentroid = cluster.getCentroid();
                clearElements();
                for (Element element : elements) {
                    assignElementToTheNearestCluster(element);
                }
                calculateCentroid(cluster);
                Element centroid = cluster.getCentroid();
                if (centroid.equals(previousCentroid)) {
                    clustersForClustering.remove(cluster);
                    break;
                }
            }
        }
    }

    // Method to initialize k clusters.
    public void initializeClusters(int k) {
        clusters = new ArrayList<>();
        for(int i = 0; i < k; i++) {
            clusters.add(new Cluster());
        }
    }

    // Method to pick a random element from elements set.
    public Element pickRandomElement() {
        int bound = elements.size();
        return elements.get(ThreadLocalRandom.current().nextInt(bound)).clone();
    }

    // Method to assign a given element to the nearest cluster by picking the nearest centroid.
    public void assignElementToTheNearestCluster(Element element) {
        int nearestClusterIndex = 0;
        double lowestDistance = Double.MAX_VALUE;

        for (int i = 0; i < clusters.size(); i++) {
            Element centroid = clusters.get(i).getCentroid();
            double d = calculateDistance(element, centroid);
            if (d < lowestDistance) {
                lowestDistance = d;
                nearestClusterIndex = i;
            }
        }

        clusters.get(nearestClusterIndex).addElement(element);
    }

    // Method to calculate distance between elements using their features and the manhattan metric.
    public double calculateDistance(Element element1, Element element2) {
        double d = 0;
        int[] element1Features = element1.features();
        int[] element2Features = element2.features();

        for (int i = 0; i < element1Features.length; i++) {
            d += Math.abs(element1Features[i] - element2Features[i]);
        }

        return d;
    }

    // Method to calculate new centroid for a cluster by calculating
    // average values of cluster's elements' features.
    public void calculateCentroid(Cluster cluster) {
        int[] centroidFeatures = new int[numberOfElementFeatures];

        for(Element element : cluster.getElements()) {
            int[] features = element.features();
            for(int i = 0; i < centroidFeatures.length; i++) {
                centroidFeatures[i] += features[i];
            }
        }

        for (int i = 0; i < centroidFeatures.length; i++) {
            int tmpFeature = centroidFeatures[i];
            tmpFeature /= cluster.getElements().size();
            centroidFeatures[i] = tmpFeature;
        }

        cluster.setCentroid(new Element(centroidFeatures));
    }

    public void clearElements() {
        for (Cluster cluster : clusters) {
            cluster.clearElements();
        }
    }

    // Method to verify how clustering went using specific methods.
    // The verification of distance between clusters is written for k = 4,
    // but it can be easily extended by following the pattern if needed.
    public void verify() {
        int i = 1;
        for (Cluster cluster : clusters) {
            System.out.println("\n" + i + ": " + cluster);
            System.out.println("Cluster size = " + cluster.getElements().size());
            verifyByCohesion(cluster);
            i++;
        }

        StringBuilder sb = new StringBuilder();
        if (k >= 2) {
            sb.append("\n" + "The distance between clusters 1 and 2 is ")
                    .append(calculateDistanceBetweenClusters(clusters.get(0), clusters.get(1)));
        }
        if (k >= 3) {
            sb.append(System.lineSeparator());
            sb.append("The distance between clusters 1 and 3 is ")
                    .append(calculateDistanceBetweenClusters(clusters.get(0), clusters.get(2)));
            sb.append(System.lineSeparator());
            sb.append("The distance between clusters 2 and 3 is ")
                    .append(calculateDistanceBetweenClusters(clusters.get(1), clusters.get(2)));
        }
        if (k >= 4) {
            sb.append(System.lineSeparator());
            sb.append("The distance between clusters 1 and 4 is ")
                    .append(calculateDistanceBetweenClusters(clusters.get(0), clusters.get(3)));
            sb.append(System.lineSeparator());
            sb.append("The distance between clusters 2 and 4 is ")
                    .append(calculateDistanceBetweenClusters(clusters.get(1), clusters.get(3)));
            sb.append(System.lineSeparator());
            sb.append("The distance between clusters 3 and 4 is ")
                    .append(calculateDistanceBetweenClusters(clusters.get(2), clusters.get(3)));
        }
        System.out.println(sb);
    }

    // Method to measure how close are the elements within the same cluster
    // by calculating the average distance between them.
    public void verifyByCohesion(Cluster cluster) {
        List<Double> averages = new ArrayList<>();
        List<Element> elements = cluster.getElements();
        for (Element currentElement : elements) {
            double sum = 0;
            for (Element element : elements) {
                double d = calculateDistance(currentElement, element);
                sum += d;
            }
            double average = sum / elements.size();
            averages.add(average);
        }
        double sum = 0;
        for (Double average : averages) {
            sum += average;
        }
        double cohesion = sum / averages.size();
        System.out.println("Cluster cohesion = " + cohesion);
    }

    // Method to calculate the average distance between elements of two clusters.
    public double calculateDistanceBetweenClusters(Cluster cluster1, Cluster cluster2) {
        List<Double> averages = new ArrayList<>();

        List<Element> elements1 = cluster1.getElements();
        List<Element> elements2 = cluster2.getElements();

        for (Element element1 : elements1) {
            double sum = 0;
            int i = 0;
            for (Element element2 : elements2) {
                sum += calculateDistance(element1, element2);
                i++;
            }
            averages.add(sum / i);
        }
        double sum = 0;
        for (Double average : averages) {
            sum += average;
        }
        return sum / averages.size();
    }
}
