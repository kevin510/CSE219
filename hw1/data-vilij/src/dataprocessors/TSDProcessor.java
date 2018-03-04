package dataprocessors;

import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {
    
    private int sumOfY;
    private int divBy;
    private int largestX;
    
    private static final String ERROR_ON_LINE = "Error on Line ";

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }
    
    public static class NameTakenException extends Exception {

        private static final String ERROR_MSG = "already exists. First duplicate on line: ";

        public NameTakenException(String name) {
            super(String.format("'%s' " + ERROR_MSG, name));
        }
    }

    private Map<String, String>  dataLabels;
    private Map<String, Point2D> dataPoints;
    private int lineNumber;
    private HashSet<String> nameCheck;

    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
    }
    
    private synchronized void incLineNumber() {
        lineNumber++;
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    void processString(String tsdString) throws Exception {
        lineNumber = 0;
        sumOfY = 0;
        divBy = 0;
        largestX = 0;
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder(0);
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach((List<String> list) -> {
                  try {
                      incLineNumber();
                      String   name  = checkedname(list.get(0));
                      String   label = list.get(1);
                      String[] pair  = list.get(2).split(",");
                      sumOfY += Double.parseDouble(pair[1]);
                      divBy++;
                      if(Double.parseDouble(pair[0]) > largestX) {
                          largestX = (int) Double.parseDouble(pair[0]);
                      }
                      Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      dataLabels.put(name, label);
                      dataPoints.put(name, point);

                  } catch (InvalidDataNameException e) {
                      if(!(errorMessage.length() > 0)) {
                        errorMessage.append(getClass().getSimpleName()).append(": ").append(e.getMessage());
                        hadAnError.set(true);
                      }

                  } catch (Exception e) {
                      if(!(errorMessage.length() > 0)) {
                          errorMessage.append(getClass().getSimpleName()).append(": ").append(ERROR_ON_LINE).append(lineNumber).append("\n");
                      }
                      hadAnError.set(true);
                  }
              });
        if (errorMessage.length() > 0) {
            throw new Exception(errorMessage.toString());
        }
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    public void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new HashSet<>(dataLabels.values());
        sumOfY = sumOfY / divBy;
        XYChart.Series<Number, Number> avgLine = new XYChart.Series<>();
        avgLine.setName("Average");
        avgLine.getData().add(new XYChart.Data<>(0, sumOfY));
        avgLine.getData().add(new XYChart.Data<>(largestX, sumOfY));
        chart.getData().add(avgLine);
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
            });
            chart.getData().add(series);
        }
    }
    
    public void dataNameCheck(String data) throws Exception {
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder(0);
        nameCheck = new HashSet<>();
        lineNumber = 0;
        Stream.of(data.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach((List<String> list) -> {
                  try {
                      incLineNumber();
                      String   name  = nameExists(list.get(0));
                      nameCheck.add(name);
                  } catch (NameTakenException e) {
                      if(!(errorMessage.length() > 0)) {
                        errorMessage.append(getClass().getSimpleName()).append(": ").append(e.getMessage()).append(lineNumber);
                        hadAnError.set(true);
                      }
                  }
              });
        if (errorMessage.length() > 0) {
            throw new Exception(errorMessage.toString());
        }
    }

    void clear() {
        dataPoints.clear();
        dataLabels.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@")) 
            throw new InvalidDataNameException(name);
        return name;
    }
    
    private String nameExists(String name) throws NameTakenException {
        if(nameCheck.contains(name))
            throw new NameTakenException(name);
        return name;
    }
    
}