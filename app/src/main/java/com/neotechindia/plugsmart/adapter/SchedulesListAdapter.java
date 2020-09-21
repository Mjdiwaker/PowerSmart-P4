package com.neotechindia.plugsmart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.listeners.IScheduleEnableDisableListener;
import com.neotechindia.plugsmart.listeners.RecyclerItemClickListener;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.model.JsonDataFieldsModel;
import com.neotechindia.plugsmart.model.RecyclerSelectorScheduleModel;

import java.util.ArrayList;

public class SchedulesListAdapter extends RecyclerView.Adapter<SchedulesListAdapter.MyViewHolder> {
    Context _context;
    View view;
    ArrayList<JsonDataFieldsModel> al = new ArrayList();
    DeviceBean deviceBean = new DeviceBean();
    IScheduleEnableDisableListener iScheduleEnableDisableListener;
    public int mSelectedItem = -1;
 /*   public SchedulesListAdapter(Context _context, ArrayList<JsonDataFieldsModel> al, DeviceBean deviceBean) {
        this._context = _context;
        this.al = al;
        this.deviceBean = deviceBean;
    }*/
    public SchedulesListAdapter(Context _context, ArrayList<JsonDataFieldsModel> al, DeviceBean deviceBean,int pos) {
        this._context = _context;
        this.al = al;
        this.deviceBean = deviceBean;
        this.mSelectedItem=pos;
    }
    @NonNull
    @Override
    public SchedulesListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(_context).inflate(R.layout.adapter_schedules_view, null, false);
        SchedulesListAdapter.MyViewHolder viewHolder = new SchedulesListAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SchedulesListAdapter.MyViewHolder holder, final int position) {
        final JsonDataFieldsModel jsonDataFieldsModel = al.get(position);
        holder.tv_sr_no.setText(jsonDataFieldsModel.getOne());
        int startHours = Integer.parseInt(jsonDataFieldsModel.getThree()) / 60;
        int startMin = Integer.parseInt(jsonDataFieldsModel.getThree()) % 60;
        holder.tv_start_time.setText("Start Time: " + String.format("%02d", startHours) + " : " + String.format("%02d", startMin));
        int endHours = Integer.parseInt(jsonDataFieldsModel.getFour()) / 60;
        int endtMin = Integer.parseInt(jsonDataFieldsModel.getFour()) % 60;
        holder.tv_end_time.setText("End Time:   " + String.format("%02d", endHours) + " : " + String.format("%02d", endtMin));
        String result = getDaysString(jsonDataFieldsModel.getFive());
        holder.tv_days.setText(result);
        if (jsonDataFieldsModel.getTwo().equalsIgnoreCase("1")) {
            holder.sb_schedule.setCheckedNoEvent(true);
        } else {
            holder.sb_schedule.setCheckedNoEvent(false);
        }
        if(position==mSelectedItem)
        {
            holder.rl.setSelected(true);
            holder.rl.setBackgroundColor(_context.getResources().getColor(R.color.colorDivider));
        }
        holder.col_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonDataFieldsModel jsonDataFieldsModel = al.get(position);
                RecyclerSelectorScheduleModel recyclerSelectorScheduleModel = new RecyclerSelectorScheduleModel();
                recyclerSelectorScheduleModel.one = jsonDataFieldsModel.getOne();
                recyclerSelectorScheduleModel.two = jsonDataFieldsModel.getTwo();
                recyclerSelectorScheduleModel.three = jsonDataFieldsModel.getThree();
                recyclerSelectorScheduleModel.four = jsonDataFieldsModel.getFour();
                recyclerSelectorScheduleModel.five = jsonDataFieldsModel.getFive();

                if (holder.rl.isSelected()) {

                    holder.rl.setSelected(false);
                    holder.rl.setBackgroundColor(_context.getResources().getColor(R.color.white));
                    recyclerItemClickListener = ConstantUtil.recyclerItemClickListener;
                    recyclerItemClickListener.onItemClickListener(null,mSelectedItem);
                    notifyDataSetChanged();
                } else {
                    mSelectedItem=position;
                    holder.rl.setSelected(true);
                    holder.rl.setBackgroundColor(_context.getResources().getColor(R.color.colorDivider));
                    recyclerItemClickListener = ConstantUtil.recyclerItemClickListener;
                    recyclerItemClickListener.onItemClickListener(recyclerSelectorScheduleModel,mSelectedItem);
                    notifyDataSetChanged();
                }
                // holder.itemView.getDrawingCacheBackgroundColor()
                // holder.itemView.setBackgroundColor(_context.getResources().getColor(R.color.colorDivider));
            }
        });
        holder.tv_sr_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonDataFieldsModel jsonDataFieldsModel = al.get(position);
                RecyclerSelectorScheduleModel recyclerSelectorScheduleModel = new RecyclerSelectorScheduleModel();
                recyclerSelectorScheduleModel.one = jsonDataFieldsModel.getOne();
                recyclerSelectorScheduleModel.two = jsonDataFieldsModel.getTwo();
                recyclerSelectorScheduleModel.three = jsonDataFieldsModel.getThree();
                recyclerSelectorScheduleModel.four = jsonDataFieldsModel.getFour();
                recyclerSelectorScheduleModel.five = jsonDataFieldsModel.getFive();

                if (holder.rl.isSelected()) {
                    holder.rl.setBackgroundColor(_context.getResources().getColor(R.color.white));
                    holder.rl.setSelected(false);
                    recyclerItemClickListener = ConstantUtil.recyclerItemClickListener;
                    recyclerItemClickListener.onItemClickListener(null,mSelectedItem);
                    notifyDataSetChanged();
                } else {
                    mSelectedItem=position;
                    holder.rl.setSelected(true);
                    holder.rl.setBackgroundColor(_context.getResources().getColor(R.color.colorDivider));
                    recyclerItemClickListener = ConstantUtil.recyclerItemClickListener;
                    recyclerItemClickListener.onItemClickListener(recyclerSelectorScheduleModel,mSelectedItem);
                    notifyDataSetChanged();
                }
            }
        });
        holder.sb_schedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                iScheduleEnableDisableListener = ConstantUtil.scheduleEnableDisableState;
                iScheduleEnableDisableListener.scheduleState(b, jsonDataFieldsModel);
                if (b) {
                    holder.sb_schedule.setCheckedNoEvent(true);


                } else {
                    holder.sb_schedule.setCheckedNoEvent(false);


                }
            }
        });


    }

    private String getDaysString(String days) {
        String res = "";
        char[] dayArr = days.toCharArray();

        for (char c : dayArr) {
            switch (c) {
                case '1':
                    res += " " + "Mon";
                    break;
                case '2':
                    res += " " + "Tue";
                    break;
                case '3':
                    res += " " + "Wed";
                    break;
                case '4':
                    res += " " + "Thu";
                    break;
                case '5':
                    res += " " + "Fri";
                    break;
                case '6':
                    res += " " + "Sat";
                    break;
                case '7':
                    res += " " + "Sun";
                    break;
            }
        }
     /*   switch (days) {
            case "1234567": {
                res = "EveryDay";
                break;
            }
            case "12345": {
                res = "Monday To Friday";
                break;
            }
            default: {

                res = "Custom Days";
                break;
            }
        }*/
        return res;
    }

    @Override
    public int getItemCount() {

        return al.size();
    }

    private static RecyclerItemClickListener recyclerItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        SwitchButton sb_schedule;
        TextView tv_start_time, tv_end_time, tv_days, tv_sr_no;
        LinearLayout col_2;
        RelativeLayout rl;

        public MyViewHolder(final View itemView) {
            super(itemView);
            rl = itemView.findViewById(R.id.rl);
            tv_start_time = itemView.findViewById(R.id.tv_start_time);
            tv_end_time = itemView.findViewById(R.id.tv_end_time);
            tv_days = itemView.findViewById(R.id.tv_days);
            tv_sr_no = itemView.findViewById(R.id.tv_sr_no);
            col_2 = itemView.findViewById(R.id.col_2);
            sb_schedule = itemView.findViewById(R.id.sb_schedule);
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JsonDataFieldsModel jsonDataFieldsModel = al.get(getAdapterPosition());
                    RecyclerSelectorScheduleModel recyclerSelectorScheduleModel = new RecyclerSelectorScheduleModel();
                    recyclerSelectorScheduleModel.one = jsonDataFieldsModel.getOne();
                    recyclerSelectorScheduleModel.two = jsonDataFieldsModel.getTwo();
                    recyclerSelectorScheduleModel.three = jsonDataFieldsModel.getThree();
                    recyclerSelectorScheduleModel.four = jsonDataFieldsModel.getFour();
                    recyclerSelectorScheduleModel.five = jsonDataFieldsModel.getFive();

                    if (rl.isSelected()) {

                        rl.setSelected(false);
                        rl.setBackgroundColor(_context.getResources().getColor(R.color.white));
                        recyclerItemClickListener = ConstantUtil.recyclerItemClickListener;
                        recyclerItemClickListener.onItemClickListener(null,mSelectedItem);
                    } else {
                        mSelectedItem=getAdapterPosition();
                        rl.setSelected(true);
                        rl.setBackgroundColor(_context.getResources().getColor(R.color.colorDivider));
                        recyclerItemClickListener = ConstantUtil.recyclerItemClickListener;
                        recyclerItemClickListener.onItemClickListener(recyclerSelectorScheduleModel,mSelectedItem);
                    }
                }
            });
        }
    }
}
