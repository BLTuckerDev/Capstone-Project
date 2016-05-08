package dev.bltucker.nanodegreecapstone.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class TopFiveStoriesWidgetRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyRemoteViewsFactory(this);
    }
}
