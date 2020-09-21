package com.neotechindia.plugsmart.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;

public class Select_Radio extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton radioButton;
    private Toolbar toolbar;
    Button sumbit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__radio);
        initViews();

        radioGroup = findViewById(R.id.radiogroup);
        sumbit = findViewById(R.id.btn_apply);
        sumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int RadioID = radioGroup.getCheckedRadioButtonId();

                radioButton = findViewById(RadioID);

                if (radioButton.getText().toString().trim().equals("Edge")) {

                    Intent intent = new Intent(Select_Radio.this,Edge_discovery.class);
                    startActivityForResult(intent, 1);


                } else {

                }

            }
        });
    }

    public void checkButton(View v) {
        int RadioID = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(RadioID);

        Toast.makeText(this, radioButton.getText(), Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_Select_edge));
        setSupportActionBar(toolbar);
    }
}