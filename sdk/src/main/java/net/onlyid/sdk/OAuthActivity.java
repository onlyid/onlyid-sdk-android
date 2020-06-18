package net.onlyid.sdk;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class OAuthActivity extends Activity {
    static final String TAG = "OnlyID";
    static final String MY_URL = "https://www.onlyid.net/oauth";

    ProgressBar progressBar;

    class JsInterface {
        @JavascriptInterface
        public void onCode(final String code, final String state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OnlyID.listener.onComplete(code, state);
                    finish();
                }
            });
        }

        @JavascriptInterface
        public void setTitle(final String title) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getActionBar().setTitle(title);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        getActionBar().setDisplayShowHomeEnabled(false);

        progressBar = findViewById(R.id.progress_bar);

        initWebView();
    }

    void initWebView() {
        WebView webView = findViewById(R.id.web_view);
        OnlyID.OAuthConfig config = (OnlyID.OAuthConfig) getIntent().getSerializableExtra("config");

        if ("dark".equals(config.theme)) {
            int colorDark = getResources().getColor(R.color.theme_dark);
            getActionBar().setBackgroundDrawable(new ColorDrawable(colorDark));
            webView.setBackgroundColor(colorDark);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        // 要使用localStorage 需要这句 不然会出错
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.w(TAG, "onReceivedError: " + description);
                OnlyID.listener.onError(OnlyID.ErrCode.NETWORK_ERR);
                finish();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        webView.addJavascriptInterface(new JsInterface(), "android");
        WebView.setWebContentsDebuggingEnabled(true);

        String url = MY_URL + "?client-id=" + config.clientId + "&package-name=" + getPackageName();
        if (!TextUtils.isEmpty(config.theme)) url += "&theme=" + config.theme;
        if (!TextUtils.isEmpty(config.view)) url += "&view=" + config.view;
        if (!TextUtils.isEmpty(config.state)) url += "&state=" + config.state;

        Log.d(TAG, "url= " + url);
        webView.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.oauth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel) {
            OnlyID.listener.onCancel();
            finish();
            return true;
        }

        return false;
    }
}
