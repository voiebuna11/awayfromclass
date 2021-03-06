package com.example.afc.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.afc.R;
import com.example.afc.activities.BaseTogglelessActivity;
import com.example.afc.app.Config;
import com.example.afc.classes.DateUtil;
import com.example.afc.classes.FilePath;
import com.example.afc.classes.PolymorphButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FilesActivity extends BaseTogglelessActivity {
    private int REQUEST_PERMISSION;
    private static final int PICK_FILE_REQUEST = 1;
    public static final int progress_bar_type = 0;
    private static final String TAG = FilesActivity.class.getSimpleName();

    private String selectedFilePath, mFilesLocation;
    private DateUtil mCurrentDate;

    ProgressDialog dialog;
    FloatingActionButton addFileBtn;
    
    RecyclerView mFilesRecyclerView;
    RecyclerView.LayoutManager mFilesLayoutManager;
    RecyclerView.Adapter mAdapter;

    RelativeLayout mFileListMenu;
    TextView mFileListEmpty;
    ArrayList<MyFile> mFileList;
    PolymorphButton mAdapterDisplayBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.file_list_title));
        mFileList = new ArrayList<MyFile>();
        mCurrentDate = new DateUtil(new Date());
        mFilesLocation = session.getAFCLink() + "/afc/users/" + sessionData.get(Config.KEY_USER) + "/";

        addFileBtn = (FloatingActionButton) findViewById(R.id.add_new_file);
        mFilesRecyclerView = (RecyclerView) findViewById(R.id.file_list);
        mFileListEmpty = (TextView) findViewById(R.id.file_list_empty);
        mFileListMenu = (RelativeLayout) findViewById(R.id.file_list_menu);

        mAdapterDisplayBtn = (PolymorphButton) findViewById(R.id.file_list_adapter);
        mAdapterDisplayBtn.setBackgrounds(R.drawable.file_list_adapter, R.drawable.file_list_adapter);

        addFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        //set recyler list and manager layout
        mFilesRecyclerView.setHasFixedSize(true);
        mFilesLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mFilesRecyclerView.setLayoutManager(mFilesLayoutManager);

        mAdapter = new RecyclerFileListAdapter(this, mFileList, mFilesLocation);
        mFilesRecyclerView.setAdapter(mAdapter);

        jsonParseMyFiles();
    }

    //get user files from server
    private void jsonParseMyFiles() {
        startLoadingBar();
        mFileList.clear();

        String url = session.getAFCLink() + "/afc/users/get_user_files.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject j = new JSONObject(response);
                    JSONArray jsonArray = j.getJSONArray("file_list");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject intermediar = jsonArray.getJSONObject(i);

                        mFileList.add(new MyFile(
                                intermediar.getString("file_id"),
                                intermediar.getString("file_author"),
                                intermediar.getString("file_name"),
                                intermediar.getString("file_type"),
                                intermediar.getString("file_date")
                        ));
                    }
                    mAdapter.notifyDataSetChanged();
                    if(mFileList.size() == 0){
                        mFileListEmpty.setVisibility(View.VISIBLE);
                        mFileListMenu.setVisibility(View.GONE);
                    }
                    else{
                        mFileListEmpty.setVisibility(View.GONE);
                        mFileListMenu.setVisibility(View.VISIBLE);
                    }

                    stopLoadingBar();
                } catch (JSONException e) {
                    e.printStackTrace();
                    alert(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", sessionData.get(Config.KEY_ID)); //parametrii POST
                return params;
            }
        };
        mQueue.add(request);
    }

    //start upload function
    public void startUpload(){
        //tvFileName.setText(selectedFilePath);
        dialog = ProgressDialog.show(this,"",getString(R.string.file_list_uploading),true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //creating new thread to handle Http Operations
                uploadFile(selectedFilePath);
            }
        }).start();
    }

    //display file manager
    private void showFileChooser() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent,"Choose File to Upload.."),PICK_FILE_REQUEST);
    }

    //android upload file to server
    public void uploadFile(final String selectedFilePath){

        int serverResponseCode = 0;
        String serverResponseMessage = "";

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length-1];

        if (!selectedFile.isFile()){
            dialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alert(getString(R.string.file_list_source_not_found)+ ": " + selectedFilePath);
                }
            });
            return;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(session.getAFCLink() + "/afc/users/add_new_file.php?user_name=" + sessionData.get(Config.KEY_USER));
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",selectedFilePath);
                connection.setRequestProperty("user_name", sessionData.get(Config.KEY_USER));

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                //get server response code
                serverResponseCode = connection.getResponseCode();

                //get server response string
                InputStream dataInputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        dataInputStream, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                serverResponseMessage = sb.toString();
                dataInputStream.close();

                //Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                final String data = serverResponseMessage;
                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (data){
                                case "success: file_uploaded":
                                    alert(getString(R.string.file_list_uploading_success));
                                    jsonParseMyFiles();
                                    break;
                                case "error: file_not_sent":
                                    alert(getString(R.string.file_list_not_sent));
                                default:
                                    alert(data);
                            }
                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alert(getString(R.string.file_list_not_found));
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                alert(getString(R.string.file_list_wrong_url));

            } catch (IOException e) {
                e.printStackTrace();
                alert(getString(R.string.file_list_wrong_format));
            }
            dialog.dismiss();
        }
    }

    public void showSorting(View view){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PICK_FILE_REQUEST){
                if(data == null){
                    //no data present
                    return;
                }


                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this,selectedFileUri);
                Log.i(TAG,"Selected File Path:" + selectedFilePath);

                if(selectedFilePath != null && !selectedFilePath.equals("")){
                    startUpload();
                }else{
                    alert(getString(R.string.file_list_wrong_format));
                }
            }
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_file_list;
    }

    @Override
    public int getToolbarResource() { return R.id.top_menu_layout; }
}
