package com.neotechindia.plugsmart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.neotechindia.plugsmart.R;

import java.util.ArrayList;

public class GadgetNameImageAdapter extends BaseAdapter {
    Context context;
    ArrayList<Integer> arrayList = new ArrayList<>();

    public GadgetNameImageAdapter(Context context, ArrayList<Integer> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_device_image, null, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = view.findViewById(R.id.imageView);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.imageView.setBackground(context.getResources().getDrawable(arrayList.get(i)));
        return view;
    }

    private static class ViewHolder {
        ImageView imageView;
    }
}
