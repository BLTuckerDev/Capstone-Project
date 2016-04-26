package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.injection.GregorianUTC;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;

public class StoryCommentsAdapter extends RecyclerView.Adapter<StoryCommentsAdapter.CommentViewHolder> {

    private final Resources resources;
    private final ReadingSession readingSession;
    private final Calendar calendar;

    @Inject
    public StoryCommentsAdapter(@GregorianUTC Calendar utcCalendar, Resources resources, ReadingSession readingSession){
        this.resources = resources;
        this.readingSession = readingSession;
        calendar = utcCalendar;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.story_comment_layout_item, parent, false);
        View commentContainer = itemView.findViewById(R.id.comment_container);
        itemView.setTag(R.id.comment_container, commentContainer);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = readingSession.getCurrentStoryComment(position);
        holder.authorNameTextView.setText(comment.getAuthorName());
        holder.postTimeTextView.setText(getFormattedCommentTime(comment, holder.itemView.getContext()));
        holder.commentBodyTextView.setText(Html.fromHtml(comment.getCommentText()));

        int commentDepth = getCommentDepth(comment, 0);
        if(commentDepth > 0){
            LinearLayout container = (LinearLayout) holder.itemView.getTag(R.id.comment_container);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) container.getLayoutParams();
            float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, commentDepth * 15, resources.getDisplayMetrics());
            layoutParams.setMarginStart((int) margin);
            container.setLayoutParams(layoutParams);
            container.invalidate();
        }
    }

    private String getFormattedCommentTime(Comment comment, Context context){
        long elapsedSeconds = (calendar.getTimeInMillis() / 1000) - comment.getUnixPostTime();
        long elapsedMinutes = elapsedSeconds / 60;
        long elapsedHours = elapsedMinutes / 60;
        long elapsedDays = elapsedHours / 24;

        if(elapsedDays > 0){
            return String.format(context.getString(R.string.days_ago), elapsedDays);
        }

        if(elapsedHours > 0){
            return String.format(context.getString(R.string.hours_ago), elapsedHours);
        }

        if(elapsedMinutes > 0){
            return String.format(context.getString(R.string.minutes_ago), elapsedMinutes);
        }

        return context.getString(R.string.less_than_a_minute);
    }

    private int getCommentDepth(Comment aComment, int depth){
        Comment parentComment = readingSession.getParentComment(aComment.getId());
        if(null == parentComment){
            return depth;
        }

        depth++;
        return getCommentDepth(parentComment, depth);
    }

    @Override
    public int getItemCount() {
        return readingSession.currentStoryCommentCount();
    }

    public void reset() {
        notifyDataSetChanged();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.comment_poster_name_textview)
        TextView authorNameTextView;

        @Bind(R.id.comment_post_time_textview)
        TextView postTimeTextView;

        @Bind(R.id.comment_body_textview)
        TextView commentBodyTextView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
