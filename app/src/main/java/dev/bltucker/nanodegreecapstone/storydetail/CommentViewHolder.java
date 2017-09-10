package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.widget.LinearLayout;

import java.util.Calendar;

import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.databinding.StoryCommentLayoutItemBinding;
import dev.bltucker.nanodegreecapstone.models.Comment;

class CommentViewHolder extends RecyclerView.ViewHolder{

    private static final int MARGIN_PER_DEPTH = 15;


    private final StoryCommentLayoutItemBinding binding;
    private final Calendar calendar;
    private final Resources resources;

    CommentViewHolder(StoryCommentLayoutItemBinding binding, Calendar calendar, Resources resources) {
        super(binding.getRoot());
        this.binding = binding;
        this.calendar = calendar;
        this.resources = resources;
    }

    public void bind(Comment comment) {
        binding.commentPosterNameTextview.setText(comment.getAuthorName());
        binding.commentPostTimeTextview.setText(getFormattedCommentTime(comment, binding.getRoot().getContext()));
        binding.commentBodyTextview.setText(Html.fromHtml(comment.getCommentText()));

        int commentDepth = comment.getDepth();
        LinearLayout container = (LinearLayout) binding.getRoot().getTag(R.id.comment_container);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) container.getLayoutParams();
        float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, commentDepth * MARGIN_PER_DEPTH, resources.getDisplayMetrics());
        layoutParams.setMarginStart((int) margin);
        container.setLayoutParams(layoutParams);
        container.invalidate();

    }

    @SuppressWarnings("squid:S109")
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
}
