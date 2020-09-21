package com.neotechindia.plugsmart.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.adapter.ImagePickerAdapter;
import com.neotechindia.plugsmart.listeners.IimagePicker;
import com.neotechindia.plugsmart.model.ImagePickBean;

import java.util.ArrayList;

public class ImagePick extends AppCompatActivity {
    RecyclerView rv_image_picker;
    int imgPosition = 0, iD;
    ArrayList<ImagePickBean> arrayList = new ArrayList<>();
    Button btn_select, btn_cancel;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pick);
        arrayList = getIntent().getParcelableArrayListExtra("picArraylist");
        initViews();
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
    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.imagePick));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        btn_select = findViewById(R.id.btn_select);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgPosition==-1)
                {
                    Toast.makeText(ImagePick.this,"Please Select Image",Toast.LENGTH_SHORT).show();
                }
                else {
                    IimagePicker iimagePicker = ConstantUtil.iimagePick;
                    iimagePicker.image(imgPosition, iD);
                    finish();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        RecyclerView.LayoutManager layoutManager;
        rv_image_picker = findViewById(R.id.rv_image_picker);
        layoutManager = new GridLayoutManager(this, 3);
        rv_image_picker.setLayoutManager(layoutManager);
        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_image_picker.getContext(),
                DividerItemDecoration.HORIZONTAL);*/
       // rv_image_picker.addItemDecoration(dividerItemDecoration);
        ImagePickerAdapter adapter = new ImagePickerAdapter(this, arrayList, new IimagePicker() {
            @Override
            public void image(int i, int id) {
                imgPosition = i;
                iD = id;
                if(imgPosition==-1)
                {
                    Toast.makeText(ImagePick.this,"Please Select Image",Toast.LENGTH_SHORT).show();
                }
                else {
                    IimagePicker iimagePicker = ConstantUtil.iimagePick;
                    iimagePicker.image(imgPosition, iD);
                    finish();
                }

            }


        });
        rv_image_picker.setAdapter(adapter);
    }
}
