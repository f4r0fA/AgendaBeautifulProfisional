package com.digtech.agendaprofisional.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digtech.agendaprofisional.Common.MyDiffCallBack;
import com.digtech.agendaprofisional.Model.MyNotification;
import com.digtech.agendaprofisional.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyNotificationAdapter extends RecyclerView.Adapter<MyNotificationAdapter.MyViewHolder> {

    Context context;
    List<MyNotification> myNotificationList;

    public MyNotificationAdapter(Context context, List<MyNotification> myNotificationList) {
        this.context = context;
        this.myNotificationList = myNotificationList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_notification_item,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_notification_title.setText(myNotificationList.get(i).getTitle());
        myViewHolder.getTxt_notification_content.setText(myNotificationList.get(i).getContent());

    }

    @Override
    public int getItemCount() {
        return myNotificationList.size();
    }

    public void updateList(List<MyNotification> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffCallBack(this.myNotificationList, newList));
        myNotificationList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.txt_notification_title)
        TextView txt_notification_title;
        @BindView(R.id.txt_notification_content)
        TextView getTxt_notification_content;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
