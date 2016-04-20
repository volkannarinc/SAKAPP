package com.one.sakap;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by cscmehmet on 28.08.2015.
 */
public class BusTime extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acrivity_bustime);

        WebView webview = (WebView) findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("http://sakus.sakarya.bel.tr/Sbb/SakusOtobusSaat?hatNo=" + SystemValues.busName);
    }
}
