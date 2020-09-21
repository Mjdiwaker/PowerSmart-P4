package com.neotechindia.plugsmart.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.neotechindia.plugsmart.R;

public class Contact_Us extends AppCompatActivity {
    ImageView call, mail;
    private Toolbar toolbar;
    TextView tv_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact__us);
        Initviews();
        tv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall("8800144844");
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makePhoneCall("8800144844");
            }
        });
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail(getResources().getString(R.string.email_id), getResources().getString(R.string.subject), getResources().getString(R.string.email_msg_body));
            }
        });
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

    public void sendMail(String to, String subject, String email_body) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, email_body);

        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Select Email Sending App :"));
    }

    private void makePhoneCall(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    private void Initviews() {
        call = (ImageView) findViewById(R.id.callimage);
        mail = (ImageView) findViewById(R.id.emailimage);
        tv_call=findViewById(R.id.tv_call);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_customer_support));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }
}
