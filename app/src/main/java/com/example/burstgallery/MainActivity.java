package com.example.burstgallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

public class MainActivity extends AppCompatActivity {
    private static final String webViewUrl   = "https://www.burst.com/webapps/widgets/gallery/cards?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwaWQiOiJZSEdIRjdKODczNEgzS1AwMzdHS19JbnRlcm5hbFB1YmxpYyIsImJpZCI6IjUxNDgxNDc3MzU3NDQ3MTAwMCIsImJ0cCI6IkEiLCJ0dHAiOiJHIiwiaXNzIjoiYnVyc3Qud2lkZ2V0IiwiaWF0IjoxNTc0NzY5NTg1LCJleHAiOjE2Mzc4NDE1ODV9.b7LODWnmxK-oJ1TzAa8_vGPpgGXKmuBzezk469ikWUg";
    /* @todo: if already not exists, include below permissions  */
    private BurstWebView webView;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    /* end  */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* @todo: if already not exists, include below permissions  */
        webView = (BurstWebView) findViewById(R.id.burstWebview);
        webView.setListener(this);
        webView.loadUrl(webViewUrl);
        /* end  */
    }

    /* @todo: if onActivityResult function not already defined, you can copy paste as is, otherwise copy content inside of the function  */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        webView.onActivityResult(requestCode, resultCode, intent);

        if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE
        ) {
            if (!Settings.canDrawOverlays(this)) {
                Intent overlayIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(overlayIntent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
    /* end  */
}