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
import com.neotechindia.plugsmart.listeners.RecyclerItemClickListener;
import com.neotechindia.plugsmart.model.JsonDataFieldsModel;

import java.util.ArrayList;

public class SchedulesListNewAdapter extends RecyclerView.Adapter<SchedulesListNewAdapter.MyViewHolder> {
    private Context _context;
    private ArrayList<JsonDataFieldsModel> al;
    private int mSelectedItem = -1;
    private OnListItemClickCallBack mOnListItemClickCallBack;

    public SchedulesListNewAdapter(Context _context, ArrayList<JsonDataFieldsModel> al, OnListItemClickCallBack mOnListItemClickCallBack) {
        this._context = _context;
        this.al = al;
        this.mOnListItemClickCallBack = mOnListItemClickCallBack;
    }

    @NonNull
    @Override
    public SchedulesListNewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SchedulesListNewAdapter.MyViewHolder(LayoutInflater.from(_context).inflate(R.layout.adapter_schedules_view, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SchedulesListNewAdapter.MyViewHolder holder, final int position) {
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
        if (jsonDataFieldsModel.isSelected() == 1) {
            holder.rl.setBackgroundColor(_context.getResources().getColor(R.color.colorDivider));
        } else {
            holder.rl.setBackgroundColor(_context.getResources().getColor(R.color.white));
        }
        if (jsonDataFieldsModel.getTwo().equalsIgnoreCase("1")) {
            holder.sb_schedule.setCheckedNoEvent(true);
        } else {
            holder.sb_schedule.setCheckedNoEvent(false);
        }
        if (position == mSelectedItem) {
            holder.rl.setSelected(true);
            holder.rl.setBackgroundColor(_context.getResources().getColor(R.color.colorDivider));
        }
        holder.col_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mOnListItemClickCallBack.onItemClick(view, holder.getAdapterPosition());



            }
        });
        holder.sb_schedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mOnListItemClickCallBack.onItemClick(holder.sb_schedule, position);
            }
        });


    }

    public ArrayList<JsonDataFieldsModel> getUpdatedList() {
        return this.al;
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnListItemClickCallBack.onItemClick(view, getAdapterPosition());
                }
            });
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnListItemClickCallBack.onItemClick(view, getAdapterPosition());
                }
            });
        }
    }

    public interface OnListItemClickCallBack {
        public void onItemClick(View view, int pos);
    }
}
