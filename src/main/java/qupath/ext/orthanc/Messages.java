package qupath.ext.orthanc;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class for internationalization (i18n).
 * Load strings from messages_XX.properties based on the current locale.
 */
public class Messages {

    private static ResourceBundle bundle = ResourceBundle.getBundle(
            "qupath.ext.orthanc.ui.messages", Locale.getDefault());

    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }
}
