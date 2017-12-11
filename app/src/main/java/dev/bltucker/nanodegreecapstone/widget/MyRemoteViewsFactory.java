package dev.bltucker.nanodegreecapstone.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Locale;

import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.data.StoryProvider;
import timber.log.Timber;

class MyRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    public static final int MAX_WIDGET_STORY_COUNT = 5;
    private Context context;
    private Cursor dataCursor = null;

    public MyRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @SuppressWarnings("squid:S1186")
    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        if (dataCursor != null) {
            dataCursor.close();
        }

        //clear identity since we do not export our content provider
        //this is called from within the app launcher process
        final long identityToken = Binder.clearCallingIdentity();
        dataCursor = context.getContentResolver().query(StoryProvider.STORIES_URI,
                null,
                null,
                null,
                null);
        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {
        if (dataCursor != null) {
            dataCursor.close();
            dataCursor = null;
        }
    }

    @Override
    public int getCount() {
        if (null == dataCursor) {
            return 0;
        }

        return Math.min(MAX_WIDGET_STORY_COUNT, dataCursor.getCount());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        boolean invalidPosition = position == AdapterView.INVALID_POSITION;
        if (invalidPosition || null == dataCursor || !dataCursor.moveToPosition(position)) {
            return null;
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.story_list_widget_item);

        String storyTitle = dataCursor.getString(dataCursor.getColumnIndex("title"));
        String formattedTitle = String.format(Locale.US, "%d. %s", position + 1, storyTitle);
        remoteViews.setTextViewText(R.id.story_title_textview, formattedTitle);
        //TODO we can have a click intent that will send us straight to the detail activity here+

        Intent intent = new Intent();
        remoteViews.setOnClickFillInIntent(R.id.story_widget_list_item_container, intent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(context.getPackageName(), R.layout.story_list_widget_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (dataCursor != null && dataCursor.moveToPosition(position)) {
            try {
                return Long.valueOf(dataCursor.getString(dataCursor.getColumnIndex("_id")));
            } catch (NumberFormatException ex) {
                Timber.e(ex, "");
                return position;
            }
        }

        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
