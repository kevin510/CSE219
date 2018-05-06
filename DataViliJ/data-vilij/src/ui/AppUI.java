package ui;

import actions.AppActions;
import algorithms.Algorithm;
import algorithms.Clusterer;
import classification.RandomClassifier;
import clustering.KMeansClusterer;
import clustering.RandomClusterer;
import dataprocessors.AppData;
import static java.io.File.separator;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import static settings.AppPropertyTypes.*;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import static vilij.settings.PropertyTypes.*;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private LineChart<Number, Number>    chart;          // the chart where data will be displayed
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private String scrnshotIconPath, runIconPath, settingsIconPath, editIconPath;
    private String cssPathUI;
    private RadioButton algType1, algType2;
    private ToggleGroup selectAlgType;
    private Label instanceCount, labelCount, labelNames, source;
    private Button editText;
    private int numLabels;
    private RadioButton clusteringAlg1, clusteringAlg2;
    private RadioButton classificationAlg1;
    private ToggleGroup selectClassificationAlg, selectClusteringAlg;
    private HBox alg1Layout, alg2Layout;
    private Button alg1Settings, alg2Settings, run;
    private GridPane mainPane;
    private VBox leftPanel;
    private boolean isClassification;
    private final HashMap<RadioButton, Algorithm> algList = new HashMap<>();
    private static final AtomicInteger globalTimer = new AtomicInteger(0);
    private static final AtomicBoolean runInProgress = new AtomicBoolean(false);
    private static final AtomicInteger flag = new AtomicInteger(0);
    
    public LineChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = "/" + String.join(separator,
                                             applicationTemplate.manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                                             applicationTemplate.manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        scrnshotIconPath = String.join(separator, iconsPath, applicationTemplate.manager.getPropertyValue(SCREENSHOT_ICON.name()));
        runIconPath = String.join(separator, iconsPath, applicationTemplate.manager.getPropertyValue(RUN_ICON.name()));
        settingsIconPath = String.join(separator, iconsPath, applicationTemplate.manager.getPropertyValue(SETTINGS_ICON.name()));
        editIconPath = String.join(separator, iconsPath, applicationTemplate.manager.getPropertyValue(EDIT_ICON.name()));
        cssPathUI = "/" + String.join(separator,
                                             manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                                             manager.getPropertyValue(CSS_RESOURCE_PATH.name()),
                                             manager.getPropertyValue(DATA_VILIJ_CSS_NAME.name()));
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
        scrnshotButton = setToolbarButton(scrnshotIconPath, applicationTemplate.manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        toolBar.getItems().add(scrnshotButton);
        toolBar.getStylesheets().add(cssPath);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());

        scrnshotButton.setOnAction(e -> {
            try {
                ((AppActions) applicationTemplate.getActionComponent()).handleScreenshotRequest();
            } catch (IOException ex) {
                
            }
        });
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }
    
    @Override
    public void clear() {
        textArea.clear();
        applicationTemplate.getDataComponent().clear();
        clearChart();
    }

    private void layout() {
        
        PropertyManager manager = applicationTemplate.manager;
        
        leftPanel = new VBox();
        mainPane = new GridPane();
        
        textArea = new TextArea();
        textArea.setVisible(false);
        textArea.setDisable(true);
        //textArea.getStyleClass().add("text-area");
        textArea.setPrefRowCount(10);
        algType1 = new RadioButton(manager.getPropertyValue(CLASSIFICATION_ALG.name()));
        algType1.getStyleClass().add("app-radio-button");
        algType2 = new RadioButton(manager.getPropertyValue(CLUSTERING_ALG.name()));
        algType2.getStyleClass().add("app-radio-button");
        algType1.setVisible(false);
        algType2.setVisible(false);
        selectAlgType = new ToggleGroup();
        selectAlgType.getToggles().addAll(algType1, algType2);
        
        classificationAlg1 = new RadioButton("RandomClassifier ");
        classificationAlg1.getStyleClass().add("app-radio-button");
        algList.put(classificationAlg1, new RandomClassifier());
//        classificationAlg2 = new RadioButton("Classification Algorithm 2 ");
//        classificationAlg2.getStyleClass().add("app-radio-button");
//        algList.put(classificationAlg2, new ClassificationParameters());
//        classificationAlg3 = new RadioButton("Classification Algorithm 3 ");
//        classificationAlg3.getStyleClass().add("app-radio-button");
//        algList.put(classificationAlg3, new ClassificationParameters());
        selectClassificationAlg = new ToggleGroup();
        selectClassificationAlg.getToggles().add(classificationAlg1);
        
        clusteringAlg1 = new RadioButton("Random Clusterer");
        algList.put(clusteringAlg1, new RandomClusterer());
        clusteringAlg2 = new RadioButton("K Means Clusterer");
        algList.put(clusteringAlg2, new KMeansClusterer());
//        clusteringAlg3 = new RadioButton("Clustering Algorithm 3 ");
//        algList.put(clusteringAlg3, new ClusteringParameters());
        selectClusteringAlg = new ToggleGroup();
        selectClusteringAlg.getToggles().addAll(clusteringAlg1, clusteringAlg2);
        
        alg1Layout = new HBox();
        alg2Layout = new HBox();
        
        alg1Settings = setToolbarButton(settingsIconPath, applicationTemplate.manager.getPropertyValue(SETTINGS_TOOLTIP.name()), false);
        alg2Settings = setToolbarButton(settingsIconPath, applicationTemplate.manager.getPropertyValue(SETTINGS_TOOLTIP.name()), false);
        
        alg1Layout.setVisible(false);
        alg2Layout.setVisible(false);
        
        run = setToolbarButton(runIconPath, applicationTemplate.manager.getPropertyValue(RUN_TOOLTIP.name()), false);
        run.setVisible(false);
        
        instanceCount = new Label();
        instanceCount.setWrapText(true);
        labelCount = new Label();
        labelCount.setWrapText(true);
        labelNames = new Label();
        labelNames.setWrapText(true);
        source = new Label();
        source.setWrapText(true);
        
        editText = setToolbarButton(editIconPath, applicationTemplate.manager.getPropertyValue(EDIT_TOOLTIP.name()), false);
        editText.setVisible(false);
        
        newButton.setDisable(false);
        
        leftPanel.getChildren().addAll(textArea, editText, instanceCount, labelCount, labelNames, source, algType1, algType2,
        alg1Layout, alg2Layout, run);
        
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis(); 
        chart = new LineChart(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setTitle(manager.getPropertyValue(AppPropertyTypes.CHART_TITLE.name()));
        
        GridPane.setRowIndex(leftPanel, 0);
        GridPane.setColumnIndex(leftPanel, 0);
        GridPane.setRowIndex(chart, 0);
        GridPane.setColumnIndex(chart, 1);
        
        mainPane.getStylesheets().add(cssPathUI);
        mainPane.getChildren().addAll(leftPanel, chart);
        appPane.getChildren().add(mainPane);
    }
    
    private VBox configPane(String title, Algorithm A, boolean classification) {
        PropertyManager manager = applicationTemplate.manager;
        ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        VBox configPane = new VBox();
        Label T = new Label(title);
        
        Label maxIt = new Label(manager.getPropertyValue(MAX_ITERATIONS_LABEL.name()));
        TextArea setMaxIt = new TextArea();
        setMaxIt.setText(Integer.toString(A.getMaxIterations()));
        setMaxIt.setPrefRowCount(1);
        setMaxIt.setPrefColumnCount(10);
        
        Label updateInterval = new Label(manager.getPropertyValue(UPDATE_INTERVAL_LABEL.name()));
        TextArea setUpdateInterval = new TextArea();
        setUpdateInterval.setText(Integer.toString(A.getUpdateInterval()));
        setUpdateInterval.setPrefRowCount(1);
        setUpdateInterval.setPrefColumnCount(10);
        
        CheckBox contRun = new CheckBox(manager.getPropertyValue(CONTINUOUS_RUN_LABEL.name()));
        contRun.setSelected(A.tocontinue());
        
        Button ret = new Button(manager.getPropertyValue(RETURN_LABEL.name()));
        
        configPane.getChildren().addAll(T, maxIt, setMaxIt, updateInterval, setUpdateInterval, contRun);
        
        TextArea setNumberOfClusters = new TextArea();
        setNumberOfClusters.setVisible(false);
        if(classification == false) {
            Label numberOfClusters = new Label(manager.getPropertyValue(CLUSTERING_NUMBER_LABEL.name()));
            setNumberOfClusters.setText(Integer.toString(((Clusterer) A).getNumberOfClusters()));
            setNumberOfClusters.setVisible(true);
            setNumberOfClusters.setPrefRowCount(1);
            setNumberOfClusters.setPrefColumnCount(10);
            configPane.getChildren().addAll(numberOfClusters, setNumberOfClusters);
        }
        
        ret.setOnAction(e -> {
            try {
                if(Integer.parseInt(setMaxIt.getText()) < 1 || Integer.parseInt(setUpdateInterval.getText()) < 1)
                    throw new NumberFormatException();
                if(classification) {
                    if(algList.get((RadioButton) selectClassificationAlg.getSelectedToggle()).getClass().equals(RandomClassifier.class)) {
                        RandomClassifier newAlg = new RandomClassifier(
                                    ((AppData) applicationTemplate.getDataComponent()).getData(),
                                    Integer.parseInt(setMaxIt.getText()),
                                    Integer.parseInt(setUpdateInterval.getText()),
                                    contRun.isSelected());
                        newAlg.template = applicationTemplate;
                        algList.put((RadioButton) selectClassificationAlg.getSelectedToggle(), newAlg);
                    }
                } else {
                    if(algList.get((RadioButton) selectClusteringAlg.getSelectedToggle()).getClass().equals(RandomClusterer.class)) {
                        RandomClusterer newAlg = new RandomClusterer(
                                    ((AppData) applicationTemplate.getDataComponent()).getData(),
                                    Integer.parseInt(setMaxIt.getText()),
                                    Integer.parseInt(setUpdateInterval.getText()),
                                    contRun.isSelected(),
                                    Integer.parseInt(setNumberOfClusters.getText()));
                        newAlg.template = applicationTemplate;
                        algList.put((RadioButton) selectClusteringAlg.getSelectedToggle(), newAlg);
                    } else {
                        KMeansClusterer newAlg = new KMeansClusterer(
                                    ((AppData) applicationTemplate.getDataComponent()).getData(),
                                    Integer.parseInt(setMaxIt.getText()),
                                    Integer.parseInt(setUpdateInterval.getText()),
                                    contRun.isSelected(),
                                    Integer.parseInt(setNumberOfClusters.getText()));
                        newAlg.template = applicationTemplate;
                        algList.put((RadioButton) selectClusteringAlg.getSelectedToggle(), newAlg);
                    }
                }
                appPane.getChildren().remove(configPane);
                appPane.getChildren().addAll(toolBar, mainPane);
                run.setVisible(true);
            } catch(NumberFormatException ex) {
                dialog.show(manager.getPropertyValue((INVALID_ALGORITHM_PARAMETERS_TITLE).name()),
                            manager.getPropertyValue((INVALID_ALGORITHM_PARAMETERS).name()));
            }
            
        });
        
        configPane.getChildren().add(ret);
        return configPane;
    }
    
    private void setWorkspaceActions() {
        hasNewText = false;
        textArea.textProperty().addListener((final ObservableValue<? extends String> observable, final String oldValue, final String newValue) -> {
            if(!newValue.equals(oldValue)) {
                ((AppActions) applicationTemplate.getActionComponent()).setIsUnsaved(true);
                if(!newValue.equals("")) {
                    hasNewText = true;
                    newButton.setDisable(false);
                    saveButton.setDisable(false);
                } else {
                    hasNewText = false;
                    newButton.setDisable(true);
                    saveButton.setDisable(true);
                }
            }
        });
        
        alg1Settings.setOnAction(e -> {
            VBox config;
            if(isClassification) {
                config = configPane("Algorithm Configuration", algList.get((RadioButton) selectClassificationAlg.getSelectedToggle()), isClassification);
            } else {
                config = configPane("Algorithm Configuration", algList.get((RadioButton) selectClusteringAlg.getSelectedToggle()), isClassification) ;
            }
            appPane.getChildren().removeAll(toolBar, mainPane);
            appPane.getChildren().add(config);
        });
        
        alg2Settings.setOnAction(e -> {
            VBox config;
            if(isClassification) {
                config = configPane("Algorithm Configuration", algList.get((RadioButton) selectClassificationAlg.getSelectedToggle()), isClassification);
            } else {
                config = configPane("Algorithm Configuration", algList.get((RadioButton) selectClusteringAlg.getSelectedToggle()), isClassification) ;
            }
            appPane.getChildren().removeAll(toolBar, mainPane);
            appPane.getChildren().add(config);
        });
        
        selectAlgType.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable,
                        Toggle oldValue, Toggle newValue) -> {
                            if(selectAlgType.getSelectedToggle().equals(algType1)) {
                                showClassificationAlgs();
                                isClassification = true;
                            } else if(selectAlgType.getSelectedToggle().equals(algType2)) {
                                showClusteringAlgs();
                                isClassification = false;
                            }
                        });
        
        selectClassificationAlg.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable,
                        Toggle oldValue, Toggle newValue) -> {
                            run.setVisible(false);
                        });
        
        selectClusteringAlg.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable,
                        Toggle oldValue, Toggle newValue) -> {
                            run.setVisible(false);
                        });
        
        run.setOnAction(e -> {
            run.setDisable(true);
            if(flag.get() == 0) {
                flag.set(1);
                Thread alg;
                if(isClassification == true) {
                    alg = new Thread(algList.get((RadioButton) selectClassificationAlg.getSelectedToggle()));
                } else {
                    alg = new Thread(algList.get((RadioButton) selectClusteringAlg.getSelectedToggle()));
                }
                alg.start();
                runInProgress.set(true);
            } else {
                runInProgress.set(true);
            }
            
        });
        
        chart.setOnMouseEntered(e -> {
            chart.setCursor(Cursor.HAND);
        });
        
        chart.setOnMouseExited(e -> {
            chart.setCursor(Cursor.DEFAULT);
        });
        
        editText.setOnAction(e -> {
            clearChart();
            if(((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText())) {
                textArea.setDisable(!textArea.isDisable());
                showAlgTypes();
            }
        });
              
    }
    
    public void setLabels(String instanceC, String labelC, String labels, String sourceL) {
        PropertyManager manager = applicationTemplate.manager; 
        numLabels = Integer.parseInt(labelC);
        instanceCount.setText(manager.getPropertyValue(INSTANCE_COUNT_LABEL.name()) + instanceC);
        labelCount.setText(manager.getPropertyValue(LABEL_COUNT_LABEL.name()) + labelC);
        labelNames.setText(manager.getPropertyValue(LABEL_NAMES_LABEL.name()) + labels);
        source.setText(manager.getPropertyValue(SOURCE_LABEL.name()) + sourceL);
    }
    
    private void clearLabels() {
        instanceCount.setText("");
        labelCount.setText("");
        labelNames.setText("");
        source.setText("");
    }
    
    public void clearChart() {
        chart.getData().clear();
        scrnshotButton.setDisable(true);
    }
    
    public static int getGlobalTimer() {
        return globalTimer.get();
    }
    
    public static void resetGlobalTimer() {
        globalTimer.set(0);
    }
    
    public static synchronized void incrementGlobalTimer() {
        globalTimer.getAndIncrement();
    }
    
    public synchronized void addToChart(XYChart.Series<Number, Number> line) {
        chart.getData().add(line);
    }
    
    public void addSeriesToChart(Series s) {
        chart.getData().add(s);
        s.getNode().setStyle("-fx-stroke: transparent;");
    }
    
    public String getCurrentText() { return textArea.getText(); }
    
    public void setCurrentText(String data) { textArea.setText(data); }
    
    public void disableSaveButton(boolean b) {
        saveButton.setDisable(b);
    }
    
    public static boolean runInProgress() {
        return runInProgress.get();
    }
    
    public static void resetFlag() {
        flag.set(0);
    }
    
    public static void setRunInProgress(boolean b) {
        runInProgress.set(b);
    }
    
    public void showTextArea(boolean b) {
        textArea.setVisible(b);
    }
    
    public void disableScreenshotButton(boolean b) {
        scrnshotButton.setDisable(b);
    }
    
    public void disableRunButton(boolean b) {
        run.setDisable(b);
    }
    
    
    public void initNew() {
        clearLabels();
        algType1.setVisible(false);
        algType2.setVisible(false);
        textArea.setVisible(true);
        textArea.setDisable(false);
        editText.setVisible(true);
    }
    
    public void initLoad() {
        textArea.setVisible(true);
        editText.setVisible(false);
        textArea.setDisable(true);
        showAlgTypes();
    }
    
    private void showAlgTypes() {
        algType2.setVisible(true);
        if(numLabels >= 2) {
            algType1.setVisible(true);
        } else {
            algType1.setVisible(false);
        }
    }
    
    public void showClassificationAlgs() {
        alg1Layout.getChildren().clear();
        alg2Layout.getChildren().clear();
        alg1Layout.getChildren().addAll(classificationAlg1, alg1Settings);
        alg1Layout.setVisible(true);
    }
    
    public void showClusteringAlgs() {
        alg1Layout.getChildren().clear();
        alg2Layout.getChildren().clear();
        alg1Layout.getChildren().addAll(clusteringAlg1, alg1Settings);
        alg2Layout.getChildren().addAll(clusteringAlg2, alg2Settings);
        alg1Layout.setVisible(true);
        alg2Layout.setVisible(true);
    }
}