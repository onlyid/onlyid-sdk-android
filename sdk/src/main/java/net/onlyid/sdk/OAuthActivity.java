package net.onlyid.sdk;

import static net.onlyid.sdk.OnlyID.TAG;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class OAuthActivity extends Activity {
    static final String MY_URL = "https://onlyid.net/oauth";

    ActionBar actionBar;
    ProgressBar progressBar;
    WebView webView;
    ValueCallback<Uri[]> filePathCallback;

    class JsInterface {
        @JavascriptInterface
        public void onSuccess(String code, String state) {
            Intent data = new Intent();
            data.putExtra(OnlyID.EXTRA_CODE, code);
            data.putExtra(OnlyID.EXTRA_STATE, state);
            setResult(RESULT_OK, data);
            finish();
        }

        @JavascriptInterface
        public void setTitle(String title) {
            runOnUiThread(() -> actionBar.setTitle(title));
        }
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Toast.makeText(OAuthActivity.this, "⚠️网络错误，请检查", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
        }
    };

    WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                         WebChromeClient.FileChooserParams fileChooserParams) {
            OAuthActivity.this.filePathCallback = filePathCallback;

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.only_activity_oauth);

        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.only_ic_close);

        progressBar = findViewById(R.id.progress_bar);
        webView = findViewById(R.id.web_view);

        initWebView();
    }

    void initWebView() {
        Intent intent = getIntent();
        String clientId = intent.getStringExtra("clientId");
        String state = intent.getStringExtra("state");

        webView.getSettings().setJavaScriptEnabled(true);
        // 要使用localStorage 需要这句 不然会出错
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);
        webView.addJavascriptInterface(new JsInterface(), "android");
        WebView.setWebContentsDebuggingEnabled(true);

        String url = MY_URL + "?client-id=" + clientId + "&package-name=" + getPackageName();
        if (!TextUtils.isEmpty(state)) url += "&state=" + state;

        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 1) return;

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            filePathCallback.onReceiveValue(new Uri[]{uri});
        } else {
            filePathCallback.onReceiveValue(null);
        }
    }
}
