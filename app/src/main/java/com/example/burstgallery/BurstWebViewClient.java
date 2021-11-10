/*
    @todo: update below package reference
 */
package com.example.burstgallery;

/*
    @source https://stackoverflow.com/questions/18874282/webview-email-link-mailto
    Code that is responsible for opening mailing options on clicking 'mailto' link
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.net.MailTo;

import java.lang.ref.WeakReference;

public class BurstWebViewClient extends WebViewClient {
    private final WeakReference<Activity> mActivityRef;

    public BurstWebViewClient(Activity activity) {
        mActivityRef = new WeakReference<Activity>(activity);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("mailto:")) {
            final Activity activity = mActivityRef.get();
            if (activity != null) {
                MailTo mt = MailTo.parse(url);
                Intent i = newEmailIntent(activity, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                activity.startActivity(i);
                view.reload();
                return true;
            }
        } else {
            view.loadUrl(url);
        }
        return true;
    }

    private Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }
}
