package ui;

import actions.AppActions;
import algorithms.AlgorithmParameters;
import classification.ClassificationParameters;
import classification.RandomClassifier;
import clustering.ClusteringParameters;
import dataprocessors.AppData;
import static java.io.File.separator;
import java.io.IOException;
import java.util.HashMap;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
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
    private RadioButton clusteringAlg1, clusteringAlg2, clusteringAlg3;
    private RadioButton classificationAlg1, classificationAlg2, classificationAlg3;
    private ToggleGroup selectClassificationAlg, selectClusteringAlg;
    private HBox alg1Layout, alg2Layout, alg3Layout;
    private Button alg1Settings, alg2Settings, alg3Settings, run;
    private GridPane mainPane;
    private boolean isClassification;
    private RandomClassifier random;
    private HashMap<RadioButton, AlgorithmParameters> algList = new HashMap<>();
    
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
        
        VBox leftPanel = new VBox();
        mainPane = new GridPane();
        
        textArea = new TextArea();
        textArea.setVisible(false);
        textArea.setDisable(true);
        //textArea.getStyleClass().add("text-area");
        textArea.setPrefRowCount(10);
        algType1 = new RadioButton(manager.getPropertyValue(CLASSIFICATION_ALG.name()));
        algType2 = new RadioButton(manager.getPropertyValue(CLUSTERING_ALG.name()));
        algType1.setVisible(false);
        algType2.setVisible(false);
        selectAlgType = new ToggleGroup();
        selectAlgType.getToggles().addAll(algType1, algType2);
        
        classificationAlg1 = new RadioButton("RandomClassifier ");
        algList.put(classificationAlg1, new ClassificationParameters());
        classificationAlg2 = new RadioButton("Classification Algorithm 2 ");
        algList.put(classificationAlg2, new ClassificationParameters());
        classificationAlg3 = new RadioButton("Classification Algorithm 3 ");
        algList.put(classificationAlg3, new ClassificationParameters());
        selectClassificationAlg = new ToggleGroup();
        selectClassificationAlg.getToggles().addAll(classificationAlg1, classificationAlg2, classificationAlg3);
        
        clusteringAlg1 = new RadioButton("Clustering Algorithm 1 ");
        algList.put(clusteringAlg1, new ClusteringParameters());
        clusteringAlg2 = new RadioButton("Clustering Algorithm 2 ");
        algList.put(clusteringAlg2, new ClusteringParameters());
        clusteringAlg3 = new RadioButton("Clustering Algorithm 3 ");
        algList.put(clusteringAlg3, new ClusteringParameters());
        selectClusteringAlg = new ToggleGroup();
        selectClusteringAlg.getToggles().addAll(clusteringAlg1, clusteringAlg2, clusteringAlg3);
        
        
        alg1Layout = new HBox();
        alg2Layout = new HBox();
        alg3Layout = new HBox();
        
        alg1Settings = setToolbarButton(settingsIconPath, applicationTemplate.manager.getPropertyValue(SETTINGS_TOOLTIP.name()), false);
        alg2Settings = setToolbarButton(settingsIconPath, applicationTemplate.manager.getPropertyValue(SETTINGS_TOOLTIP.name()), false);
        alg3Settings = setToolbarButton(settingsIconPath, applicationTemplate.manager.getPropertyValue(SETTINGS_TOOLTIP.name()), false);
        
        alg1Layout.setVisible(false);
        alg2Layout.setVisible(false);
        alg3Layout.setVisible(false);
        
        run = setToolbarButton(runIconPath, applicationTemplate.manager.getPropertyValue(RUN_TOOLTIP.name()), true);
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
        alg1Layout, alg2Layout, alg3Layout, run);
        
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis(); 
        chart = new LineChart(xAxis, yAxis);
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
    
    private VBox configPane(String title, AlgorithmParameters P, boolean classification) {
        PropertyManager manager = applicationTemplate.manager;
        
        VBox configPane = new VBox();
        Label T = new Label(title);
        
        Label maxIt = new Label(manager.getPropertyValue(MAX_ITERATIONS_LABEL.name()));
        TextArea setMaxIt = new TextArea();
        setMaxIt.setText(Integer.toString(P.getMaxIterations()));
        setMaxIt.setPrefRowCount(1);
        setMaxIt.setPrefColumnCount(10);
        
        Label updateInterval = new Label(manager.getPropertyValue(UPDATE_INTERVAL_LABEL.name()));
        TextArea setUpdateInterval = new TextArea();
        setUpdateInterval.setText(Integer.toString(P.getUpdateInterval()));
        setUpdateInterval.setPrefRowCount(1);
        setUpdateInterval.setPrefColumnCount(10);
        
        CheckBox contRun = new CheckBox(manager.getPropertyValue(CONTINUOUS_RUN_LABEL.name()));
        contRun.setSelected(P.isContinuous());
        
        Button ret = new Button(manager.getPropertyValue(RETURN_LABEL.name()));
        
        configPane.getChildren().addAll(T, maxIt, setMaxIt, updateInterval, setUpdateInterval, contRun);
        
        TextArea setNumberOfClusters = new TextArea();
        setNumberOfClusters.setVisible(false);
        if(classification == false) {
            Label numberOfClusters = new Label(manager.getPropertyValue(CLUSTERING_NUMBER_LABEL.name()));
            setNumberOfClusters.setText(Integer.toString(((ClusteringParameters) P).numberOfClusters()));
            setNumberOfClusters.setVisible(true);
            setNumberOfClusters.setPrefRowCount(1);
            setNumberOfClusters.setPrefColumnCount(10);
            configPane.getChildren().addAll(numberOfClusters, setNumberOfClusters);
        }
        
        ret.setOnAction(e -> {
            try {
                if(classification) {
                    ClassificationParameters Pa = new ClassificationParameters(Integer.parseInt(setMaxIt.getText()),
                                    Integer.parseInt(setUpdateInterval.getText()),
                                    contRun.isSelected());
                    algList.put((RadioButton) selectClassificationAlg.getSelectedToggle(), Pa);
                } else {
                    ClusteringParameters Pb = new ClusteringParameters(Integer.parseInt(setMaxIt.getText()),
                                    Integer.parseInt(setUpdateInterval.getText()),
                                    contRun.isSelected(), Integer.parseInt(setNumberOfClusters.getText()));
                    algList.put((RadioButton) selectClusteringAlg.getSelectedToggle(), Pb);
                }
                appPane.getChildren().remove(configPane);
                appPane.getChildren().addAll(toolBar, mainPane);
            } catch(Exception ex) {
                
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
        
        alg3Settings.setOnAction(e -> {
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
        
//        selectClassificationAlg.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable,
//                        Toggle oldValue, Toggle newValue) -> {
//                            run.setVisible(true);
//                        });
//        
//        selectClusteringAlg.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable,
//                        Toggle oldValue, Toggle newValue) -> {
//                            run.setVisible(true);
//                        });
        
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
    
    private void clearChart() {
        chart.getData().remove(0, (int) (chart.getData().size()));
        scrnshotButton.setDisable(true);
    }
    
    public String getCurrentText() { return textArea.getText(); }
    
    public void setCurrentText(String data) { textArea.setText(data); }
    
    public void disableSaveButton(boolean b) {
        saveButton.setDisable(b);
    }
    
    public void showTextArea(boolean b) {
        textArea.setVisible(b);
    }
    
    public void disableScreenshotButton(boolean b) {
        scrnshotButton.setDisable(b);
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
        alg3Layout.getChildren().clear();
        alg1Layout.getChildren().addAll(classificationAlg1, alg1Settings);
        alg2Layout.getChildren().addAll(classificationAlg2, alg2Settings);
        alg3Layout.getChildren().addAll(classificationAlg3, alg3Settings);
        alg1Layout.setVisible(true);
        alg2Layout.setVisible(true);
        alg3Layout.setVisible(true);
    }
    
    public void showClusteringAlgs() {
        alg1Layout.getChildren().clear();
        alg2Layout.getChildren().clear();
        alg3Layout.getChildren().clear();
        alg1Layout.getChildren().addAll(clusteringAlg1, alg1Settings);
        alg2Layout.getChildren().addAll(clusteringAlg2, alg2Settings);
        alg3Layout.getChildren().addAll(clusteringAlg3, alg3Settings);
        alg1Layout.setVisible(true);
        alg2Layout.setVisible(true);
        alg3Layout.setVisible(true);
    }
}