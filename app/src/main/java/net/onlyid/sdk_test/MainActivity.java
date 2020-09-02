package net.onlyid.sdk_test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import net.onlyid.sdk.OnlyID;

public class MainActivity extends Activity {
    private static final String TAG = "OnlyID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button test = findViewById(R.id.test);
        test.setOnClickListener((View v) -> {
            OnlyID.OAuthConfig config = new OnlyID.OAuthConfig("0958a5d2a9614ae2813397c1f3bc6b19");
            OnlyID.oauth(this, config, new OnlyID.OAuthListener() {
                @Override
                public void onComplete(String code, String state) {
                    Log.d(TAG, "onComplete: code= " + code + ", state= " + state);
                }

                @Override
                public void onError(OnlyID.ErrCode errCode) {
                    Log.d(TAG, "onError: " + errCode + ", msg= " + errCode.msg);
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "onCancel");
                }
            });
        });
    }
}