package ui;

import actions.AppActions;
import dataprocessors.AppData;
import static java.io.File.separator;
import java.io.IOException;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
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
import static vilij.settings.PropertyTypes.CSS_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;
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
    private String scrnshotIconPath;
    private String cssPathUI;
    private RadioButton algType1, algType2;
    private ToggleGroup selectAlgType;
    private Label instanceCount, labelCount, labelNames, source;
    private Button editText;
    private int numLabels;
    private RadioButton alg1, alg2, alg3;
    private ToggleGroup selectAlg;
    private HBox alg1Layout, alg2Layout, alg3Layout;
    private Button run;
    
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
        GridPane mainPane = new GridPane();
        
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
        
        alg1 = new RadioButton("Algorithm 1 ");
        alg2 = new RadioButton("Algorithm 2 ");
        alg3 = new RadioButton("Algorithm 3 ");
        selectAlg = new ToggleGroup();
        selectAlg.getToggles().addAll(alg1, alg2, alg3);
        
        
        alg1Layout = new HBox();
        alg2Layout = new HBox();
        alg3Layout = new HBox();
        alg1Layout.getChildren().addAll(alg1, new Button("Settings"));
        alg2Layout.getChildren().addAll(alg2, new Button("Settings"));
        alg3Layout.getChildren().addAll(alg3, new Button("Settings"));
        alg1Layout.setVisible(false);
        alg2Layout.setVisible(false);
        alg3Layout.setVisible(false);
        
        run = new Button("Run");
        run.setVisible(false);
        
        instanceCount = new Label();
        instanceCount.setWrapText(true);
        labelCount = new Label();
        labelCount.setWrapText(true);
        labelNames = new Label();
        labelNames.setWrapText(true);
        source = new Label();
        source.setWrapText(true);
        
        editText = new Button("Edit");
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
                    //newButton.setDisable(true);
                    saveButton.setDisable(true);
                }
            }
        });
        
        selectAlgType.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable,
                        Toggle oldValue, Toggle newValue) -> {
                            if(selectAlgType.getSelectedToggle() != null) {
                                showAlgs();
                            }
                        });
        
        selectAlg.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable,
                        Toggle oldValue, Toggle newValue) -> {
                            if(selectAlg.getSelectedToggle() != null) {
                                run.setVisible(true);
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
        textArea.setVisible(true);
        textArea.setDisable(false);
        editText.setVisible(true);
    }
    
    public void showAlgTypes() {
        algType2.setVisible(true);
        if(numLabels == 2) {
            algType1.setVisible(true);
        } else {
            algType1.setVisible(false);
        }
    }
    
    public void showAlgs() {
        alg1Layout.setVisible(true);
        alg2Layout.setVisible(true);
        alg3Layout.setVisible(true);
    }
}