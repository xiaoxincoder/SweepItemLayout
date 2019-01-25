package com.keepshare.sweepitemlayoutdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mListView;
    private SimpleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.list_view);

        initList();

        setData();
    }

    private void initList() {
        final LinearLayoutManager lm = new LinearLayoutManager(this);
        mListView.setLayoutManager(lm);
        mListView.setHasFixedSize(true);

        mAdapter = new SimpleAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    private void setData() {
        final List<String> dataList = new ArrayList<>(30);
        for (int i = 0; i < 30; i++) {
            dataList.add("第" + i + "条数据");
        }

        mAdapter.setDataList(dataList);
    }
}
