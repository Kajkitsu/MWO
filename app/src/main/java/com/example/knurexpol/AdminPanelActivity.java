package com.example.knurexpol;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import static com.example.knurexpol.AdminFunctions.*;

public class AdminPanelActivity extends AppCompatActivity implements
        View.OnClickListener{

    private static final String TAG = "UploadListActivity";

    ProgressDialog progressDialog;
    private int progressStatus = 0;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        // Buttons
        findViewById(R.id.button_upload_list).setOnClickListener(this);
        findViewById(R.id.button_upload_duty).setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }

    private void showProgressDialogWithTitle(String title, String substring, final int max) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(substring);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(max);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {
                while (progressDialog.getMax() > progressDialog.getProgress()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.dismiss();
            }
        }).start();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        if (requestCode == READ_REQUEST_CODE_UPLOAD_LIST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                try {
                    ContentResolver contetnt = getContentResolver();
                    showProgressDialogWithTitle("Upload list","uploading...",getNumberOfLines(uri,contetnt));
                    generateDocumentsFromTxtAndSend(uri,contetnt,progressDialog);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == READ_REQUEST_CODE_UPLOAD_DUTY && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                try {
                    ContentResolver contetnt = getContentResolver();
                    showProgressDialogWithTitle("Upload duty","uploading...",getNumberOfLines(uri,contetnt));
                    generateDutyListFromTxtAndSend(uri,contetnt,progressDialog);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch(int requestCode) {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("text/plain");

        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_upload_list) {
            performFileSearch(READ_REQUEST_CODE_UPLOAD_LIST);
        }else if (i == R.id.button_upload_duty) {
            performFileSearch(READ_REQUEST_CODE_UPLOAD_DUTY);
        }
    }
}
