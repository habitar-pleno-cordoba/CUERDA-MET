package com.cuerdamet.app;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.JavascriptInterface;
import android.content.ContentValues;
import android.provider.MediaStore;
import android.os.Build;
import android.widget.Toast;
import android.util.Base64;
import java.io.OutputStream;

public class MainActivity extends Activity {
  private WebView web;
  @Override public void onCreate(Bundle state) {
    super.onCreate(state);
    web=new WebView(this);
    web.getSettings().setJavaScriptEnabled(true);
    web.getSettings().setAllowFileAccess(true);
    web.getSettings().setDomStorageEnabled(true);
    web.setWebViewClient(new WebViewClient());
    web.setWebChromeClient(new WebChromeClient());
    web.addJavascriptInterface(new Object() {
      @JavascriptInterface public void saveSvg(String svg, String filename) {
        try {
          ContentValues values=new ContentValues();
          values.put(MediaStore.Downloads.DISPLAY_NAME,filename);
          values.put(MediaStore.Downloads.MIME_TYPE,"image/svg+xml");
          if(Build.VERSION.SDK_INT>=29) values.put(MediaStore.Downloads.RELATIVE_PATH,"Download/");
          android.net.Uri uri=getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,values);
          if(uri==null) throw new Exception("No se pudo crear archivo");
          try(OutputStream stream=getContentResolver().openOutputStream(uri)) { stream.write(svg.getBytes("UTF-8")); }
          runOnUiThread(()->Toast.makeText(MainActivity.this,"Plano guardado en Descargas",Toast.LENGTH_LONG).show());
        } catch(Exception ex) {runOnUiThread(()->Toast.makeText(MainActivity.this,"No se pudo guardar el plano",Toast.LENGTH_LONG).show());}
      }
    },"CuerdaAndroid");
    setContentView(web);
    web.loadUrl("file:///android_asset/index.html");
  }
  @Override public void onBackPressed(){ if(web.canGoBack())web.goBack();else super.onBackPressed(); }
}
