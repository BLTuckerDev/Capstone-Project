package dev.bltucker.nanodegreecapstone.readlater;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dev.bltucker.nanodegreecapstone.R;

public class ReadLaterListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_later_list);
    }

    public static void launch(Context context) {
        Intent launchIntent = new Intent(context, ReadLaterListActivity.class);
        context.startActivity(launchIntent);
    }
}
