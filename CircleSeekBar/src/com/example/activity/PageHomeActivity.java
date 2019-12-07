package com.example.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lee.circleseekbar.R;
import com.example.activity.status.OBDHomeActivity;
import com.example.db.logic.DeviceLogic;
import com.example.main.DeviceScanActivity;
import com.example.model.BleDeviceModel;
import com.example.utils.ApplicationStaticValues;
import com.example.utils.BleCommandManager;
import com.example.utils.ListUtils;
import com.example.utils.LogUtils;
import com.example.utils.ToastUtil;

import java.util.List;

public class PageHomeActivity extends JdyBaseActivity {
    private static final int REQUEST_ADD_DEVICE = 100;

    private ListView mListView;
    private List<BleDeviceModel> mDeviceModels;
    private MyDeviceListAdapter mAdapter;
    private TextView mTvEmpty;
    private Handler mHandler = new Handler();
    
    private boolean mReceiveConnectResponse = false;

    @Override
    protected void onBleConnectSuccess() {
        super.onBleConnectSuccess();
        ToastUtil.show(this, "蓝牙连接成功，正在匹配设备，请稍等...");
        mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				sendMessage(BleCommandManager.Sender.composeDeviceNumCommand(ApplicationStaticValues.moduleId));
			}
		}, 3000);
    }

    @Override
    protected void onMessageReceive(String msg) {
    	super.onMessageReceive(msg);
        if (TextUtils.isEmpty(msg)){
            return;
        }
        if (msg.contains("OK")){
        	//模块号匹配成功连接成功
            LogUtils.d(TAG, "模块号匹配成功: " + ApplicationStaticValues.moduleId
                    + "deviceAddress: " + ApplicationStaticValues.deviceAddress
                    + "deviceName: " + ApplicationStaticValues.deviceName);
            if (!mReceiveConnectResponse) {
            	mReceiveConnectResponse = true;
                ToastUtil.show(PageHomeActivity.this, "连接成功");
                Intent intent = new Intent(PageHomeActivity.this, OBDHomeActivity.class);
                startActivity(intent);
			}
        }else {
        	//这个界面只有连接蓝牙操作，所以这个ERR肯定是点击连接蓝牙传回来的。
            LogUtils.d(TAG, "模块号匹配失败: " + ApplicationStaticValues.moduleId
                    + "deviceAddress: " + ApplicationStaticValues.deviceAddress
                    + "deviceName: " + ApplicationStaticValues.deviceName);
            ToastUtil.show(PageHomeActivity.this, "模块号匹配失败,请重新添加蓝牙");
        }
    }

    @Override
    public void beforeInitLayout() {
        super.beforeInitLayout();
        initPermission();
    }

    private void initPermission() {
//        List<String> permissionList = new ArrayList<String>();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//
//        if (!permissionList.isEmpty()) {
//            LogUtils.i(TAG,"请求授权列表:"+permissionList.toString());
//            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
//        } else {
//            LogUtils.i(TAG,"全部授权成功");
//        }
    }

    @Override
    public void initLayout(Bundle savedInstanceState) {
        LogUtils.d(TAG, "initLayout");
        setContentView(R.layout.activity_page_home);
    }

    @Override
    public void afterInitView() {
        super.afterInitView();
        
        mTvEmpty = (TextView) findViewById(R.id.tv_empty);
        mDeviceModels = DeviceLogic.getAllBleDevices(this);
        mListView = (ListView) findViewById(R.id.lv_devices_list);
        mAdapter = new MyDeviceListAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //连接蓝牙
                ApplicationStaticValues.deviceName = mDeviceModels.get(position).getDeviceName();
                ApplicationStaticValues.deviceAddress = mDeviceModels.get(position).getDeviceAddress();
                ApplicationStaticValues.moduleId = mDeviceModels.get(position).getModuleID();
                bindBleService();
            }
        });
        
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(PageHomeActivity.this);
				builder.setTitle("确认");
				builder.setMessage("是否要删除" + mDeviceModels.get(position).getDeviceName() 
						+ "_" + mDeviceModels.get(position).getModuleID() + "吗?");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDeviceModels.remove(position);
						DeviceLogic.removeDevice(PageHomeActivity.this, mDeviceModels.get(position).getDeviceName(), 
								mDeviceModels.get(position).getDeviceAddress(), 
								mDeviceModels.get(position).getModuleID());
						mAdapter.notifyDataSetChanged();
					}
				});
				builder.setNegativeButton("取消", null);
				builder.show();
				return true;
			}
		});

        hideBack();
        mTvTitle.setText("蓝牙列表");
        showIvMenu(R.drawable.add);

        Button btnGoTaobao = (Button) findViewById(R.id.btn_go_taobao);
        btnGoTaobao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d(TAG, "跳转到淘宝的原来页面");
                Intent intent = new Intent(PageHomeActivity.this, DeviceScanActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiveConnectResponse = false;
        updateEmptyView();
        mDeviceModels = DeviceLogic.getAllBleDevices(this);
        mAdapter.notifyDataSetChanged();
        LogUtils.d(TAG, "onResume");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_ADD_DEVICE:
                if (resultCode == RESULT_OK){
                    LogUtils.d(TAG, "request code->add device ok");
                    mDeviceModels = DeviceLogic.getAllBleDevices(this);
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    protected void onClickIvMenu() {
    	super.onClickIvMenu();
        LogUtils.d(TAG, "点击跳转到AddDeviceActivity");
        Intent intent = new Intent(PageHomeActivity.this, AddDeviceActivity.class);
        startActivityForResult(intent, REQUEST_ADD_DEVICE);
    }

    private void updateEmptyView(){
        if (ListUtils.isEmpty(mDeviceModels)){
            mListView.setVisibility(View.GONE);
            mTvEmpty.setVisibility(View.VISIBLE);
        }else {
            mListView.setVisibility(View.VISIBLE);
            mTvEmpty.setVisibility(View.GONE);
        }
    }

    private class MyDeviceListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (mDeviceModels == null){
                LogUtils.d(TAG, "device getCount = 0");
                return 0;
            }
            LogUtils.d(TAG, "device getCount: " + mDeviceModels.size());
            return mDeviceModels.size();
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
                convertView = LayoutInflater.from(PageHomeActivity.this).inflate(R.layout.listitem_my_device,
                        null);
            }
            TextView tvName = (TextView)convertView.findViewById(R.id.tv_name);
            tvName.setText(mDeviceModels.get(position).getDeviceName() +"_" + mDeviceModels.get(position).getModuleID());

            return convertView;
        }
    }
}
