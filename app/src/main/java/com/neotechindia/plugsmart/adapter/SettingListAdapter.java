package com.neotechindia.plugsmart.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.Logger;
import com.neotechindia.plugsmart.activity.ChangePasswordActivity;
import com.neotechindia.plugsmart.activity.ForgotPasswordActivity;
import com.neotechindia.plugsmart.activity.TimeSyncActivity;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.model.SettingListBean;

import java.util.ArrayList;

public class SettingListAdapter extends RecyclerView.Adapter<SettingListAdapter.MyViewHolder> {
    Context _context;
    View view;
    ArrayList<SettingListBean> al = new ArrayList();
    DeviceBean deviceBean;
    private OnListItemClickCallBack mOnListItemClickCallBack;
    public SettingListAdapter(Context _context, ArrayList<SettingListBean> al, DeviceBean deviceBean,OnListItemClickCallBack mOnListItemClickCallBack) {
        this._context = _context;
        this.al = al;
        this.deviceBean=deviceBean;
        this.mOnListItemClickCallBack=mOnListItemClickCallBack;
    }

    @NonNull
    @Override
    public SettingListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(_context).inflate(R.layout.adapter_settings_list, null, false);
        SettingListAdapter.MyViewHolder viewHolder = new SettingListAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SettingListAdapter.MyViewHolder holder, final int position) {
        SettingListBean settingListBean=al.get(position);
        holder.iv_setting_logo_settings.setImageResource(settingListBean.getId());
        holder.tv_setting_name_setting_adapter.setText(settingListBean.getName());
        holder.tv_setting_name_setting_adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(position,holder);

            }
        });
        holder.iv_setting_logo_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(position,holder);

            }
        });


    }

    private void openActivity(int position,SettingListAdapter.MyViewHolder holder) {
        if(Logger.isPlugSmart)
        {
            switch (position) {
                case 0: {

                    Intent i = new Intent(_context, ForgotPasswordActivity.class);
                    i.putExtra(ConstantUtil.stationList,deviceBean);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);
                    break;
                }
                case 1: {
                    Intent i = new Intent(_context, ChangePasswordActivity.class);
                    i.putExtra(ConstantUtil.stationList,deviceBean);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);
                    break;
                }

                case 2: {
                    mOnListItemClickCallBack.onItemClick(view, holder.getAdapterPosition());
               /*     Intent i = new Intent(_context, NetworkSettingsActivity.class);
                    i.putExtra(ConstantUtil.stationList,deviceBean);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);*/
                    break;
                }
                case 3: {
                    mOnListItemClickCallBack.onItemClick(view, holder.getAdapterPosition());
                /*    Intent i = new Intent(_context, ViewRegistrationActivity.class);
                    i.putExtra(ConstantUtil.stationList,deviceBean);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);*/
                    break;
                }
            }
        }
        else
        {
            switch (position) {
                case 0: {
                    Intent i = new Intent(_context, ForgotPasswordActivity.class);
                    i.putExtra(ConstantUtil.stationList,deviceBean);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);
                    break;
                }
                case 1: {
                    Intent i = new Intent(_context, ChangePasswordActivity.class);
                    i.putExtra(ConstantUtil.stationList,deviceBean);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);
                    break;
                }
                case 2: {
                    Intent i = new Intent(_context, TimeSyncActivity.class);
                    i.putExtra(ConstantUtil.stationList,deviceBean);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);
                    break;
                }
                case 3: {
                    mOnListItemClickCallBack.onItemClick(view, holder.getAdapterPosition());
              /*      Intent i = new Intent(_context, NetworkSettingsActivity.class);
                    i.putExtra(ConstantUtil.stationList,deviceBean);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);*/
                    break;
                }
                case 4: {
                    mOnListItemClickCallBack.onItemClick(view, holder.getAdapterPosition());
            /*        Intent i = new Intent(_context, ViewRegistrationActivity.class);
                    i.putExtra(ConstantUtil.stationList,deviceBean);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);*/
                    break;
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_setting_name_setting_adapter;
        ImageView iv_setting_logo_settings;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_setting_logo_settings = itemView.findViewById(R.id.iv_setting_logo_settings);
            tv_setting_name_setting_adapter = itemView.findViewById(R.id.tv_setting_name_setting_adapter);
        }
    }
    public interface OnListItemClickCallBack {
        public void onItemClick(View view, int pos);
    }
}
