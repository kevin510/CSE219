/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

import algorithms.Clusterer;
import dataprocessors.DataSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import vilij.templates.ApplicationTemplate;

/**
 *
 * @author kevingray
 */
public class RandomClusterer extends Clusterer {

    
    private static final Random RAND = new Random();
    public ApplicationTemplate template;

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;

    private final AtomicBoolean tocontinue;
    
    public RandomClusterer(DataSet dataset,
                           int maxIterations,
                           int updateInterval,
                           int numberOfClusters) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(false);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
