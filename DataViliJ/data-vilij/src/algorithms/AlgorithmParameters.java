/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

/**
 *
 * @author kevingray
 */
public abstract class AlgorithmParameters {
    
    int maxIterations;
    int updateInterval;
    boolean continuous;
    boolean runnable;
    
    public abstract int getMaxIterations();
    
    public abstract int getUpdateInterval();
    
    public abstract boolean isContinuous();
    
    public abstract boolean isRunnable();

}
