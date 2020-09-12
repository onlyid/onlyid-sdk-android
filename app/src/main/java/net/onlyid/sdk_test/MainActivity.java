package net.onlyid.sdk_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import net.onlyid.sdk.OAuthConfig;
import net.onlyid.sdk.OnlyID;

public class MainActivity extends Activity {
    static final String TAG = "OnlyID";
    static final int REQUEST_OAUTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button test = findViewById(R.id.test);
        test.setOnClickListener((View v) -> {
            // prd
            String clientId = "0958a5d2a9614ae2813397c1f3bc6b19";
            // dev
//            String clientId = "047f9fff2c2647529c7e5c69b1f40b0d";

            OAuthConfig config = new OAuthConfig(clientId);
            OnlyID.oauth(this, config, REQUEST_OAUTH);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_OAUTH) return;

        if (resultCode == RESULT_OK) {
            String code = data.getStringExtra(OnlyID.EXTRA_CODE);
            Log.d(TAG, "登录成功，code= " + code);
        } else if (resultCode == RESULT_CANCELED) {
            Log.d(TAG, "用户取消（拒绝）");
        } else if (resultCode == OnlyID.RESULT_ERROR) {
            Exception exception = (Exception) data.getSerializableExtra(OnlyID.EXTRA_EXCEPTION);
            Log.w(TAG, "发生错误", exception);
        }
    }
}