package com.example.activity.status;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lee.circleseekbar.R;
import com.example.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/** 冻结数据 */
public class OBDFrezenDataActivity extends BaseActivity {
    private ListView mListView;
    private List<DataModel> mDataSource;
    private MyFuctionsAdapter mAdapter;

    @Override
    public void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_obd_home);
    }

    @Override
    public void afterInitView() {
        initDataSource();
        mTvTitle.setText("冻结数据");
        mListView = (ListView) findViewById(R.id.lv_functionlist);
        mAdapter = new MyFuctionsAdapter();
        mListView.setAdapter(mAdapter);
    }

    private void initDataSource(){
        mDataSource = new ArrayList<DataModel>();
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
        mDataSource.add(new DataModel("参数描述", "参数值"));
    }

    private class DataModel{
        public String title;
        public String value;
        public DataModel(String title, String value){
            this.title = title;
            this.value = value;
        }
    }

    private class MyFuctionsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mDataSource == null) {
                return 0;
            }
            return mDataSource.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(OBDFrezenDataActivity.this).inflate(
                        R.layout.listitem_common, null);
            }
            TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            TextView tvValue = (TextView) convertView.findViewById(R.id.tv_right_text);
            tvTitle.setText(mDataSource.get(position).title);
            tvValue.setText(mDataSource.get(position).value);

            ImageView imgRight = (ImageView) convertView.findViewById(R.id.img_right_icon);
            imgRight.setVisibility(View.GONE);
            return convertView;
        }
    }
}
