package com.keepshare.sweepitemlayoutdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BUG君 on 2019/1/25.
 */
public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {

    private Context mContext;
    private List<String> dataList = new ArrayList<>();

    public SimpleAdapter(Context context) {
        mContext = context;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SimpleViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_simple, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder simpleViewHolder, int i) {
        simpleViewHolder.tvContent.setText(dataList.get(i));

        simpleViewHolder.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "点击跳转", Toast.LENGTH_SHORT).show();
            }
        });

        simpleViewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "编辑", Toast.LENGTH_SHORT).show();
            }
        });

        simpleViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "删除", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList != null && dataList.size() > 0 ? dataList.size() : 0;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        private TextView btnDelete;
        private TextView btnEdit;
        private TextView tvContent;

        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);

            btnDelete = itemView.findViewById(R.id.item_delete_btn);
            btnEdit = itemView.findViewById(R.id.item_edit_btn);
            tvContent = itemView.findViewById(R.id.item_content);
        }
    }
}

