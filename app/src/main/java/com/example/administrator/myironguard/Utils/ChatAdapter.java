package com.example.administrator.myironguard.Utils;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.myironguard.Bean.Sms;
import com.example.administrator.myironguard.R;

import java.util.ArrayList;

/**
 * Created by sunginous on 2017/2/24.
 */

public class ChatAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Sms> smsArrayList;
    public ChatAdapter(ArrayList<Sms> smsArrayList,Context context){
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
        Sms sms=smsArrayList.get(position);
        ViewHolder viewHolder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.message_item,null);
            viewHolder=new ViewHolder();
            viewHolder.leftLayout=(LinearLayout) convertView.findViewById(R.id.left_layout);
            viewHolder.rightLayout=(LinearLayout) convertView.findViewById(R.id.right_layout);
            viewHolder.leftMsg=(TextView) convertView.findViewById(R.id.message_left);
            viewHolder.rightMsg=(TextView)convertView.findViewById(R.id.message_right);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder) convertView.getTag();
        }
        if (sms.content==null){
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.GONE);
        }
        if (sms.type==1) {
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(sms.content);
        } else if(sms.type==2) {
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightMsg.setText(sms.content);
        }
        return convertView;
    }

    class ViewHolder{
        TextView leftMsg;
        TextView rightMsg;
        LinearLayout leftLayout;
        LinearLayout rightLayout;
    }
}
