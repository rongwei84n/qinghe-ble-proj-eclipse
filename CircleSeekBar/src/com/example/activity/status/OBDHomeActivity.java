package com.example.activity.status;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lee.circleseekbar.R;
import com.example.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备功能进来的首页面
 */
public class OBDHomeActivity extends BaseActivity {
    private ListView mListView;
    private List<ItemModel> mDataSource;

    private static final int INDEX_READ_ERROR = 1;
    private static final int INDEX_CLEAR_ERROR = 2;
    private static final int INDEX_REAL_DATA = 3;
    private static final int INDEX_FREZE_DATA = 4;
    private static final int INDEX_CAR_INFO = 5;
    private static final int INDEX_YIBIAO = 6;


    @Override
    public void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_obd_home);
    }

    @Override
    public void afterInitView() {
        initData();
        mTvTitle.setText("OBD检测");

        mListView = (ListView) findViewById(R.id.lv_functionlist);
        MyFuctionsAdapter adapter = new MyFuctionsAdapter();
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemModel model = mDataSource.get(position);
                Intent intent = null;
                switch (model.index){
                    case INDEX_READ_ERROR:
                        intent = new Intent(OBDHomeActivity.this, OBDReadErrorActivity.class);
                        startActivity(intent);
                        break;
                    case INDEX_CLEAR_ERROR:
                        intent = new Intent(OBDHomeActivity.this, OBDClearErrorActivity.class);
                        startActivity(intent);
                        break;
                    case INDEX_REAL_DATA:
                        intent = new Intent(OBDHomeActivity.this, OBDRealDataActivity.class);
                        startActivity(intent);
                        break;
                    case INDEX_FREZE_DATA:
                        intent = new Intent(OBDHomeActivity.this, OBDFrezenDataActivity.class);
                        startActivity(intent);
                        break;
                    case INDEX_CAR_INFO:
                        intent = new Intent(OBDHomeActivity.this, OBDCarInfoActivity.class);
                        startActivity(intent);
                        break;
                    case INDEX_YIBIAO:
                        intent = new Intent(OBDHomeActivity.this, OBDYiBiaoActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void initData(){
        mDataSource = new ArrayList<ItemModel>();
        mDataSource.add(new ItemModel("读取故障码", INDEX_READ_ERROR));
        mDataSource.add(new ItemModel("清除故障码", INDEX_CLEAR_ERROR));
        mDataSource.add(new ItemModel("实时数据", INDEX_REAL_DATA));
        mDataSource.add(new ItemModel("冻结数据", INDEX_FREZE_DATA));
        mDataSource.add(new ItemModel("车辆信息", INDEX_CAR_INFO));
        mDataSource.add(new ItemModel("仪表", INDEX_YIBIAO));
    }

    private static class ItemModel{
        private String itemName;
        private int index;

        public ItemModel(String name, int index){
            this.itemName = name;
            this.index = index;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }
    }

    private class MyFuctionsAdapter extends BaseAdapter{

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
                convertView = LayoutInflater.from(OBDHomeActivity.this).inflate(
                        R.layout.listitem_common, null);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.tv_title);
            textView.setText(mDataSource.get(position).getItemName());
            TextView subTitle = (TextView) convertView.findViewById(R.id.tv_right_text);
            subTitle.setVisibility(View.GONE);
            ImageView imgRightIcon = (ImageView) convertView.findViewById(R.id.img_right_icon);
            imgRightIcon.setVisibility(View.GONE);
            return convertView;
        }
    }
}
