package dev.bltucker.nanodegreecapstone.readlater;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dev.bltucker.nanodegreecapstone.R;

public class ReadLaterListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_later_list);
    }

    public static void launch(Activity activity) {
        Intent launchIntent = new Intent(activity, ReadLaterListActivity.class);
        ActivityCompat.startActivity(activity, launchIntent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
    }
}
