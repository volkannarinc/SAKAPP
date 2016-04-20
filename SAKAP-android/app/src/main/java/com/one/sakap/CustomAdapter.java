package com.one.sakap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cscmehmet on 21.03.2015.
 */
public class CustomAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    Context context;
    String [] result;
    List<BusDto> busDtoList;

    public CustomAdapter(MainActivity mainActivity, List<BusDto> busDto) {

        context         =   mainActivity;
        inflater        =   (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Comparator<BusDto> cmp = new Comparator<BusDto>() {
            public int compare(BusDto o1, BusDto o2) {
                return Integer.valueOf(o1.getDistance().intValue()).compareTo(Integer.valueOf(o2.getDistance().intValue()));
            }
        };

        Collections.sort(busDto, cmp);
        busDtoList      =   busDto;

        SystemValues.waitForView = true;
    }

    @Override
    public int getCount() {

        return busDtoList.size();
    }

    @Override
    public Object getItem(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    public class Holder
    {
        TextView tv_name;
        TextView tv_prev;
        TextView tv_next;
    }

    Holder holder;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView;
        holder = new Holder();

        rowView     =   inflater.inflate(R.layout.program_list, null);

        holder.tv_name   =   (TextView) rowView.findViewById(R.id.textView1);
        holder.tv_prev   =   (TextView) rowView.findViewById(R.id.textView2);
        holder.tv_next   =   (TextView) rowView.findViewById(R.id.textView3);

        try {

            holder.tv_name.setText(busDtoList.get(position).getName().toString());
            holder.tv_prev.setText(busDtoList.get(position).getPrevLocation().toString());
            holder.tv_next.setText(busDtoList.get(position).getNextLocation().toString());

        }catch (Exception e){}

        return rowView;
    }

}
