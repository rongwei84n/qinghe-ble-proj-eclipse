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
import com.example.activity.JdyBaseActivity;
import com.example.utils.BleCommandManager;

import java.util.ArrayList;
import java.util.List;

/** 实时数据 */
public class OBDRealDataActivity extends JdyBaseActivity {
    private ListView mListView;
    private List<DataModel> mDataSource;
    private MyFuctionsAdapter mAdapter;
    
    @Override
    public void beforeInitLayout() {
		super.beforeInitLayout();
		
	}

    @Override
    public void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_obd_home);
    }

    @Override
    public void afterInitView() {
        initDataSource();
        mTvTitle.setText("实时数据");
        mListView = (ListView) findViewById(R.id.lv_functionlist);
        mAdapter = new MyFuctionsAdapter();
        mListView.setAdapter(mAdapter);
    }
    
    @Override
    protected void onDestroy() {
		super.onDestroy();
		sendMessage(BleCommandManager.Sender.COMMAND_FINISH);
	}

    private void initDataSource(){
        mDataSource = new ArrayList<DataModel>();
        mDataSource.add(new DataModel("车速", "0km/h"));
        mDataSource.add(new DataModel("点火时刻", "-10CA"));
        mDataSource.add(new DataModel("吸气温度", "-30"));
        mDataSource.add(new DataModel("车速", "0km/h"));
        mDataSource.add(new DataModel("点火时刻", "-10CA"));
        mDataSource.add(new DataModel("吸气温度", "-30"));
        mDataSource.add(new DataModel("车速", "0km/h"));
        mDataSource.add(new DataModel("点火时刻", "-10CA"));
        mDataSource.add(new DataModel("吸气温度", "-30"));
        mDataSource.add(new DataModel("车速", "0km/h"));
        mDataSource.add(new DataModel("点火时刻", "-10CA"));
        mDataSource.add(new DataModel("吸气温度", "-30"));
        mDataSource.add(new DataModel("车速", "0km/h"));
        mDataSource.add(new DataModel("点火时刻", "-10CA"));
        mDataSource.add(new DataModel("吸气温度", "-30"));
        mDataSource.add(new DataModel("车速", "0km/h"));
        mDataSource.add(new DataModel("点火时刻", "-10CA"));
        mDataSource.add(new DataModel("吸气温度", "-30"));
        mDataSource.add(new DataModel("车速", "0km/h"));
        mDataSource.add(new DataModel("点火时刻", "-10CA"));
        mDataSource.add(new DataModel("吸气温度", "-30"));
        mDataSource.add(new DataModel("车速", "0km/h"));
        mDataSource.add(new DataModel("点火时刻", "-10CA"));
        mDataSource.add(new DataModel("吸气温度", "-30"));
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
                convertView = LayoutInflater.from(OBDRealDataActivity.this).inflate(
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
