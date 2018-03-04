package dataprocessors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.templates.ApplicationTemplate;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import settings.AppPropertyTypes;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        try {
            FileReader reader = new FileReader(dataFilePath.toString());
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder toWrite = new StringBuilder();
            toWrite.setLength(0);
            String line;
            while((line = bufferedReader.readLine()) != null) {
                toWrite.append(line).append("\n");
            }
            processor.processString(toWrite.toString());
            loadData(toWrite.toString());
        } catch (Exception e) {
            if(e.getMessage().length() > 1) {
                ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                PropertyManager manager  = applicationTemplate.manager;
                String errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
                String errMsg = e.getMessage();
                String errMsg2 = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
                String errInput = dataFilePath.toString();
                dialog.show(errTitle, errMsg + errMsg2 + errInput);
            } else {
                ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                PropertyManager manager  = applicationTemplate.manager;
                String errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
                String errMsg = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
                String errInput = dataFilePath.toString();
                dialog.show(errTitle, errMsg + errInput);
            }
    }
}
    
    public void loadData(String dataString) {
        AtomicBoolean hadError = new AtomicBoolean(false);
        try {
            processor.processString(dataString);
            displayData();
        } catch (Exception e) {
            if(e.getMessage().length() > 1) {
                ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                PropertyManager manager  = applicationTemplate.manager;
                String errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
                String errMsg = e.getMessage();
                String errMsg2 = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
                String errInput = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
                hadError.set(true);
                dialog.show(errTitle, errMsg + errMsg2 + errInput);
            } else {
                ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                PropertyManager manager  = applicationTemplate.manager;
                String errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
                String errMsg = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
                String errInput = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
                hadError.set(true);
                dialog.show(errTitle, errMsg + errInput);
            }
        }

    }

    @Override
    public void saveData(Path dataFilePath) {
        AtomicBoolean hadError = new AtomicBoolean(false);
        try  {
            processor.processString(((AppUI) applicationTemplate.getUIComponent()).getCurrentText());
        } catch (Exception e) {
            ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager  = applicationTemplate.manager;
            String errTitle = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name());
            String errMsg = e.getMessage();
            String errMsg2 = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name());
            String errInput = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
            dialog.show(errTitle, errMsg + errMsg2 + errInput);
            hadError.set(true);
        }
        if(hadError.get() == false) {
            PrintWriter writer;
            try {
                writer = new PrintWriter(Files.newOutputStream(dataFilePath));
                writer.write(((AppUI) applicationTemplate.getUIComponent()).getCurrentText());
            } catch (IOException ex) {
                ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                PropertyManager manager  = applicationTemplate.manager;
                String errTitle = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name());
                String errMsg = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name()) + dataFilePath;
                dialog.show(errTitle, errMsg);
            }
        }
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }
}
