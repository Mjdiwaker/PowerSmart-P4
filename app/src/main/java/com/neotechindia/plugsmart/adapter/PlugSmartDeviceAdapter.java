package com.neotechindia.plugsmart.adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.Logger;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.IAuthentcUser;
import com.neotechindia.plugsmart.listeners.IDeviceInfo;
import com.neotechindia.plugsmart.listeners.IPlugOnOff;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;

import java.util.ArrayList;

public class PlugSmartDeviceAdapter extends RecyclerView.Adapter<PlugSmartDeviceAdapter.MyViewHolder> {
    Context _context;
    View view;
    ArrayList<DeviceBean> al = new ArrayList();
    private static final String TAG = "PlugSmartDeviceAdapter";

    private static IAuthentcUser mAuthentcUser;
    private static IDeviceInfo mDeviceInfo;

    public PlugSmartDeviceAdapter(Context _context, ArrayList<DeviceBean> al) {
        this._context = _context;
        this.al = al;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(_context).inflate(R.layout.adapter_device_list, null, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        if (Logger.isPlugSmart) {
            holder.iv_device_info.setVisibility(View.GONE);
        } else {
            holder.iv_device_info.setVisibility(View.VISIBLE);
        }
        if (ConstantUtil.isForAPModeOnly) {
            if (al != null) {
                if (al.size() != 0) {
                    final DeviceBean deviceBean = al.get(position);
                    holder.tv_device_name.setText(deviceBean.name);
                    if (deviceBean.switchs) {
                        holder.sb_device.setCheckedNoEvent(true);
                    } else {
                        holder.sb_device.setCheckedNoEvent(false);
                    }
                }
            }
            SharedPreferences firmwareSharedPref = _context.getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            holder.tv_device_name.setText(firmwareSharedPref.getString(ConstantUtil.gadgetName, ""));
            if (Logger.isPlugSmart) {
                switch (firmwareSharedPref.getString(ConstantUtil.gadgetPos, "")) {
                    case "0": {
                        holder.iv_device_logo.setImageResource(R.drawable.ac_black);
                        break;
                    }
                    case "1": {
                        holder.iv_device_logo.setImageResource(R.drawable.tv_black);
                        break;
                    }
                    case "2": {
                        holder.iv_device_logo.setImageResource(R.drawable.fan_black);
                        break;
                    }
                    case "3": {
                        holder.iv_device_logo.setImageResource(R.drawable.fridge_black);
                        break;
                    }
                    case "4": {
                        holder.iv_device_logo.setImageResource(R.drawable.geyser_black);
                        break;
                    }
                    case "5": {
                        holder.iv_device_logo.setImageResource(R.drawable.washing_machine_black);
                        break;
                    }
                    case "6": {
                        holder.iv_device_logo.setImageResource(R.drawable.lugo);
                        break;
                    }
                    default: {
                        holder.iv_device_logo.setImageResource(R.drawable.lugo);
                        break;
                    }
                }
            }
            holder.sb_device.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.d(TAG, "onCheckedChanged: " + b);
                    if (b) {
                        holder.sb_device.setCheckedNoEvent(true);


                        MyWifiReceiver.write(PacketsUrl.setPowerStatus("0"), CommandUtil.cmd_set_power_status);
                    } else {
                        holder.sb_device.setCheckedNoEvent(false);
                         MyWifiReceiver.write(PacketsUrl.setPowerStatus("1"), CommandUtil.cmd_set_power_status);
                    }
                    IPlugOnOff iPlugOnOff = ConstantUtil.iPlugOnOff;
                    iPlugOnOff.plugState(b, null);
                }
            });
            holder.iv_device_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDeviceInfo = ConstantUtil.deviceInfo;
                    mDeviceInfo.deviceInfo(null);

                }
            });


        } else {
            if (al != null) {
                if (al.size() != 0) {
                    final DeviceBean deviceBean = al.get(position);
                    holder.tv_device_name.setText(deviceBean.name);
                    if (Logger.isPlugSmart) {

                        switch (deviceBean.iconType) {
                            case "0": {
                                holder.iv_device_logo.setImageResource(R.drawable.ac_black);
                                break;
                            }
                            case "1": {
                                holder.iv_device_logo.setImageResource(R.drawable.tv_black);
                                break;
                            }
                            case "2": {
                                holder.iv_device_logo.setImageResource(R.drawable.fan_black);
                                break;
                            }
                            case "3": {
                                holder.iv_device_logo.setImageResource(R.drawable.fridge_black);
                                break;
                            }
                            case "4": {
                                holder.iv_device_logo.setImageResource(R.drawable.geyser_black);
                                break;
                            }
                            case "5": {
                                holder.iv_device_logo.setImageResource(R.drawable.washing_machine_black);
                                break;
                            }
                            case "6": {
                                holder.iv_device_logo.setImageResource(R.drawable.lugo);
                                break;
                            }
                            default: {
                                holder.iv_device_logo.setImageResource(R.drawable.lugo);
                                break;
                            }
                        }
                    }
                    if (deviceBean.switchs) {
                        holder.sb_device.setCheckedNoEvent(true);
                    } else {
                        holder.sb_device.setCheckedNoEvent(false);
                    }
                    holder.ll_device_list.setBackgroundColor(Color.parseColor(deviceBean.status));

                    holder.sb_device.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            Log.d(TAG, "onCheckedChanged: " + b);
                            if (b) {

//                                holder.sb_device.setCheckedNoEvent(true);

                                if ((deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDeviceNotReg))) ||
                                        (deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDevice_On_edge))) ||
                                        (deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDevice_On_edge_notConnected)))) {
                                    mAuthentcUser = ConstantUtil.authentcUser;
                                    mAuthentcUser.findAuthenticity(deviceBean);
                                    return;
                                }

                                String packet = PacketsUrl.setPowerStatus("0");
                                //  Util.sendPacketStationMode(deviceBean, packet, _context);
                            } else {

                                if ((deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDeviceNotReg))) ||
                                        (deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDevice_On_edge))) ||
                                        (deviceBean.status.equalsIgnoreCase(_context.getResources().getString(R.string.colorDevice_On_edge_notConnected)))) {
                                    mAuthentcUser = ConstantUtil.authentcUser;
                                    mAuthentcUser.findAuthenticity(deviceBean);
                                    return;
                                }
                                holder.sb_device.setCheckedNoEvent(false);
                                String packet = PacketsUrl.setPowerStatus("1");
                                // Util.sendPacketStationMode(deviceBean, packet, _context);
                            }
                            IPlugOnOff iPlugOnOff = ConstantUtil.iPlugOnOff;
                            iPlugOnOff.plugState(b, deviceBean);
                        }
                    });
                    holder.iv_device_info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDeviceInfo = ConstantUtil.deviceInfo;
                            mDeviceInfo.deviceInfo(deviceBean);

                        }
                    });
                }
            }


        }
        // String pos=String.valueOf(position+1);
        // holder.tv_device_name.setText("Device-"+pos);

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
        ImageView iv_device_logo, iv_device_info;
        TextView tv_device_name;
        SwitchButton sb_device;
        RelativeLayout ll_device_list;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_device_logo = itemView.findViewById(R.id.iv_device_logo);
            tv_device_name = itemView.findViewById(R.id.tv_device_name);
            sb_device = itemView.findViewById(R.id.sb_device);
            ll_device_list = itemView.findViewById(R.id.ll_device_list);
            iv_device_info = itemView.findViewById(R.id.iv_device_info);
        }
    }
}
