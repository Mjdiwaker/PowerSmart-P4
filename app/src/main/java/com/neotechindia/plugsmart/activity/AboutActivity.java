package com.neotechindia.plugsmart.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.neotechindia.plugsmart.BuildConfig;
import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;

public class AboutActivity extends AppCompatActivity{

    private static final String TAG = AboutActivity.class.getSimpleName();
    View view;
    View parent;
    Toolbar toolbar;
    TextView tv_app_version, tv_email, tv_website,tv_app_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_about));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        String versionName = BuildConfig.VERSION_NAME;
        tv_app_version = (TextView) findViewById(R.id.app_version);
       // tv_app_id = (TextView) findViewById(R.id.app_id);
   //     tv_email = (TextView) findViewById(R.id.tv_email);
        tv_website = (TextView) findViewById(R.id.tv_website);
        tv_website.setText(Html.fromHtml("<a href=\"http://www.neotechindia.com/\">www.neotechindia.com</a>"));
        tv_website.setTextColor(getResources().getColor(R.color.blue));

            tv_app_version.setText("  PowerSmart(" + versionName + ")");
        SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
        String uuid = firmwareSharedPref.getString(ConstantUtil.uuid1, "");
            //tv_app_id.setText(uuid);


        tv_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompanyWebsite(getResources().getString(R.string.website_url));

            }
        });

//        tv_email.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendMail(getResources().getString(R.string.email_id), getResources().getString(R.string.subject), getResources().getString(R.string.email_msg_body));
//            }
//        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void openCompanyWebsite(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void sendMail(String to, String subject, String email_body) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, email_body);

        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Select Email Sending App :"));
    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }


}
