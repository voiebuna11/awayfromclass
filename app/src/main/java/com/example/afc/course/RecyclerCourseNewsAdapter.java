package com.example.afc.course;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.afc.R;
import com.example.afc.app.SessionManagement;
import com.example.afc.classes.DateUtil;
import com.example.afc.content.Content;
import com.example.afc.content.ContentViewActivity;
import com.example.afc.user.News;
import com.example.afc.user.ProfileActivity;
import com.example.afc.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerCourseNewsAdapter extends RecyclerView.Adapter<RecyclerCourseNewsAdapter.ViewHolder> {
    private Context ctx;
    private DateUtil mCurrentDate;
    private ArrayList<News> mList;
    private SessionManagement session;
    private String mCourseFolder;

    public RecyclerCourseNewsAdapter(Context ctx, ArrayList<News> mList, String mCourseFolder){
        this.ctx = ctx;
        this.mList = mList;
        this.mCurrentDate = new DateUtil(new Date());
        this.session = new SessionManagement(ctx);
        this.mCourseFolder = mCourseFolder;
    }

    @Override
    public RecyclerCourseNewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_news_layout, parent, false);
        RecyclerCourseNewsAdapter.ViewHolder vh = new RecyclerCourseNewsAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerCourseNewsAdapter.ViewHolder holder, final int i) {
        User mUser = mList.get(i).getAuthor();
        Resources resources = ctx.getResources();

        String target_id = mList.get(i).getTarget_id();
        String target_type = mList.get(i).getTarget_type();
        String newsType = mList.get(i).getContext();
        String newsAction = mList.get(i).getAction();
        DateUtil date = new DateUtil(mList.get(i).getDate());
        String url = session.getAFCLink() + "/afc/assets/profile_pics/" + mUser.getPic();
        
        String err = "ERROR: target_type PATTERN IS UNKNOWN";
    

        //create spannable username
        Spannable name = new SpannableString(mUser.getLname()+ " " + mUser.getFname());

        //bold style
        StyleSpan boldStyle = new StyleSpan(android.graphics.Typeface.BOLD);
        StyleSpan italicStyle = new StyleSpan(Typeface.ITALIC);

        //content of the news
        CharSequence newsContent = "";

        //set bold + black color
        name.setSpan(new ForegroundColorSpan(Color.BLACK), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        name.setSpan(boldStyle, 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if(target_type.equals("course")){
            switch (newsType){
                case "course_managing":
                    switch (newsAction){
                        case "created":
                            newsContent = TextUtils.concat(name, " ", resources.getString(R.string.course_event_course_creation));
                            break;
                        case "edited":
                            newsContent = TextUtils.concat(name, " ", resources.getString(R.string.course_event_course_edit));
                            break;
                        case "removed":
                            newsContent = TextUtils.concat(name, " ", resources.getString(R.string.course_event_course_removed));
                            break;
                        default: newsContent = err; break;
                    }
                    break;
            }
        } else if(target_type.equals("content")){
            switch (newsType){
                case "course_managing":
                    Content content = mList.get(i).getContent();
                    Spannable contentName = new SpannableString(content.getName());
                    StyleSpan contentBold = new StyleSpan(android.graphics.Typeface.BOLD);

                    contentName.setSpan(contentBold, 0, contentName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    switch (newsAction){
                        case "created":
                            newsContent = TextUtils.concat(name, " ", resources.getString(R.string.course_event_content_creation), " ", contentName,".");
                            break;
                        case "edited":
                            newsContent = TextUtils.concat(name, " ", resources.getString(R.string.course_event_content_edit), " ", contentName, ".");
                            break;
                        case "removed":
                            newsContent = TextUtils.concat(name, " ", resources.getString(R.string.course_event_content_removed));
                            break;
                        default: newsContent = err; break;
                    }
                    break;
                default: newsContent = err; break;
            }
        } else if(target_type.equals("user")){
            switch(newsType){
                case "course_enrollment":
                    User user = mList.get(i).getTargetUser();

                    Spannable userName = new SpannableString(user.getLname() + " " + user.getFname());
                    StyleSpan contentBold = new StyleSpan(android.graphics.Typeface.BOLD);

                    userName.setSpan(contentBold, 0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    switch (newsAction){
                        case "pending":
                            newsContent = TextUtils.concat(name, " ", resources.getString(R.string.course_event_enroll_pending));
                            break;
                        case "accepted":
                            newsContent = TextUtils.concat(name, " ", resources.getString(R.string.course_event_enroll_accepted), " ", userName);
                            break;
                        case "rejected":
                            newsContent = TextUtils.concat(name, " ", resources.getString(R.string.course_event_enroll_rejected), " ", userName);
                            break;
                        default: newsContent = err; break;
                    }
                    break;
                default: newsContent = err; break;
            }
        } else if(target_type.equals("file")){
            switch (newsType){
                case "course_file":
                    Content content = mList.get(i).getContent();
                    Spannable contentName = new SpannableString(content.getName());
                    StyleSpan contentBold = new StyleSpan(android.graphics.Typeface.BOLD);

                    contentName.setSpan(contentBold, 0, contentName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    switch (newsAction){
                        case "added":
                            newsContent = TextUtils.concat(name, " ", resources.getString(R.string.course_event_file_added), " ", contentName, ".");
                            break;
                        case "removed":
                            //create spannable
                            Spannable fileName = new SpannableString(mList.get(i).getTarget_id());
                            //set bold + black color
                            fileName.setSpan(new ForegroundColorSpan(Color.BLACK), 0, fileName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            fileName.setSpan(italicStyle, 0, fileName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            newsContent = TextUtils.concat(name, " ", resources.getString(R.string.course_event_file_removed), " ", fileName, " ", resources.getString(R.string.general_term_from), " ", contentName, ".");
                            break;
                        default: newsContent = err; break;
                    }
                    break;
            }
        }
        else {
            newsContent = err;
        }

        //set content text
        holder.mContent.setText(newsContent);

        //set profile image
        Glide.with(ctx)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.mPic);

        //set date
        if(date.getDay() == mCurrentDate.getDay() && date.getMonth() == mCurrentDate.getMonth() &&
                date.getYear() == mCurrentDate.getYear()){
            holder.mDate.setText(ctx.getResources().getString(R.string.general_term_today) + " la " + date.getDate("HH:mm"));
        } else if(date.getDay() == mCurrentDate.getDay()-1 && date.getMonth() == mCurrentDate.getMonth() &&
                date.getYear() == mCurrentDate.getYear()){
            holder.mDate.setText(ctx.getResources().getString(R.string.general_term_yesterday) + " la " + date.getDate("HH:mm"));
        } else if(date.getYear() < mCurrentDate.getYear()){
            holder.mDate.setText(date.getDate("dd MMM yyyy"));
        }else {
            holder.mDate.setText(date.getDate("dd MMM"));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mContent;
        private TextView mDate;
        private ImageView mPic;

        private ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContent = (TextView) itemView.findViewById(R.id.news_content);
            mDate = (TextView) itemView.findViewById(R.id.news_date);
            mPic = (ImageView) itemView.findViewById(R.id.news_user_avatar);
        }

        @Override
        public void onClick(View view) {
            int i = getLayoutPosition();
            String newsType = mList.get(i).getTarget_type();
            Intent intent = new Intent();
            switch (newsType){
                case "content":
                case "file":
                    Content content = mList.get(i).getContent();
                    intent = new Intent(ctx, ContentViewActivity.class);
                    intent.putExtra("EXTRA_CONTENT_ID", content.getId() + "");
                    intent.putExtra("EXTRA_CONTENT_NAME", content.getName());
                    intent.putExtra("EXTRA_CONTENT_FOLDER", mCourseFolder);
                    intent.putExtra("EXTRA_CONTENT_DESCRIPTION", content.getDescription());
                    ctx.startActivity(intent);
                    break;
                case "user":
                    intent = new Intent(ctx, ProfileActivity.class);
                    intent.putExtra("EXTRA_USER", (Serializable) mList.get(i).getAuthor());
                    ctx.startActivity(intent);
                    break;
                case "course":
                    alert("course: " + mList.get(i).getTarget_id());
                    break;
                default: alert(mList.get(i).getId()+"");
            }

        }
    }
    public void alert(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }
}


