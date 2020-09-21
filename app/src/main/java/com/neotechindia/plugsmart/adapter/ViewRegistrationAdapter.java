package com.neotechindia.plugsmart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.listeners.RemoveDeviceListener;
import com.neotechindia.plugsmart.model.RegisteredClientBean;

import java.util.ArrayList;

public class ViewRegistrationAdapter extends RecyclerView.Adapter<ViewRegistrationAdapter.MyViewHolder> {
    Context _context;
    View view;
    ArrayList<RegisteredClientBean> al = new ArrayList();
    private static RemoveDeviceListener removeDeviceListener;
    public ViewRegistrationAdapter(Context _context, ArrayList<RegisteredClientBean> al) {
        this._context = _context;
        this.al = al;
    }

    @NonNull
    @Override
    public ViewRegistrationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(_context).inflate(R.layout.adapter_registered_device_list, null, false);
        ViewRegistrationAdapter.MyViewHolder viewHolder = new ViewRegistrationAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewRegistrationAdapter.MyViewHolder holder, int position) {
        // DeviceBean deviceBean = al.get(position);
        final RegisteredClientBean registeredClientBean=al.get(position);
     /*   holder.tv_device_name.setText(deviceBean.name);
        if (deviceBean.switchs) {
            holder.sb_device.setCheckedNoEvent(true);
        } else {
            holder.sb_device.setCheckedNoEvent(false);
        }*/
        String pos=String.valueOf(position+1);
        holder.tv_registered_devices_adapter.setText(registeredClientBean.getName());
        holder.btn_submit_adapter_register_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeDeviceListener= ConstantUtil.removeListener;
                removeDeviceListener.onRemoveDevice(registeredClientBean.getName());

            }
        });
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView btn_submit_adapter_register_devices;
        TextView tv_registered_devices_adapter;


        public MyViewHolder(View itemView) {
            super(itemView);
            btn_submit_adapter_register_devices = itemView.findViewById(R.id.btn_submit_adapter_register_devices);
            tv_registered_devices_adapter = itemView.findViewById(R.id.tv_registered_devices_adapter);

        }
    }
}


