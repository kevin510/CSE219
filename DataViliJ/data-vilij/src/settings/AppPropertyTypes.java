package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    DATA_RESOURCE_PATH,
    
    DATA_VILIJ_RESOURCE_PATH,
    
    RESOURCE_PATH,
    
    DATA_VILIJ_CSS_PATH,
    
    DATA_VILIJ_CSS_NAME,

    /* user interface icon file names */
    SCREENSHOT_ICON,
    RUN_ICON,
    SETTINGS_ICON,
    EDIT_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,
    RUN_TOOLTIP,
    SETTINGS_TOOLTIP,
    EDIT_TOOLTIP,

    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,

    /* application-specific message titles */
    LOAD_WORK_TITLE,
    SAVE_UNSAVED_WORK_TITLE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,

    /* application-specific parameters */
    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    TEXT_AREA,
    SPECIFIED_FILE,
    DISPLAY_BUTTON_TEXT,
    CHART_TITLE,
    LOADING_10_OF,
    PNG_FILE_EXT,
    PNG_FILE_EXT_DESC,
    CLASSIFICATION_ALG,
    CLUSTERING_ALG,
    INSTANCE_COUNT_LABEL,
    LABEL_COUNT_LABEL,
    LABEL_NAMES_LABEL,
    SOURCE_LABEL,
    MAX_ITERATIONS_LABEL,
    UPDATE_INTERVAL_LABEL,
    CONTINUOUS_RUN_LABEL,
    RETURN_LABEL,
    CLUSTERING_NUMBER_LABEL,
    EXIT_WHILE_RUNNING,
    EXIT_WHILE_RUNNING_TITLE,
    INVALID_ALGORITHM_PARAMETERS_TITLE,
    INVALID_ALGORITHM_PARAMETERS
}
