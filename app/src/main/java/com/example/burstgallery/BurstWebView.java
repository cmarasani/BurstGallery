/*
    @todo: update below package reference
 */
package com.example.burstgallery;
/*
    @todo: update below import reference
 */
import com.example.burstgallery.BurstWebViewClient;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.io.File;

public class BurstWebView extends WebView {
    private Activity mActivity = null;
    private Fragment mFragment = null;
    private ValueCallback<Uri[]> mFilePathCallback_5x;
    private ValueCallback<Uri> mFilePathCallback_4x;
    private static final int MEDIA_UPLOAD_REQUEST = 1;
    private String mCapturedImageURI;

    public BurstWebView(Context context) {
        super(context);
        init(context);
    }

    public BurstWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BurstWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BurstWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setListener(final Activity activity) {
        mActivity = activity;
    }

    public void setListener(final Fragment fragment) {
        mFragment = fragment;
    }

    public void loadUrl(String url) {
        super.loadUrl(url);
    }

    private void init(Context context) {
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
        setWebViewDefaults();
    }

    private void setWebViewDefaults() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);

        setWebChromeClient(new WebChromeClient() {
            // openFileChooser for Android < 3.0
            public void openFileChooser(ValueCallback<Uri> filePathCallback) {
                if (mFilePathCallback_4x != null) {
                    mFilePathCallback_4x.onReceiveValue(null);
                }
                mFilePathCallback_4x = filePathCallback;

                startActivity();
            }

            //For 3.0 - 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooser(uploadMsg);
            }

            // openFileChooser for Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg);
            }

            // > 5.x
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (
                        ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                ) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                    return false;
                }

                if (mFilePathCallback_5x != null) {
                    mFilePathCallback_5x.onReceiveValue(null);
                }
                mFilePathCallback_5x = filePathCallback;

                startActivity();

                return true;
            }
        });

        setWebViewClient(new BurstWebViewClient(mActivity) {});
    }

    private void startActivity() {
        final Intent chooserIntent = createChooserIntent();
        if (mFragment != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mFragment.startActivityForResult(chooserIntent, MEDIA_UPLOAD_REQUEST);
        }
        else if (mActivity != null) {
            mActivity.startActivityForResult(chooserIntent, MEDIA_UPLOAD_REQUEST);
        }
    }

    private Intent createChooserIntent() {
        final Intent imageCaptureIntent = this.createImageCaptureIntent();
        final Intent dockIntent = this.createDockIntent();
        final Intent voiceRecordeIntent = this.createVoiceReorderIntent();
        final Intent videoIntent = this.createVideoIntent();

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, dockIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Media Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{imageCaptureIntent, voiceRecordeIntent, videoIntent});

        return chooserIntent;
    }

    private Intent createDockIntent() {
        final Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imagePickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
        imagePickerIntent.setType("*/*");
        return imagePickerIntent;
    }

    private Intent createVideoIntent() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Video.mp4");
        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        return videoIntent;
    }

    private Intent createImageCaptureIntent() {
        final Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Avatar.jpg");
        this.mCapturedImageURI = file.getAbsolutePath();
        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        return imageCaptureIntent;
    }

    private Intent createVoiceReorderIntent() {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        return intent;
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode != MEDIA_UPLOAD_REQUEST) {
            return;
        }

        Uri results_4x = null;
        Uri[] results_5x = null;

        // Check that the response is a good one
        if(resultCode == Activity.RESULT_OK) {
            if(data == null || data.getDataString() == null) { // If there is not data, then we may have taken a photo

                String fileName = null;
                if(mCapturedImageURI != null) {
                    fileName = "Avatar.jpg";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        results_5x = new Uri[]{Uri.fromFile(new File(Environment.getExternalStorageDirectory(),  fileName))};
                    } else {
                        results_4x = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileName));
                    }
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5x
                        results_5x = new Uri[]{Uri.parse(dataString)};
                    } else {//<5x
                        Uri result = data.getData();
                        final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION  | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        String path = getPath( mActivity, result);
                        if (path != null) {
                            File selectedFile = new File(path);
                            results_4x = Uri.fromFile(selectedFile);
                        }
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mFilePathCallback_5x.onReceiveValue(results_5x);
        } else  {
            mFilePathCallback_4x.onReceiveValue(results_4x);
        }
        mFilePathCallback_5x = null;
        mFilePathCallback_4x = null;
        mCapturedImageURI = null;
        return;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @source http://stackoverflow.com/a/20559175
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @source http://stackoverflow.com/a/20559175
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @source http://stackoverflow.com/a/20559175
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @source http://stackoverflow.com/a/20559175
     */
    public String getDataColumn(Activity context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     * @source http://stackoverflow.com/a/20559175
     */
    public String getPath(final Activity context, final Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
