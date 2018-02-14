package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
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
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    
    public ScatterChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        textArea.clear();
    }

    private void layout() {
        FlowPane mainPane = new FlowPane();
        appPane.getChildren().add(mainPane);
        textArea = new TextArea();
        displayButton = new Button("Display");
        scrnshotButton = new Button("Screenshot");
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis(); 
        chart = new ScatterChart(xAxis, yAxis);
        mainPane.getChildren().add(textArea);
        mainPane.getChildren().add(displayButton);
        mainPane.getChildren().add(scrnshotButton);
        mainPane.getChildren().add(chart);
        
    }

    private void setWorkspaceActions() {
        hasNewText = false;
        textArea.textProperty().addListener((final ObservableValue<? extends String> observable, final String oldValue, final String newValue) -> {           
            newButton.setDisable(false);
        });
        
        displayButton.setOnAction((ActionEvent e) -> {
            if(hasNewText) {
                //confirmation 
            }
            if(!textArea.getText().equals("")) {
                hasNewText = true;
                String plot = textArea.getText();                           
                AppData dat = (AppData) applicationTemplate.getDataComponent();
                dat.loadData(plot);               
            }
            System.out.println("clicked");
        });
    }
}
