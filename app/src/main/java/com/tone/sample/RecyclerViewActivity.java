package com.tone.sample;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tone.library.refreshview.PullToRefreshView;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class RecyclerViewActivity extends AppCompatActivity {

    private PullToRefreshView pullToRefreshView;
    private ArrayList<Integer> strings = new ArrayList<>();
    private RecyclerView recyclerView;

    private Adapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 1) {
                pullToRefreshView.onComplete(true, false, false);
                strings.clear();
                adapter.notifyDataSetChanged();
            }
            if (what == 2) {
                pullToRefreshView.onComplete(false, true, false);
                for (int i = 0; i < 10; i++) {
                    strings.add(i);
                }
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pulltorefreshview);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.onRefreshListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(1, 3000);
            }

            @Override
            public void onLoadMore() {
                handler.sendEmptyMessageDelayed(2, 3000);
            }
        });
    }

    private class Adapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    RecyclerViewActivity.this).inflate(R.layout.list_item, parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.textView.setText("item"+position);
        }

        @Override
        public int getItemCount() {
            return strings.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public MyViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.text);
        }
    }

}
