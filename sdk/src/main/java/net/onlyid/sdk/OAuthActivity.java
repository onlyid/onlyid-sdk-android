package net.onlyid.sdk;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class OAuthActivity extends Activity {
    static final String MY_URL = "https://www.onlyid.net/oauth";

    ProgressBar progressBar;
    WebView webView;
    ValueCallback<Uri[]> filePathCallback;
    ActionBar actionBar;

    class JsInterface {
        @JavascriptInterface
        public void onCode(final String code, final String state) {
            Intent data = new Intent();
            data.putExtra(OnlyID.EXTRA_CODE, code);
            data.putExtra(OnlyID.EXTRA_STATE, state);
            setResult(RESULT_OK, data);
            finish();
        }

        @JavascriptInterface
        public void setTitle(final String title) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    actionBar.setTitle(title);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);

        progressBar = findViewById(R.id.progress_bar);
        webView = findViewById(R.id.web_view);

        initWebView();
    }

    void initWebView() {
        OAuthConfig config = (OAuthConfig) getIntent().getSerializableExtra("oauthConfig");

        if ("dark".equals(config.theme)) {
            int colorDark = getResources().getColor(R.color.theme_dark);
            actionBar.setBackgroundDrawable(new ColorDrawable(colorDark));
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

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                OAuthActivity.this.filePathCallback = filePathCallback;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.w(OnlyID.TAG, "onReceivedError: " + description);
                Intent data = new Intent();
                Exception exception = new Exception("网络错误，请检查：" + description);
                data.putExtra(OnlyID.EXTRA_EXCEPTION, exception);
                setResult(OnlyID.RESULT_ERROR, data);
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

        Log.d(OnlyID.TAG, "url= " + url);
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
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 1) return;

        if (resultCode != RESULT_OK) {
            filePathCallback.onReceiveValue(null);
            return;
        }

        Uri uri = data.getData();
        filePathCallback.onReceiveValue(new Uri[]{uri});
    }
}
