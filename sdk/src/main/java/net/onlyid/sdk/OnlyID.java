package net.onlyid.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * SDK主入口
 */
public class OnlyID {
    public static final int RESULT_ERROR = 1;
    public static final String EXTRA_EXCEPTION = "extraException";
    public static final String EXTRA_CODE = "extraCode";
    public static final String EXTRA_STATE = "extraState";
    static final String TAG = "OnlyID";
    static final String PACKAGE = "net.onlyid";

    /**
     * 发起oauth请求，打开授权页
     */
    public static void oauth(Activity activity, OAuthConfig config, int requestCode) {
        if (appInstalled(activity)) {
            JSONObject jsonObject = new JSONObject();
            try {
                for (Field field : OAuthConfig.class.getFields()) {
                    jsonObject.put(field.getName(), field.get(config));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent("net.onlyid.OAUTH_ACTIVITY");
            intent.setPackage(PACKAGE);
            intent.putExtra("oauthConfig", jsonObject.toString());
            activity.startActivityForResult(intent, requestCode);
        } else {
            Intent intent = new Intent(activity, OAuthActivity.class);
            intent.putExtra("oauthConfig", config);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    static boolean appInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
