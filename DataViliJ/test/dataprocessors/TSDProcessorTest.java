/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataprocessors;

import dataprocessors.TSDProcessor.NameTakenException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kevingray
 */
public class TSDProcessorTest {
    
    public TSDProcessorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * 
     * Test for: processing valid line of input
     */
    @Test
    public void ProcessStringValidCheck() {
        TSDProcessor processor = new TSDProcessor();
        boolean thrown = false;
        String valid = "@a\tl\t3,8.4";
        try {
            processor.processString(valid);
        } catch(Exception e) {
            thrown = true;
        }
        assertFalse(thrown);
    }
    
    /**
     * 
     * @throws Exception 
     * Test for: InvalidDataNameException
     */
    @Test(expected = Exception.class)
    public void ProcessStringInvalidCheck() throws Exception {
        TSDProcessor processor = new TSDProcessor();
        String invalid = "a\tl\t3,3";
        processor.processString(invalid);
    }
    
    /**
     * 
     * @throws Exception 
     * Test for: NameTakenException
     */
    @Test(expected = Exception.class)
    public void ProcessStringNameTakenCheck() throws Exception {
        TSDProcessor processor = new TSDProcessor();
        String valid = "@a\tl\t3,3";
        processor.processString(valid);
        String taken = "@a\nla\t5,4";
        processor.processString(taken);
    }
}
