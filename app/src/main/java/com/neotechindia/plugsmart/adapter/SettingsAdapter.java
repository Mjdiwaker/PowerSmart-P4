package com.neotechindia.plugsmart.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.Logger;
import com.neotechindia.plugsmart.activity.AddGadgetActivity;
import com.neotechindia.plugsmart.activity.SettingsActivity;
import com.neotechindia.plugsmart.listeners.IAuthentcUser;
import com.neotechindia.plugsmart.model.DeviceBean;

import java.util.ArrayList;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {
    Context _context;
    View view;
    ArrayList<DeviceBean> al = new ArrayList();
    private static IAuthentcUser mAuthentcUser;

    public SettingsAdapter(Context _context, ArrayList<DeviceBean> al) {
        this._context = _context;
        this.al = al;
    }

    @NonNull
    @Override
    public SettingsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(_context).inflate(R.layout.adapter_settings_device_list, null, false);
        SettingsAdapter.MyViewHolder viewHolder = new SettingsAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsAdapter.MyViewHolder holder, int position) {
/*        String pos = String.valueOf(position + 1);
        holder.tv_device_name_setting_adapter.setText("Device-" + pos);*/

        if (ConstantUtil.isForAPModeOnly) {
            SharedPreferences firmwareSharedPref = _context.getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            holder.tv_device_name_setting_adapter.setText(firmwareSharedPref.getString(ConstantUtil.gadgetName, ""));
//            Log.i("tester","my test "+firmwareSharedPref.getString(ConstantUtil.gadgetName,""));
//            Log.i("tester","my test "+firmwareSharedPref.getString(ConstantUtil.gadgetPos,""));
            String icon_type = firmwareSharedPref.getString(ConstantUtil.gadgetPos,"");
            if (Logger.isPlugSmart) {
                switch (icon_type) {
                    case "0": {
                        holder.iv_device_logo_settings.setImageResource(R.drawable.ac_black);
//                        Toast.makeText(_context, "type "+icon_type+" case "+0, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "1": {
//                        Toast.makeText(_context, "type "+icon_type+" case "+1, Toast.LENGTH_SHORT).show();
                        holder.iv_device_logo_settings.setImageResource(R.drawable.tv_black);
                        break;
                    }
                    case "2": {
//                        Toast.makeText(_context, "type "+icon_type+" case "+2, Toast.LENGTH_SHORT).show();
                        holder.iv_device_logo_settings.setImageResource(R.drawable.fan_black);
                        break;
                    }
                    case "3": {
//                        Toast.makeText(_context, "type "+icon_type+" case "+3, Toast.LENGTH_SHORT).show();
                        holder.iv_device_logo_settings.setImageResource(R.drawable.fridge_black);
                        break;
                    }
                    case "4": {
//                        Toast.makeText(_context, "type "+icon_type+" case "+4, Toast.LENGTH_SHORT).show();
                        holder.iv_device_logo_settings.setImageResource(R.drawable.geyser_black);
                        break;
                    }
                    case "5": {
//                        Toast.makeText(_context, "type "+icon_type+" case "+5, Toast.LENGTH_SHORT).show();
                        holder.iv_device_logo_settings.setImageResource(R.drawable.washing_machine_black);
                        break;
                    }
                    case "6": {
//                        Toast.makeText(_context, "type "+icon_type+" case "+6, Toast.LENGTH_SHORT).show();
                        holder.iv_device_logo_settings.setImageResource(R.drawable.lugo);
                        break;
                    }
                    default: {
//                        Toast.makeText(_context, "type "+icon_type+" case def", Toast.LENGTH_SHORT).show();
                        holder.iv_device_logo_settings.setImageResource(R.drawable.lugo);
                        break;
                    }
                }
            }
            holder.iv_settings_edit_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConstantUtil.isFromSettings = true;
                    Intent i = new Intent(_context, AddGadgetActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);
                }
            });
            holder.iv_settings_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(_context, SettingsActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);
                }
            });
        } else {
            if (al != null) {
                if (al.size() != 0) {
                    final DeviceBean deviceBean = al.get(position);
                    holder.tv_device_name_setting_adapter.setText(deviceBean.name);
                    holder.rl_adapter_settings.setBackgroundColor(Color.parseColor(deviceBean.status));
                    Log.i("tester","case "+deviceBean.iconType);
                    if (Logger.isPlugSmart) {
                        switch (deviceBean.iconType) {
                            case "0": {
                                holder.iv_device_logo_settings.setImageResource(R.drawable.ac_black);
                                break;
                            }
                            case "1": {
                                holder.iv_device_logo_settings.setImageResource(R.drawable.tv_black);
                                break;
                            }
                            case "2": {
                                holder.iv_device_logo_settings.setImageResource(R.drawable.fan_black);
                                break;
                            }
                            case "3": {
                                holder.iv_device_logo_settings.setImageResource(R.drawable.fridge_black);
                                break;
                            }
                            case "4": {
                                holder.iv_device_logo_settings.setImageResource(R.drawable.geyser_black);
                                break;
                            }
                            case "5": {
                                holder.iv_device_logo_settings.setImageResource(R.drawable.washing_machine_black);
                                break;
                            }
                            case "6": {
                                holder.iv_device_logo_settings.setImageResource(R.drawable.lugo);
                                break;
                            }
                            default: {
                                holder.iv_device_logo_settings.setImageResource(R.drawable.lugo);
                                break;
                            }
                        }
                    }
                    holder.iv_settings_edit_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if ((deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDeviceNotReg))) ||
                                    (deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDevice_On_edge))) ||
                                    (deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDevice_On_edge_notConnected)))) {
                                mAuthentcUser = ConstantUtil.authentcUser;
                                mAuthentcUser.findAuthenticity(deviceBean);
                                return;
                            }
                            ConstantUtil.isFromSettings = true;
                            Intent i = new Intent(_context, AddGadgetActivity.class);
                            i.putExtra(ConstantUtil.stationList, deviceBean);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            _context.startActivity(i);
                        }
                    });
                    holder.iv_settings_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if ((deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDeviceNotReg))) ||
                                    (deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDevice_On_edge))) ||
                                    (deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDevice_On_edge_notConnected)))) {
                                mAuthentcUser = ConstantUtil.authentcUser;
                                mAuthentcUser.findAuthenticity(deviceBean);
                                return;
                            }
                            Intent i = new Intent(_context, SettingsActivity.class);
                            i.putExtra(ConstantUtil.stationList, deviceBean);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            _context.startActivity(i);
                        }
                    });
                }
            }
        }

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
        TextView tv_device_name_setting_adapter;
        ImageView iv_device_logo_settings, iv_settings_icon, iv_settings_edit_icon;
        RelativeLayout rl_adapter_settings;

        public MyViewHolder(View itemView) {
            super(itemView);
            rl_adapter_settings = itemView.findViewById(R.id.rl_adapter_settings);
            iv_device_logo_settings = itemView.findViewById(R.id.iv_device_logo_settings);
            iv_settings_icon = itemView.findViewById(R.id.iv_settings_icon);
            iv_settings_edit_icon = itemView.findViewById(R.id.iv_settings_edit_icon);
            tv_device_name_setting_adapter = itemView.findViewById(R.id.tv_device_name_setting_adapter);
        }
    }
}
