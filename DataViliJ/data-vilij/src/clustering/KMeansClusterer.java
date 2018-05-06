package clustering;

import algorithms.Clusterer;
import dataprocessors.DataSet;
import javafx.geometry.Point2D;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import ui.AppUI;
import static ui.AppUI.getGlobalTimer;
import static ui.AppUI.incrementGlobalTimer;
import static ui.AppUI.resetFlag;
import static ui.AppUI.resetGlobalTimer;
import static ui.AppUI.runInProgress;
import static ui.AppUI.setRunInProgress;
import vilij.templates.ApplicationTemplate;

/**
 * @author Ritwik Banerjee
 */
public class KMeansClusterer extends Clusterer {

    private DataSet       dataset;
    private List<Point2D> centroids;

    private final int           maxIterations;
    private final int           updateInterval;
    private final AtomicBoolean tocontinue;
    public  ApplicationTemplate template;

    public KMeansClusterer() {
        super(0);
        this.dataset = new DataSet();
        this.maxIterations = 1;
        this.updateInterval = 1;
        this.tocontinue = new AtomicBoolean(true);
    }
    public KMeansClusterer(DataSet dataset, int maxIterations, int updateInterval, boolean tocontinue, int numberOfClusters) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
    }

    @Override
    public int getMaxIterations() { return maxIterations; }

    @Override
    public int getUpdateInterval() { return updateInterval; }

    @Override
    public boolean tocontinue() { return tocontinue.get(); }

    @Override
    public void run() {
        AppUI ui = (AppUI) template.getUIComponent();
        initializeCentroids();
        int iteration = 0;
        while (iteration++ < maxIterations) {
            incrementGlobalTimer();
            assignLabels();
            recomputeCentroids();
            
            if(getGlobalTimer() % updateInterval == 0) {
                
                Runnable updateUITask = () -> {
                    ui.clearChart();
                    List<String> newLabels = new ArrayList(dataset.getLabels().values());
                    for(String nL: newLabels) {
                        XYChart.Series<Number, Number> series = new XYChart.Series<>();
                        series.setName(nL);
                        dataset.getLabels().entrySet().stream().filter(entry -> entry.getValue().equals(nL)).forEach(entry -> {
                            Point2D point = dataset.getLocations().get(entry.getKey());
                            series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
                        });
                        Platform.runLater(() -> ui.addSeriesToChart(series));
                    }
                };
                
                Platform.runLater(() -> {
                    ui.clearChart();
                    Thread updateUIThread = new Thread(updateUITask);
                    updateUIThread.start();
                });
                
                if(!tocontinue()) {
                    setRunInProgress(false);
                    Platform.runLater(() -> {
                        ui.disableRunButton(false);
                        ui.disableScreenshotButton(false);
                    });
                    
                    while(!runInProgress()) {
                        try {
                        //System.out.println("waiting");
                        Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            
                        }
                    }
                    Platform.runLater(() -> {
                        ui.disableScreenshotButton(true);
                    });
                }
                Platform.runLater(() -> {
                    ui.disableRunButton(true);
                });      
                
            }
            
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                
            }
            
        }
        Platform.runLater(() -> {
            ui.disableScreenshotButton(false);
            ui.disableRunButton(false);
        });
        setRunInProgress(false);
        resetGlobalTimer();
        resetFlag();
    }

    private void initializeCentroids() {
        Set<String>  chosen        = new HashSet<>();
        List<String> instanceNames = new ArrayList<>(dataset.getLabels().keySet());
        Random       r             = new Random();
        while (chosen.size() < numberOfClusters) {
            int i = r.nextInt(instanceNames.size());
            while (chosen.contains(instanceNames.get(i)))
                ++i;
            chosen.add(instanceNames.get(i));
        }
        centroids = chosen.stream().map(name -> dataset.getLocations().get(name)).collect(Collectors.toList());
        //tocontinue.set(true);
    }

    private void assignLabels() {
        dataset.getLocations().forEach((instanceName, location) -> {
            double minDistance      = Double.MAX_VALUE;
            int    minDistanceIndex = -1;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = computeDistance(centroids.get(i), location);
                if (distance < minDistance) {
                    minDistance = distance;
                    minDistanceIndex = i;
                }
            }
            dataset.getLabels().put(instanceName, Integer.toString(minDistanceIndex));
        });
    }

    private void recomputeCentroids() {
        //tocontinue.set(false);
        IntStream.range(0, numberOfClusters).forEach(i -> {
            AtomicInteger clusterSize = new AtomicInteger();
            Point2D sum = dataset.getLabels()
                                 .entrySet()
                                 .stream()
                                 .filter(entry -> i == Integer.parseInt(entry.getValue()))
                                 .map(entry -> dataset.getLocations().get(entry.getKey()))
                                 .reduce(new Point2D(0, 0), (p, q) -> {
                                     clusterSize.incrementAndGet();
                                     return new Point2D(p.getX() + q.getX(), p.getY() + q.getY());
                                 });
            Point2D newCentroid = new Point2D(sum.getX() / clusterSize.get(), sum.getY() / clusterSize.get());
            if (!newCentroid.equals(centroids.get(i))) {
                centroids.set(i, newCentroid);
                //tocontinue.set(true);
            }
        });
    }

    private static double computeDistance(Point2D p, Point2D q) {
        return Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getY() - q.getY(), 2));
    }
    
}