package classification;

import algorithms.Classifier;
import dataprocessors.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
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
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();
    public ApplicationTemplate template;

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;

    // currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;

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
    
    public RandomClassifier() {
        this.dataset = new DataSet();
        this.maxIterations = 1;
        this.updateInterval = 1;
        this.tocontinue = new AtomicBoolean(true);
    }

    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
    }

    @Override
    public void run() {
        
        AppUI ui = ((AppUI) template.getUIComponent());
        
        for (int i = 1; i <= maxIterations; i++) {
            incrementGlobalTimer();
            XYChart.Series<Number, Number> line = new XYChart.Series<>();
            int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int yCoefficient = new Double(RAND.nextDouble() + 1 * 100).intValue();
            int constant     = new Double(RAND.nextDouble() * 100).intValue();
            line.getData().add(new XYChart.Data<>(0, constant));
            line.getData().add(new XYChart.Data<>(xCoefficient*10, (-(xCoefficient*10)-constant)/yCoefficient));
            output = Arrays.asList(xCoefficient, yCoefficient, constant);
//            System.out.println("global: " + getGlobalTimer());
//            System.out.println("i: "+ i);
            if(getGlobalTimer() % updateInterval == 0) {
                
                Platform.runLater(() -> {
                    ui.clearChart();
                    ui.addToChart(line);
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
                 //everything below is just for internal viewing of how the output is changing
                 //in the final project, such changes will be dynamically visible in the UI
//            if (i % updateInterval == 0) {
//                System.out.printf("Iteration number %d: ", i); //
//                flush();
//            }
//            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
//                System.out.printf("Iteration number %d: ", i);
//                flush();
//                break;
//            }

        }
        Platform.runLater(() -> {
            ui.disableScreenshotButton(false);
            ui.disableRunButton(false);
        });
        setRunInProgress(false);
        resetGlobalTimer();
        resetFlag();
    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    /** A placeholder main method to just make sure this code runs smoothly */
    public static void main(String... args) throws IOException {
        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
        classifier.run(); // no multithreading yet
    }
}