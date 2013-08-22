package be.kuleuven.noiseapp;

import android.content.Context;
import android.content.Intent;
import be.kuleuven.noiseapp.tools.Constants;
 
public final class CommonUtilities {
     
    // give your server registration url here
    public static final String SERVER_URL = Constants.BASE_URL_MYSQL + "gcm/register.php"; 
 
    // Google project id
    public static final String SENDER_ID = "736255737975"; 
 
    /**
     * Tag used on log messages.
     */
    static final String TAG = "NoiseMApp GCM";
 
    public static final String DISPLAY_MESSAGE_ACTION =
            "be.kuleuven.noiseapp.gcm.DISPLAY_MESSAGE";
 
    public static final String EXTRA_MESSAGE = "message";
 
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
