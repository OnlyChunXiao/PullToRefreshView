package com.tone.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.tone.library.refreshview.PullToRefreshView;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {
    private PullToRefreshView pullToRefreshView;
    private ListView listView;

    private ArrayList<Integer> strings = new ArrayList<>();


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what==1) {
                pullToRefreshView.onComplete(true,false,false);
                strings.clear();
                adapter.notifyDataSetChanged();
            }
            if (what==2) {
                pullToRefreshView.onComplete(false,true,false);
                for(int i=0;i<10;i++){
                    strings.add(i);
                }
                adapter.notifyDataSetChanged();
            }
        }
    };

    private Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pulltorefreshview);
        listView = (ListView) findViewById(R.id.listview);

        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.onRefreshListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(1,3000);
            }

            @Override
            public void onLoadMore() {
                handler.sendEmptyMessageDelayed(2,3000);
            }
        });
        adapter = new Adapter();
        listView.setAdapter(adapter);
    }

    class Adapter extends BaseAdapter{
        public Adapter() {
        }

        @Override
        public int getCount() {
            return strings.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView==null){
                convertView= View.inflate(ListViewActivity.this, R.layout.list_item, null);
                viewHolder=new ViewHolder();
                viewHolder.textView=(TextView)convertView.findViewById(R.id.text);
                convertView.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder)convertView.getTag();
            }
            viewHolder.textView.setText("item"+position);
            return convertView;
        }


    }
    private class ViewHolder{
        TextView textView;
    }

}
