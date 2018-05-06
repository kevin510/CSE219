/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

import algorithms.Clusterer;
import dataprocessors.DataSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.geometry.Point2D;
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
 *
 * @author kevingray
 */
public class RandomClusterer extends Clusterer {

    
    private static final Random RAND = new Random();
    public ApplicationTemplate template;

    @SuppressWarnings("FieldCanBeLocal")
    
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;

    private final AtomicBoolean tocontinue;
    
    public RandomClusterer(DataSet dataset,
                           int maxIterations,
                           int updateInterval,
                           boolean tocontinue,
                           int numberOfClusters) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
    }
    
    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }    

    @Override
    public void run() {
        
        AppUI ui = ((AppUI) template.getUIComponent());
        
        String[] labels = new String[numberOfClusters];
        for(int i = 0; i < numberOfClusters; i++) {
            labels[i] = Integer.toString(i);
        }
        final DataSet clusteredData = dataset;
        
        for (int i = 1; i <= maxIterations; i++) {
            incrementGlobalTimer();
            for(String name: clusteredData.getLabels().keySet()) {
                int randInt = RAND.nextInt(numberOfClusters);
                clusteredData.updateLabel(name, labels[randInt]);
            }
                        
            if(getGlobalTimer() % updateInterval == 0) {
                
                Runnable updateUITask = () -> {
                    ui.clearChart();
                    List<String> newLabels = new ArrayList(clusteredData.getLabels().values());
                    for(String nL: newLabels) {
                        XYChart.Series<Number, Number> series = new XYChart.Series<>();
                        series.setName(nL);
                        clusteredData.getLabels().entrySet().stream().filter(entry -> entry.getValue().equals(nL)).forEach(entry -> {
                            Point2D point = clusteredData.getLocations().get(entry.getKey());
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
    
    
}
