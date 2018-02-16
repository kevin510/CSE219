package actions;

import java.io.File;
import static java.io.File.separator;
import java.io.FileWriter;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import vilij.components.ConfirmationDialog;
import vilij.components.ConfirmationDialog.Option;
import static vilij.components.Dialog.DialogType.CONFIRMATION;
import static vilij.components.Dialog.DialogType.ERROR;
import javafx.stage.FileChooser;
import static settings.AppPropertyTypes.DATA_FILE_EXT;
import static settings.AppPropertyTypes.DATA_RESOURCE_PATH;
import static settings.AppPropertyTypes.DATA_VILIJ_RESOURCE_PATH;
import static settings.AppPropertyTypes.RESOURCE_PATH;
import static settings.AppPropertyTypes.SAVE_UNSAVED_WORK_TITLE;

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

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        ConfirmationDialog cd = (ConfirmationDialog) applicationTemplate.getDialog(CONFIRMATION);
        cd.show("Warning: Unsaved Data","Would you like to save your data before it is deleted?");
        Option o = cd.getSelectedOption();
        if(o != null) {
            switch(o) {
                case YES:
                    try {
                        promptToSave();
                    } catch (IOException ex) {
                        applicationTemplate.getDialog(ERROR).show(ex.getLocalizedMessage(), ex.getMessage());
                    }
                    applicationTemplate.getUIComponent().clear();
                    break;
                case NO:
                    applicationTemplate.getUIComponent().clear();
                    break;
                case CANCEL:
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
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
        // TODO: NOT A PART OF HW 1
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
        FileChooser fc = new FileChooser();
        fc.setTitle(applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()));
        fc.setInitialFileName(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name()));
        /*String initDirStr = "/" + String.join(separator,
                //Paths.get(".").toAbsolutePath().normalize().toString(),
                applicationTemplate.manager.getPropertyValue(DATA_VILIJ_RESOURCE_PATH.name()),
                applicationTemplate.manager.getPropertyValue(RESOURCE_PATH.name()),
                applicationTemplate.manager.getPropertyValue(DATA_RESOURCE_PATH.name())) + "/";
        File f = new File(initDirStr);
        fc.setInitialDirectory(f);*/
        File toSave = fc.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        FileWriter fw = new FileWriter(toSave);
        //fw.write(applicationTemplate.getUIComponent().getTextArea().getText());
        
        return false;
    }
}
