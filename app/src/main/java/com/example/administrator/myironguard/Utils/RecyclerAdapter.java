package com.example.administrator.myironguard.Utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.myironguard.Bean.Sms;
import com.example.administrator.myironguard.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Sms> mMsgList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftLayout;

        LinearLayout rightLayout;

        TextView leftMsg;

        TextView rightMsg;

        TextView leftDate;
        //TextView leftTime;
        TextView rightDate;
        //TextView rightTime;

        public ViewHolder(View view) {
            super(view);
            leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            leftMsg = (TextView) view.findViewById(R.id.message_left);
            rightMsg = (TextView) view.findViewById(R.id.message_right);
            leftDate=(TextView)view.findViewById(R.id.date_left);
            rightDate=(TextView)view.findViewById(R.id.date_right);
        }
    }

    public RecyclerAdapter(ArrayList<Sms> msgList,Context context) {
        this.mMsgList = msgList;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Sms sms= mMsgList.get(position);
        if (sms.content==null){
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftDate.setVisibility(View.GONE);
            holder.rightDate.setVisibility(View.GONE);
        }
        if (sms.type==1) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.rightDate.setVisibility(View.GONE);
            holder.leftMsg.setText(sms.content);

            long time = System.currentTimeMillis();
            SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日HH:mm");
            Date date=new Date(time);
            String now=format.format(date);
            if (now.substring(0,4).equals(sms.date.substring(0,4))
                    &&now.substring(4,7).equals(sms.date.substring(4,7))
                    &&now.substring(7,10).equals(sms.date.substring(7,10))){
                holder.leftDate.setText("今天"+sms.date.substring(11));//年月日均相同
            }else if (!now.substring(0,4).equals(sms.date.substring(0,4))){
                if (sms.date.substring(5,6).equals("0")){
                    if (sms.date.substring(8,9).equals("0")){
                        holder.leftDate.setText(sms.date.substring(0,5)+sms.date.substring(6,8)
                        +sms.date.substring(9));
                    }else {
                        holder.leftDate.setText(sms.date.substring(0,5)+sms.date.substring(6));
                    }
                }else {
                    holder.leftDate.setText(sms.date);//不同年
                }
            }else {
                if (sms.date.substring(5,6).equals("0")){
                    if (sms.date.substring(8,9).equals("0")){
                        holder.leftDate.setText(sms.date.substring(6,8)+sms.date.substring(9));
                    }else {
                        holder.leftDate.setText(sms.date.substring(6));
                    }
                }else {
                    holder.leftDate.setText(sms.date.substring(5));//同年不同日期
                }
            }

        } else if(sms.type==2) {
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.leftDate.setVisibility(View.GONE);
            holder.rightMsg.setText(sms.content);

            long time = System.currentTimeMillis();
            SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日HH:mm");
            Date date=new Date(time);
            String now=format.format(date);
            if (now.substring(0,4).equals(sms.date.substring(0,4))
                    &&now.substring(4,7).equals(sms.date.substring(4,7))
                    &&now.substring(7,10).equals(sms.date.substring(7,10))){
                holder.rightDate.setText("今天"+sms.date.substring(11));//年月日均相同
            }else if (!now.substring(0,4).equals(sms.date.substring(0,4))){
                if (sms.date.substring(5,6).equals("0")){
                    if (sms.date.substring(8,9).equals("0")){
                        holder.rightDate.setText(sms.date.substring(0,5)+sms.date.substring(6,8)
                        +sms.date.substring(9));
                    }else {
                        holder.rightDate.setText(sms.date.substring(0,5)+sms.date.substring(6));
                    }
                }else {
                    holder.rightDate.setText(sms.date);//不同年
                }
            }else {
                if (sms.date.substring(5, 6).equals("0")) {
                    if (sms.date.substring(8,9).equals("0")){
                        holder.rightDate.setText(sms.date.substring(6,8)+sms.date.substring(9));
                    }else {
                        holder.rightDate.setText(sms.date.substring(6));
                    }
                } else {
                    holder.rightDate.setText(sms.date.substring(5));//同年不同日期
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

}

