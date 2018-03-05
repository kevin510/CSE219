package actions;

import java.io.File;
import static java.io.File.separator;
import java.io.FileNotFoundException;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import vilij.components.ConfirmationDialog;

import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import settings.AppPropertyTypes;
import static settings.AppPropertyTypes.LOAD_WORK_TITLE;
import ui.AppUI;

import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import static vilij.settings.PropertyTypes.SAVE_WORK_TITLE;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;

    /** Path to the data file currently active. */
    Path dataFilePath;
    
    boolean isUnsaved;

    public void setIsUnsaved(boolean b) {
        isUnsaved = b;
    }
    
    public boolean getIsUnsaved() {
        return isUnsaved;
    }
    
    public boolean pathNull() {
        return !(dataFilePath == null && !Files.exists(dataFilePath));
    }
    
    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
        this.isUnsaved = false;
    }

    @Override
    public void handleNewRequest() {
        try {
            if (isUnsaved == true) {
                boolean b = promptToSave();
                if(b == true) {
                    applicationTemplate.getDataComponent().clear();
                    applicationTemplate.getUIComponent().clear();
                    isUnsaved = false;
                    dataFilePath = null;
                }
            } else {
                applicationTemplate.getDataComponent().clear();
                applicationTemplate.getUIComponent().clear();
                isUnsaved = false;
                dataFilePath = null;
            }
        } catch (IOException e) { 
            ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager  = applicationTemplate.manager;
            String errTitle = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name());
            String errMsg   = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name());
            String errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
            dialog.show(errTitle, errMsg + errInput);
        }
    }

    @Override
    public void handleSaveRequest() {
        try {
            if(isUnsaved == true) {
               promptToSave(); 
            }       
        } catch (IOException e) {
            ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager  = applicationTemplate.manager;
            String errTitle = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name());
            String errMsg   = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name());
            String errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
            dialog.show(errTitle, errMsg + errInput);
        }
    }

    @Override
    public void handleLoadRequest() {
        if(isUnsaved == true) {
            handleNewRequest();
        }
        try {
            loadHelper();
        } catch (IOException ex) {
            ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager  = applicationTemplate.manager;
            String errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
            String errMsg   = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
            String errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
            dialog.show(errTitle, errMsg + errInput);
        }
    }

    @Override
    public void handleExitRequest() {
        System.exit(0);
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        WritableImage image = ((AppUI) applicationTemplate.getUIComponent()).getChart()
                .snapshot(new SnapshotParameters(), null);
        PropertyManager    manager = applicationTemplate.manager;
        FileChooser fileChooser = new FileChooser();
                String      dataDirPath = separator + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
                URL         dataDirURL  = getClass().getResource(dataDirPath);

                if (dataDirURL == null)
                    throw new FileNotFoundException(manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));

                fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
                fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));

                String description = manager.getPropertyValue(AppPropertyTypes.PNG_FILE_EXT_DESC.name());
                String extension   = manager.getPropertyValue(AppPropertyTypes.PNG_FILE_EXT.name());
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(String.format("%s (.*%s)", description, extension),
                                                                String.format("*.%s", extension));

                fileChooser.getExtensionFilters().add(extFilter);
                File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
                if(selected != null) {
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", selected); // TODO: handle exception here
                    } catch(IOException e) {

                    }
                }
    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        
        PropertyManager    manager = applicationTemplate.manager;
        ConfirmationDialog dialog  = ConfirmationDialog.getDialog();
        dialog.show(manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK_TITLE.name()),
                    manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK.name()));

        if (dialog.getSelectedOption() == null) return false; // if user closes dialog using the window's close button

        if (dialog.getSelectedOption().equals(ConfirmationDialog.Option.YES)) {
            if (dataFilePath == null) {
                FileChooser fileChooser = new FileChooser();
                String      dataDirPath = separator + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
                URL         dataDirURL  = getClass().getResource(dataDirPath);

                if (dataDirURL == null)
                    throw new FileNotFoundException(manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));

                fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
                fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));

                String description = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name());
                String extension   = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name());
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(String.format("%s (.*%s)", description, extension),
                                                                String.format("*.%s", extension));

                fileChooser.getExtensionFilters().add(extFilter);
                File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
                if (selected != null) {
                    dataFilePath = selected.toPath();
                    save();
                } else return false; // if user presses escape after initially selecting 'yes'
            } else
                save();
        }
        return !dialog.getSelectedOption().equals(ConfirmationDialog.Option.CANCEL);
    }
    
    private boolean loadHelper() throws IOException {
        PropertyManager    manager = applicationTemplate.manager;
        FileChooser fileChooser = new FileChooser();
            String      dataDirPath = separator + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
            URL         dataDirURL  = getClass().getResource(dataDirPath);
            if (dataDirURL == null)
                    throw new FileNotFoundException(manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));

                fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
                fileChooser.setTitle(manager.getPropertyValue(LOAD_WORK_TITLE.name()));
                
        File selected = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        if (selected != null) {
            dataFilePath = selected.toPath();
            load();
        } else return false;
        return true;
    }
        
    private void save() throws IOException {
        applicationTemplate.getDataComponent().saveData(dataFilePath);
        ((AppUI) applicationTemplate.getUIComponent()).disableSaveButton();
        isUnsaved = false;
    }
        
    private void load() throws IOException {
        applicationTemplate.getDataComponent().loadData(dataFilePath);
        ((AppUI) applicationTemplate.getUIComponent()).disableSaveButton();
        isUnsaved = false;
    }
}
