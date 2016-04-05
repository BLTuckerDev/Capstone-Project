package dev.bltucker.nanodegreecapstone;

import android.accounts.Account;
import android.content.ContentResolver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.sync.StorySyncAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(new Account(StorySyncAdapter.ACCOUNT, StorySyncAdapter.ACCOUNT_TYPE),
              SchematicContentProviderGenerator.AUTHORITY, bundle);


        getSupportFragmentManager().beginTransaction().add(StoryListFragment.newInstance(), "Fragment").commit();
    }
}
