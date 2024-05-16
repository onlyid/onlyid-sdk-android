package net.onlyid.sdk;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

public class OnlyID {
    public static final String EXTRA_CODE = "extraCode";
    public static final String EXTRA_STATE = "extraState";
    static final String TAG = "OnlyID";

    public static void startOAuth(Activity activity, int requestCode, String clientId) {
        startOAuth(activity, requestCode, clientId, null);
    }

    public static void startOAuth(Activity activity, int requestCode, String clientId, String state) {
        try {
            Intent intent = new Intent("net.onlyid.OAUTH");
            intent.setPackage("net.onlyid");
            intent.putExtra("clientId", clientId);
            intent.putExtra("state", state);
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(activity, OAuthActivity.class);
            intent.putExtra("clientId", clientId);
            intent.putExtra("state", state);
            activity.startActivityForResult(intent, requestCode);
        }
    }
}
