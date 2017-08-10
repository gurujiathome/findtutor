package com.widiarifki.findtutor.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.model.Complain;

import java.util.List;

/**
 * Created by widiarifki on 26/07/2017.
 */

public class HelpAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<Complain> mComplainList;
    private final ProgressDialog mProgressDialog;
    private final Fragment mFragment;

    public HelpAdapter(Context context, List<Complain> complainList, Fragment fragment) {
        mContext = context;
        mComplainList = complainList;
        mProgressDialog = new ProgressDialog(mContext);
        mFragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_complain, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Complain dataItem = mComplainList.get(position);

        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.tvComplain.setText(dataItem.getComplain());
        if(dataItem.getReply().isEmpty() || dataItem.getReply().equals("null")) {
            viewHolder.tvReplyLabel.setVisibility(View.GONE);
            viewHolder.tvReply.setVisibility(View.GONE);
            viewHolder.tvNoReply.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.tvReplyLabel.setVisibility(View.VISIBLE);
            viewHolder.tvReply.setVisibility(View.VISIBLE);
            viewHolder.tvReply.setText(dataItem.getReply());
        }
    }

    @Override
    public int getItemCount() {
        return mComplainList.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvComplain;
        public TextView tvNoReply;
        public TextView tvReply;
        public TextView tvReplyLabel;
        public LinearLayout layoutReply;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvComplain = (TextView) itemView.findViewById(R.id.tvComplain);
            tvNoReply = (TextView) itemView.findViewById(R.id.tvNoReply);
            tvReply = (TextView) itemView.findViewById(R.id.tvReply);
            tvReplyLabel = (TextView) itemView.findViewById(R.id.tvReplyLabel);
            layoutReply = (LinearLayout) itemView.findViewById(R.id.layoutReply);
        }
    }
}
