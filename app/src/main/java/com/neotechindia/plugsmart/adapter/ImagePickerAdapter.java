package com.neotechindia.plugsmart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.listeners.IimagePicker;
import com.neotechindia.plugsmart.model.ImagePickBean;

import java.util.ArrayList;

public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.MyViewHolder> {
    Context _context;
    View view;
    ArrayList<ImagePickBean> al = new ArrayList();
    IimagePicker iimagePicker;


    public ImagePickerAdapter(Context _context, ArrayList<ImagePickBean> al, IimagePicker iimagePicker ) {
        this._context = _context;
        this.al = al;
        this.iimagePicker=iimagePicker;
    }

    @NonNull
    @Override
    public ImagePickerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(_context).inflate(R.layout.adapter_image_picker, null, false);
        ImagePickerAdapter.MyViewHolder viewHolder = new ImagePickerAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ImagePickerAdapter.MyViewHolder holder, final int position) {
        final ImagePickBean imagePickBean=al.get(position);
        holder.iv_image_picker.setImageResource(imagePickBean.getPicId());
        holder.iv_image_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.cb_img.isChecked()) {
                    holder.cb_img.setChecked(false);
                    iimagePicker.image(-1, -1);
                    holder.cb_img.setVisibility(View.GONE);
                }
                else
                {

                    iimagePicker.image(imagePickBean.getPicPosition(), imagePickBean.getPicId());
                    holder.cb_img.setChecked(true);    holder.cb_img.setVisibility(View.VISIBLE);

                }
            }
        });
        holder.frame_image_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.cb_img.isChecked()) {
                    holder.cb_img.setChecked(false);
                    iimagePicker.image(-1, -1);
                    holder.cb_img.setVisibility(View.GONE);
                }
                else
                {
                    iimagePicker.image(imagePickBean.getPicPosition(), imagePickBean.getPicId());
                    holder.cb_img.setChecked(true);    holder.cb_img.setVisibility(View.VISIBLE);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image_picker;
        CheckBox cb_img;
FrameLayout frame_image_picker;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_image_picker = itemView.findViewById(R.id.iv_image_picker);
            cb_img=itemView.findViewById(R.id.cb_img);
            frame_image_picker=itemView.findViewById(R.id.frame_image_picker);

        }
    }
}