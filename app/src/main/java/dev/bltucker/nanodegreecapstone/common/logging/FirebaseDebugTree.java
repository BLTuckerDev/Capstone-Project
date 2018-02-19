package dev.bltucker.nanodegreecapstone.common.logging;

import com.google.firebase.crash.FirebaseCrash;

import android.util.Log;

import timber.log.Timber;

public class FirebaseDebugTree extends Timber.DebugTree {

    private static final int MAX_LOG_LENGTH = 4000;

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (message.length() < MAX_LOG_LENGTH) {
            if (priority == Log.ASSERT) {
                FirebaseCrash.logcat(Log.ASSERT, tag, message);
            } else {
                FirebaseCrash.logcat(priority, tag, message);
            }
            return;
        }

        // Split by line, then ensure each line can fit into Log's maximum length.
        for (int i = 0, length = message.length(); i < length; i++) {
            int newline = message.indexOf('\n', i);
            newline = newline != -1 ? newline : length;
            do {
                int end = Math.min(newline, i + MAX_LOG_LENGTH);
                String part = message.substring(i, end);
                if (priority == Log.ASSERT) {
                    FirebaseCrash.logcat(Log.ASSERT, tag, part);
                } else {
                    FirebaseCrash.logcat(priority, tag, part);
                }
                i = end;
            } while (i < newline);
        }
    }

}
