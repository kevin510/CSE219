package ui;

import actions.AppActions;
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
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
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
    private RadioButton alg1, alg2;
    private ToggleGroup selectAlg;
    private Label instanceCount, labelCount, labelNames, source;
    
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
        alg1 = new RadioButton(manager.getPropertyValue(CLASSIFICATION_ALG.name()));
        alg2 = new RadioButton(manager.getPropertyValue(CLUSTERING_ALG.name()));
        selectAlg = new ToggleGroup();
        selectAlg.getToggles().addAll(alg1, alg2);
        
        instanceCount = new Label();
        labelCount = new Label();
        labelNames = new Label();
        source = new Label();
        
        leftPanel.getChildren().addAll(textArea, instanceCount, labelCount, labelNames, source, alg1, alg2);
        
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
                    newButton.setDisable(true);
                    saveButton.setDisable(true);
                }
            }
        });
        
        chart.setOnMouseEntered(e -> {
            chart.setCursor(Cursor.HAND);
        });
        
        chart.setOnMouseExited(e -> {
            chart.setCursor(Cursor.DEFAULT);
        });
              
    }
    
    public void setLabels(String instanceC, String labelC, String labels, String sourceL) {
        instanceCount.setText(instanceC);
        labelCount.setText(labelC);
        labelNames.setText(labels);
        source.setText(sourceL);
    }
    
    private void clearChart() {
        chart.getData().remove(0, (int) (chart.getData().size()));
        scrnshotButton.setDisable(true);
    }
    
    public String getCurrentText() { return textArea.getText(); }
    
    public void setCurrentText(String data) { textArea.setText(data); }
    
    public void disableSaveButton() {
        saveButton.setDisable(true);
    }
    
    public void enableScreenshotButton() {
        scrnshotButton.setDisable(false);
    }
}