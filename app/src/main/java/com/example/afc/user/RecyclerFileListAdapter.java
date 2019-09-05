package com.example.afc.user;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.afc.R;
import com.example.afc.app.Config;
import com.example.afc.app.SessionManagement;
import com.example.afc.classes.DateUtil;
import com.example.afc.classes.FileDownloader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//filelist adapter class
public class RecyclerFileListAdapter extends RecyclerView.Adapter<RecyclerFileListAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<MyFile> mList;
    private DateUtil mCurrentDate;
    private SessionManagement session;
    private HashMap<String, String> sessionData;
    private String mFilesLocation;
    private RequestQueue mQueue;

    public RecyclerFileListAdapter(Context ctx, ArrayList<MyFile> mList, String mFilesLocation){
        this.ctx = ctx;
        this.mList = mList;
        this.mCurrentDate = new DateUtil(new Date());
        this.session = new SessionManagement(ctx);
        this.sessionData = session.getUserDetails();
        this.mFilesLocation = mFilesLocation;
        this.mQueue = Volley.newRequestQueue(ctx);
    }

    @Override
    public RecyclerFileListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_layout, parent, false);
        RecyclerFileListAdapter.ViewHolder vh = new RecyclerFileListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerFileListAdapter.ViewHolder holder, final int i) {
        String fileType = mList.get(i).getType();
        final String fileName = mList.get(i).getName();
        final String fileUrl = mFilesLocation+fileName;
        DateUtil fileDate = new DateUtil(mList.get(i).getDate());

        //set file name
        holder.mText.setText(fileName);

        //set last edit time
        if(fileDate.getDay() == mCurrentDate.getDay() && fileDate.getMonth() == mCurrentDate.getMonth() &&
                fileDate.getYear() == mCurrentDate.getYear()){
            holder.mDate.setText(ctx.getResources().getString(R.string.general_term_today) + " la " + fileDate.getDate("HH:mm"));
        } else if(fileDate.getDay() == mCurrentDate.getDay()-1 && fileDate.getMonth() == mCurrentDate.getMonth() &&
                fileDate.getYear() == mCurrentDate.getYear()){
            holder.mDate.setText(ctx.getResources().getString(R.string.general_term_yesterday) + " la " + fileDate.getDate("HH:mm"));
        } else if(fileDate.getYear() < mCurrentDate.getYear()){
            holder.mDate.setText(fileDate.getDate("dd MMM yyyy"));
        }else {
            holder.mDate.setText(fileDate.getDate("dd MMM"));
        }

        //set icon
        switch (fileType){
            //images
            case "bmp":
            case "gif":
            case "jpeg":
            case "jpg":
            case "png":
            case "webp":
            case "heif":
            case "heic":
            //videos
            case "mp4":
            case "3gp":
            case "webm":
                Glide.with(ctx)
                        .load(fileUrl)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(holder.mIcon);
                break;
            //powerpoint
            case "pptx":
            case "pptm":
            case "ppt":
                holder.mIcon.setBackground(ctx.getResources().getDrawable(R.drawable.file_list_ppt));
                break;
            //word
            case "docx":
            case "doc":
            case "dotx":
                holder.mIcon.setBackground(ctx.getResources().getDrawable(R.drawable.file_list_word));
                break;
            //pdf
            case "pdf":
                holder.mIcon.setBackground(ctx.getResources().getDrawable(R.drawable.file_list_pdf));
                break;
            //excel
            case "xls":
            case "xlsx":
            case "xlt":
            case "xlts":
            case "xlsb":
            case "xlsm":
                holder.mIcon.setBackground(ctx.getResources().getDrawable(R.drawable.file_list_excel));
                break;
            //other
            default:
                holder.mIcon.setBackground(ctx.getResources().getDrawable(R.drawable.file_list_unknown));
                break;
        }

        //set options menu
        holder.mOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup;
                popup = new PopupMenu(ctx, holder.mOptions, Gravity.LEFT);
                //inflating menu from xml resource
                popup.getMenuInflater().inflate(R.menu.options_menu_file_list, popup.getMenu());

                //visibility of options
                if(mFilesLocation.contains("course") && !sessionData.get(Config.KEY_ID).equals(mList.get(i).getAuthor())) {
                    MenuItem renameBtn = popup.getMenu().findItem(R.id.fl_rename);
                    MenuItem removeBtn= popup.getMenu().findItem(R.id.fl_remove);
                    renameBtn.setVisible(false);
                    removeBtn.setVisible(false);
                }

                //adding click listener
                //displaying the popup
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.fl_preview:
                                //handle menu1 click
                                alert("preview " + fileUrl);
                                break;
                            case R.id.fl_download:
                                //download the file from server
                                new FileDownloader(ctx, fileName).execute(fileUrl);
                                break;
                            case R.id.fl_rename:
                                //handle menu3 click
                                break;
                            case R.id.fl_remove:
                                if(mList.get(i).getContent_id()==null)
                                    deleteFileFromServer(fileName, i, "");
                                else
                                    deleteFileFromServer(fileName, i, mList.get(i).getContent_id());
                                break;
                            case R.id.fl_copy_link:
                                copyToClipboard(fileName, "afc_file");
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mText;
        private ImageView mIcon;
        private ImageButton mOptions;
        private TextView mDate;

        private ViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.file_list_name);
            mIcon = (ImageView) itemView.findViewById(R.id.file_list_icon);
            mOptions = (ImageButton) itemView.findViewById(R.id.file_list_options);
            mDate = (TextView) itemView.findViewById(R.id.file_list_date);

            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int i = getLayoutPosition();
            alert(mFilesLocation);
        }
    }

    public void alert(String text){
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }

    private void copyToClipboard(String text, String label){
        ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        alert(ctx.getResources().getString(R.string.file_list_copy_to_clipboard));
    }

    //get user files from server
    private void deleteFileFromServer(final String fileName, final int position, final String contentId) {
        String url = "";
        if(mFilesLocation.contains("users")){
            url = session.getAFCLink() + "/afc/users/remove_user_file.php";
        } else {
            url = session.getAFCLink() + "/afc/courses/remove_course_file.php";
        }
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success: file_removed")){
                    alert(ctx.getResources().getString(R.string.file_list_removed));
                } else {
                    alert(response);
                }

                mList.remove(position);
                notifyDataSetChanged();
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
                params.put("user_id", mList.get(position).getAuthor()); //parametrii POST
                params.put("file_name", fileName);
                params.put("content_id", contentId);
                return params;
            }
        };
        mQueue.add(request);
    }
}
