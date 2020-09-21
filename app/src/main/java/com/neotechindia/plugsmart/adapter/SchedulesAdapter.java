package com.neotechindia.plugsmart.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.Logger;
import com.neotechindia.plugsmart.activity.SchedulesViewActivity;
import com.neotechindia.plugsmart.activity.TimerActivity;
import com.neotechindia.plugsmart.listeners.IAuthentcUser;
import com.neotechindia.plugsmart.model.DeviceBean;

import java.util.ArrayList;

public class SchedulesAdapter extends RecyclerView.Adapter<SchedulesAdapter.MyViewHolder> {
    Context _context;
    View view;
    ArrayList<DeviceBean> al = new ArrayList();
    private static IAuthentcUser mAuthentcUser;

    public SchedulesAdapter(Context _context, ArrayList<DeviceBean> al) {
        this._context = _context;
        this.al = al;
    }

    @NonNull
    @Override
    public SchedulesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(_context).inflate(R.layout.adapter_schedules_list, null, false);
        SchedulesAdapter.MyViewHolder viewHolder = new SchedulesAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SchedulesAdapter.MyViewHolder holder, int position) {

        if (ConstantUtil.isForAPModeOnly) {

            SharedPreferences firmwareSharedPref = _context.getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            holder.tv_device_name_schedules.setText(firmwareSharedPref.getString(ConstantUtil.gadgetName, ""));

            if (Logger.isPlugSmart) {
                holder.iv_timer_icon.setVisibility(View.VISIBLE);
                holder.viewSchedules.setVisibility(View.VISIBLE);
                switch (firmwareSharedPref.getString(ConstantUtil.gadgetPos, "")) {
                    case "0": {
                        holder.iv_device_logo_schedules.setImageResource(R.drawable.ac_black);
                        break;
                    }
                    case "1": {
                        holder.iv_device_logo_schedules.setImageResource(R.drawable.tv_black);
                        break;
                    }
                    case "2": {
                        holder.iv_device_logo_schedules.setImageResource(R.drawable.fan_black);
                        break;
                    }
                    case "3": {
                        holder.iv_device_logo_schedules.setImageResource(R.drawable.fridge_black);
                        break;
                    }
                    case "4": {
                        holder.iv_device_logo_schedules.setImageResource(R.drawable.geyser_black);
                        break;
                    }
                    case "5": {
                        holder.iv_device_logo_schedules.setImageResource(R.drawable.washing_machine_black);
                        break;
                    }
                    case "6": {
                        holder.iv_device_logo_schedules.setImageResource(R.drawable.lugo);
                        break;
                    }
                    default: {
                        holder.iv_device_logo_schedules.setImageResource(R.drawable.lugo);
                        break;
                    }
                }
            } else {
                holder.iv_timer_icon.setVisibility(View.VISIBLE);
                holder.viewSchedules.setVisibility(View.VISIBLE);
            }
            holder.iv_schedule_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Logger.isPlugSmart) {
                        Intent i = new Intent(_context, SchedulesViewActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        _context.startActivity(i);
                    } else {
                        Toast.makeText(_context, "Schedules works only when your device is connected to router, not when directly to powersmart device", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.iv_timer_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(_context, TimerActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);
                }
            });
        } else {
            if (al != null) {
                if (al.size() != 0) {
                    final DeviceBean deviceBean = al.get(position);
                    holder.tv_device_name_schedules.setText(deviceBean.name);
                    holder.rl_adapter_schedules.setBackgroundColor(Color.parseColor(deviceBean.status));
                    if (Logger.isPlugSmart) {

                        holder.iv_timer_icon.setVisibility(View.VISIBLE);
                        holder.viewSchedules.setVisibility(View.VISIBLE);
                        switch (deviceBean.iconType) {
                            case "0": {
                                holder.iv_device_logo_schedules.setImageResource(R.drawable.ac_black);
                                break;
                            }
                            case "1": {
                                holder.iv_device_logo_schedules.setImageResource(R.drawable.tv_black);
                                break;
                            }
                            case "2": {
                                holder.iv_device_logo_schedules.setImageResource(R.drawable.fan_black);
                                break;
                            }
                            case "3": {
                                holder.iv_device_logo_schedules.setImageResource(R.drawable.fridge_black);
                                break;
                            }
                            case "4": {
                                holder.iv_device_logo_schedules.setImageResource(R.drawable.geyser_black);
                                break;
                            }
                            case "5": {
                                holder.iv_device_logo_schedules.setImageResource(R.drawable.washing_machine_black);
                                break;
                            }
                            case "6": {
                                holder.iv_device_logo_schedules.setImageResource(R.drawable.lugo);
                                break;
                            }
                            default: {
                                holder.iv_device_logo_schedules.setImageResource(R.drawable.lugo);
                                break;
                            }
                        }
                    } else {
                        holder.iv_timer_icon.setVisibility(View.VISIBLE);
                        holder.viewSchedules.setVisibility(View.VISIBLE);
                    }
                    holder.iv_schedule_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if ((deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDeviceNotReg))) ||
                                    (deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDevice_On_edge))) ||
                                    (deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDevice_On_edge_notConnected)))) {
                                mAuthentcUser = ConstantUtil.authentcUser;
                                mAuthentcUser.findAuthenticity(deviceBean);
                                return;
                            }
                            Intent i = new Intent(_context, SchedulesViewActivity.class);
                            i.putExtra(ConstantUtil.stationList, deviceBean);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            _context.startActivity(i);
                        }
                    });
                    holder.iv_timer_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if ((deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDeviceNotReg))) ||
                                    (deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDevice_On_edge))) ||
                                    (deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDevice_On_edge_notConnected)))) {
                                mAuthentcUser = ConstantUtil.authentcUser;
                                mAuthentcUser.findAuthenticity(deviceBean);
                                return;
                            }
                            Intent i = new Intent(_context, TimerActivity.class);
                            i.putExtra(ConstantUtil.stationList, deviceBean);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            _context.startActivity(i);
                        }
                    });
                }
            }
        }

        // holder.iv_device_logo_schedules.setImageResource();
    }

    @Override
    public int getItemCount() {
        if (al != null) {
            if (al.size() != 0) {
                return al.size();
            }
        } else {
            return 1;
        }
        return 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_device_logo_schedules, iv_timer_icon, iv_schedule_icon;
        TextView tv_device_name_schedules;
        RelativeLayout rl_adapter_schedules;
        View viewSchedules;

        public MyViewHolder(View itemView) {
            super(itemView);
            viewSchedules = itemView.findViewById(R.id.viewSchedules);
            rl_adapter_schedules = itemView.findViewById(R.id.rl_adapter_schedules);
            iv_device_logo_schedules = itemView.findViewById(R.id.iv_device_logo_schedules);
            iv_timer_icon = itemView.findViewById(R.id.iv_timer_icon);
            iv_schedule_icon = itemView.findViewById(R.id.iv_schedule_icon);
            tv_device_name_schedules = itemView.findViewById(R.id.tv_device_name_schedules);
        }
    }
}
