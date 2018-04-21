/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

import algorithms.AlgorithmParameters;

/**
 *
 * @author kevingray
 */
public class ClusteringParameters extends AlgorithmParameters {
    
    private final int maxIterations;
    private final int updateInterval;
    private final boolean continuous;
    private final boolean runnable;
    private final int clusters;
    
    
    
    public ClusteringParameters() {
        this.maxIterations = 0;
        this.updateInterval= 0;
        this.continuous = true;
        this.runnable = false;
        this.clusters = 0;
    }
    
    public ClusteringParameters(int m, int u, boolean c, int n) {
        this.maxIterations = m;
        this.updateInterval= u;
        this.continuous = c;
        this.runnable = true;
        this.clusters = n;
    }
    
    @Override
    public int getMaxIterations() { return maxIterations; }
    
    @Override
    public int getUpdateInterval() { return updateInterval; }
    
    @Override
    public boolean isContinuous() { return continuous; }
    
    @Override
    public boolean isRunnable() { return runnable; }
    
    public int numberOfClusters() { return clusters; }
}
