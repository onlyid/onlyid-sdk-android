package net.onlyid.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class OnlyID {
    public static final String EXTRA_CODE = "extraCode";
    public static final String EXTRA_STATE = "extraState";
    static final String TAG = "OnlyID";
    static final String PACKAGE = "net.onlyid";

    public static void startOAuth(Activity activity, int requestCode, String clientId) {
        startOAuth(activity, requestCode, clientId, null);
    }

    public static void startOAuth(Activity activity, int requestCode, String clientId, String state) {
        Intent intent;
        if (appInstalled(activity)) {
            intent = new Intent(PACKAGE + ".OAUTH");
            intent.setPackage(PACKAGE);
        } else {
            intent = new Intent(activity, OAuthActivity.class);
        }
        intent.putExtra("clientId", clientId);
        intent.putExtra("state", state);
        activity.startActivityForResult(intent, requestCode);
    }

    static boolean appInstalled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
