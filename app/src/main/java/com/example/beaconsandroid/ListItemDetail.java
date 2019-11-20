package com.example.beaconsandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ListItemDetail extends Activity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Displays html of clicked item.
     * @param savedInstanceState If the activity is being re-initialized after
     *      previously being shut down then this Bundle contains the data it most
     *      recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_detail);

        Intent intent = getIntent();

        int position = intent.getIntExtra("position", 0);
        String html = intent.getStringExtra("itemcontent");
        String title = intent.getStringExtra("itemtitle");

        TextView titleView = findViewById(R.id.tittle_text);

        TextView contentView = (TextView) findViewById(R.id.content_text);
        titleView.setText(title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentView.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
        } else {
            contentView.setText(Html.fromHtml(html));
        }
        BeaconApplication application = ((BeaconApplication) this.getApplicationContext());
        application.setListItemDetail(this);
    }
}
