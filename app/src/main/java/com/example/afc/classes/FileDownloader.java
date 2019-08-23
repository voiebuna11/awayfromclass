package com.example.afc.classes;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.example.afc.R;

import java.io.File;

public class FileDownloader extends AsyncTask<String, Void, String> {
    private static final String TAG = "FileDownloader";
    private static final String KEY_DIR_ERROR = "FailedToCreateDirectory";
    private Context ctx;
    private String fileName;
    private boolean flag;

    public FileDownloader(Context ctx, String fileName) {
        this.ctx = ctx;
        this.fileName = fileName;
    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected String doInBackground(String... f_url) {
        flag = false;
        boolean isDownloading = true;
        DownloadManager downloadmanager = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(f_url[0]);
        String path = Environment.getExternalStorageDirectory() + File.separator + "AFC";

        //prepare folder
        File folder = new File(path);

        File mFile = new File(path,fileName);

        //prepare file
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(fileName);
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationUri(Uri.fromFile(mFile));

        if (!folder.exists()) {
            //generate folder
            if (folder.mkdirs()) {
                downloadmanager.enqueue(request);
            } else {
                return KEY_DIR_ERROR;
            }
        } else {
            downloadmanager.enqueue(request);
        }
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor c = null;
        query.setFilterByStatus(DownloadManager.STATUS_FAILED|DownloadManager.STATUS_PAUSED|DownloadManager.STATUS_SUCCESSFUL|DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_PENDING);

        while (isDownloading) {
            c = downloadmanager.query(query);
            if(c.moveToFirst()) {
                //Log.i (TAG,"Downloading file from server..");
                int status =c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

                if (status==DownloadManager.STATUS_SUCCESSFUL) {
                    //Log.i (TAG,"File successfully downloaded");
                    isDownloading = false;
                    flag = true;
                    break;
                }
                if (status==DownloadManager.STATUS_FAILED) {
                    //Log.i (TAG,"File downloading failed");
                    flag = false;
                    isDownloading = false;
                    break;
                }
            }
        }
        return null;
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String result) {
        // display message after the file was downloaded
        if(result != null && result.equals(KEY_DIR_ERROR)){
            Toast.makeText(ctx, ctx.getResources().getString(R.string.file_list_directory_fail), Toast.LENGTH_LONG).show();
        } else {
            if(flag){
                Toast.makeText(ctx, ctx.getResources().getString(R.string.file_list_download_success), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ctx, ctx.getResources().getString(R.string.file_list_download_fail), Toast.LENGTH_LONG).show();
            }
        }

    }
}

