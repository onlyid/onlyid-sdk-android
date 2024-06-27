package net.onlyid.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.onlyid.sdk.OnlyID;

public class MainActivity extends Activity {
    static final String TAG = "OnlyID";
    static final int REQUEST_OAUTH = 1;

    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = findViewById(R.id.login_button);
        resultTextView = findViewById(R.id.result_text_view);

        loginButton.setOnClickListener((View v) -> {
            String clientId = "ac426a26a1ca0c1e"; // prd 运行demo的开发者使用这个
//            String clientId = "aff330d4d20c3955"; // dev 内部开发使用，请忽略
            OnlyID.startOAuth(this, REQUEST_OAUTH, clientId);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_OAUTH) return;

        if (resultCode == RESULT_OK) {
            String code = data.getStringExtra(OnlyID.EXTRA_CODE);
            resultTextView.setText("登录成功，code= " + code);
            // 得到code后，使用code换取用户信息的逻辑三端一致（Web、Android、iOS），详见官网文档
        } else {
            resultTextView.setText("用户取消");
        }
    }
}
