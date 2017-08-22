package com.example.administrator.myironguard.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myironguard.Bean.Sms;
import com.example.administrator.myironguard.R;

import java.util.ArrayList;

/**
 * Created by sunginous on 2017/2/7.
 */

public class MessageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Sms> smsArrayList;
    public MessageAdapter(ArrayList<Sms> smsArrayList,Context context){
        this.smsArrayList=smsArrayList;
        this.context=context;
    }
    @Override
    public int getCount() {
        return smsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return smsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            Sms sms=smsArrayList.get(position);
            ViewHolder viewHolder;
            if (convertView==null){
                convertView=LayoutInflater.from(context).inflate(R.layout.listview_message,null);
                viewHolder=new ViewHolder();
                viewHolder.textView_name=(TextView)convertView.findViewById(R.id.text_name);
                viewHolder.textView_number=(TextView)convertView.findViewById(R.id.text_number);
                viewHolder.imageView_contract=(ImageView)convertView.findViewById(R.id.image_contract);
                convertView.setTag(viewHolder);
            }else {
                viewHolder=(ViewHolder)convertView.getTag();
            }
            viewHolder.textView_name.setText(sms.name);
            viewHolder.textView_number.setText(sms.number);
            viewHolder.imageView_contract.setImageResource(R.drawable.lianxiren);
        }catch (Exception e){
            Toast.makeText(context,"操作太快",Toast.LENGTH_SHORT).show();
        }
        return convertView;
    }

    class ViewHolder{
        TextView textView_name;
        TextView textView_number;
        ImageView imageView_contract;
    }
}
