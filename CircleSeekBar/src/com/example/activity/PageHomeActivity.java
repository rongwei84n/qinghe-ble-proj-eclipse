package com.example.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lee.circleseekbar.R;
import com.example.activity.status.OBDHomeActivity;
import com.example.ble.DeviceListAdapter;
import com.example.db.logic.DeviceLogic;
import com.example.jdy_touchuang.AV_Stick;
import com.example.jdy_touchuang.jdy_switch_Activity;
import com.example.jdy_touchuang.shengjiangji;
import com.example.jdy_type.Get_type;
import com.example.listener.JdyDeviceListener;
import com.example.main.DeviceScanActivity;
import com.example.model.BleDeviceModel;
import com.example.sensor.jdy_ibeacon_Activity;
import com.example.utils.ApplicationStaticValues;
import com.example.utils.BleCommandManager;
import com.example.utils.ListUtils;
import com.example.utils.LogUtils;
import com.example.utils.ToastUtil;

import java.util.List;
import java.util.Set;
import java.util.Timer;

public class PageHomeActivity extends JdyBaseActivity implements JdyDeviceListener{
    private static final int REQUEST_ADD_DEVICE = 100;

    private ListView mListView;
    private List<BleDeviceModel> mDeviceModels;
    private MyDeviceListAdapter mAdapter;
    private TextView mTvEmpty;
    private Handler mHandler = new Handler();
    
    private boolean mReceiveConnectResponse = false;
    
    
 // private LeDeviceListAdapter mLeDeviceListAdapter;
    Get_type mGet_type;
    private boolean mScanning;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 5000;

    private DeviceListAdapter mDevListAdapter;
    ToggleButton tb_on_off;
    TextView btn_searchDev;
    Button btn_aboutUs;
    ListView lv_bleList;
    byte dev_bid;
    Timer timer;
    String APP_VERTION = "1002";
    
    private boolean devicebinding = true;

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
            
            List<BleDeviceModel> devices = DeviceLogic.getAllBleDevices(PageHomeActivity.this);
        	boolean exists = false;
        	if (!ListUtils.isEmpty(devices)) {
				for (BleDeviceModel bleDeviceModel : devices) {
					if (bleDeviceModel.getDeviceName().equals(ApplicationStaticValues.deviceName) &&
							bleDeviceModel.getModuleID().contentEquals(ApplicationStaticValues.moduleId) &&
							bleDeviceModel.getDeviceAddress().equals(ApplicationStaticValues.deviceAddress)) {
						exists = true;
					}
				}
			}
        	if (!exists) {
        		BleDeviceModel bleDeviceModel = new BleDeviceModel();
                bleDeviceModel.setDeviceName(ApplicationStaticValues.deviceName);
                bleDeviceModel.setDeviceAddress(ApplicationStaticValues.deviceAddress);
                bleDeviceModel.setModuleID(ApplicationStaticValues.moduleId);
                bleDeviceModel.setStatus(0);
                bleDeviceModel.setCreateTime(System.currentTimeMillis()+"");
                DeviceLogic.addDevice(PageHomeActivity.this, bleDeviceModel);
			}
            
            
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
				builder.setMessage("确定要删除" + mDeviceModels.get(position).getDeviceName() 
						+ "_" + mDeviceModels.get(position).getModuleID() + "吗?");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DeviceLogic.removeDevice(PageHomeActivity.this, mDeviceModels.get(position).getDeviceName(), 
								mDeviceModels.get(position).getDeviceAddress(), 
								mDeviceModels.get(position).getModuleID());
						mDeviceModels.remove(position);
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
        
        mIvMenu.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				LogUtils.d(TAG, "长按跳转到淘宝的原来页面");
                Intent intent = new Intent(PageHomeActivity.this, DeviceScanActivity.class);
                startActivity(intent);
				return true;
			}
		});
        
        
        //绑定淘宝的代码列表
        lv_bleList = (ListView) findViewById(R.id.lv_bleList);
        mDevListAdapter = new DeviceListAdapter(mBluetoothAdapter, PageHomeActivity.this );
        mDevListAdapter.mJdyDeviceListener = this;
        dev_bid = (byte)0x88;//88是JDY厂家VID码
        mDevListAdapter.set_vid(dev_bid);//用于识别自家的VID相同的设备，只有模块的VID与APP的VID相同才会被搜索得到
        lv_bleList.setAdapter(mDevListAdapter.init_adapter());

        //mGet_type = new Get_type();

		/*
		// ��������Ƿ�������toggleButton״̬
		if (mBluetoothAdapter.isEnabled()) {
			tb_on_off.setChecked(true);
		} else {
			tb_on_off.setChecked(false);
		}*/

        lv_bleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            	LogUtils.d(TAG, "onItemClick position: " + position);
                if (mDevListAdapter.get_count() > 0)
                {
                    Byte vid_byte =  mDevListAdapter.get_vid( position );//返回136表示是JDY厂家模块
                    //String vid_str =String.format("%02x", vid_byte );
                    //Toast.makeText( DeviceScanActivity.this,"设备VID:"+vid_str, Toast.LENGTH_SHORT).show();
//				    Toast.makeText( DeviceScanActivity.this, "type:"+mDevListAdapter.get_item_type(position), Toast.LENGTH_SHORT).show();

                    if( vid_byte == dev_bid )//JDY厂家VID为0X88， 用户的APP不想搜索到其它厂家的JDY-08模块的话，可以设备一下 APP的VID，此时模块也需要设置，
                        //模块的VID与厂家APP的VID要一样，APP才可以搜索得到模块VID与APP一样的设备
                        switch(mDevListAdapter.get_item_type(position))
                        {
                            case JDY:////为标准透传模块
                            {
                            	LogUtils.d(TAG, "点击开始绑定JDY设备");
                                BluetoothDevice device1 = mDevListAdapter.get_item_dev(position);
                                if (device1 == null) {
                                    return;
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(PageHomeActivity.this);
                                builder.setCancelable(true);
                                View dialogView = LayoutInflater.from(PageHomeActivity.this).inflate(R.layout.dialog_edittext, null);
                                final EditText etdInput = (EditText) dialogView.findViewById(R.id.edt_input);
                                builder.setView(dialogView);
                                builder.setTitle("请填写设备上的设备号");
                                final String deviceName = device1.getName();
                                final String deviceAddress = device1.getAddress();
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String str = etdInput.getText().toString();
                                        if (TextUtils.isEmpty(str)){
                                            ToastUtil.show(PageHomeActivity.this, "设备号不能为空");
                                            return;
                                        }
                                        ApplicationStaticValues.deviceName = deviceName;
                                        ApplicationStaticValues.deviceAddress = deviceAddress;
                                        ApplicationStaticValues.moduleId = str;
                                        
                                        LogUtils.d(TAG, "确定添加 deviceName： " + deviceName + " deviceAddress: " + deviceAddress + " moduleId: " + str);
                                        bindBleService();
                                    }
                                });
                                builder.setNegativeButton("取消", null);
                                builder.show();

                                //连接蓝牙
//                                connectBle();
//                                Intent intent1 = new Intent(AddDeviceActivity.this, jdy_Activity.class);
//                                Intent intent1 = new Intent(AddDeviceActivity.this, OBDHomeActivity.class);
//                                intent1.putExtra(OBDHomeActivity.EXTRAS_DEVICE_NAME, device1.getName());
//                                intent1.putExtra(OBDHomeActivity.EXTRAS_DEVICE_ADDRESS, device1.getAddress());
//                                // if (mScanning)
//                                {
//                                    mDevListAdapter.scan_jdy_ble( false );;
//                                    mScanning = false;
//                                }
//                                startActivity(intent1);
                                break;
                            }
                            case JDY_iBeacon:////为iBeacon设备
                            {
                            	LogUtils.d(TAG, "点击开始绑定JDY_iBeacon设备");
                                BluetoothDevice device1 = mDevListAdapter.get_item_dev(position);
                                if (device1 == null) return;
                                Intent intent1 = new Intent(PageHomeActivity.this, jdy_ibeacon_Activity.class);;
                                intent1.putExtra( jdy_ibeacon_Activity.EXTRAS_DEVICE_NAME, device1.getName());
                                intent1.putExtra( jdy_ibeacon_Activity.EXTRAS_DEVICE_ADDRESS, device1.getAddress());

                                intent1.putExtra( jdy_ibeacon_Activity.EXTRAS_DEVICE_UUID, mDevListAdapter.get_iBeacon_uuid( position ));
                                intent1.putExtra( jdy_ibeacon_Activity.EXTRAS_DEVICE_MAJOR, mDevListAdapter.get_ibeacon_major( position ));
                                intent1.putExtra( jdy_ibeacon_Activity.EXTRAS_DEVICE_MINOR, mDevListAdapter.get_ibeacon_minor( position ));

                                // if (mScanning)
                                {
                                    mDevListAdapter.scan_jdy_ble( false );;
                                    mScanning = false;
                                }
                                startActivity(intent1);
                                break;
                            }
                            case sensor_temp://温度传感器
                            {
                            	LogUtils.d(TAG, "点击开始绑定温度传感器设备");
                                break;
                            }
                            case JDY_KG://开关控制APP
                            {
                            	LogUtils.d(TAG, "点击开始绑定开关控制JDY_KG设备");
                                BluetoothDevice device1 = mDevListAdapter.get_item_dev(position);
                                if (device1 == null) return;
                                Intent intent1 = new Intent(PageHomeActivity.this, jdy_switch_Activity.class);;
                                intent1.putExtra(jdy_switch_Activity.EXTRAS_DEVICE_NAME, device1.getName());
                                intent1.putExtra(jdy_switch_Activity.EXTRAS_DEVICE_ADDRESS, device1.getAddress());
                                // if (mScanning)
                                {
                                    mDevListAdapter.scan_jdy_ble( false );;
                                    mScanning = false;
                                }
                                startActivity(intent1);
                                break;
                            }
                            case JDY_KG1://开关控制APP
                            {
                            	LogUtils.d(TAG, "点击开始绑定JDY_KG1设备");
                                BluetoothDevice device1 = mDevListAdapter.get_item_dev(position);
                                if (device1 == null) return;
                                Intent intent1 = new Intent(PageHomeActivity.this, shengjiangji.class);;
                                intent1.putExtra(jdy_switch_Activity.EXTRAS_DEVICE_NAME, device1.getName());
                                intent1.putExtra(jdy_switch_Activity.EXTRAS_DEVICE_ADDRESS, device1.getAddress());
                                // if (mScanning)
                                {
                                    mDevListAdapter.scan_jdy_ble( false );;
                                    mScanning = false;
                                }
                                startActivity(intent1);
                                break;
                            }
                            case JDY_AMQ://massager 按摩器APP
                            {
                            	LogUtils.d(TAG, "点击开始绑定按摩器APP设备");
                                BluetoothDevice device1 = mDevListAdapter.get_item_dev(position);
                                if (device1 == null) return;
                                Intent intent1 = new Intent(PageHomeActivity.this, AV_Stick.class);
                                intent1.putExtra( AV_Stick.EXTRAS_DEVICE_NAME, device1.getName() );
                                intent1.putExtra( AV_Stick.EXTRAS_DEVICE_ADDRESS, device1.getAddress() );
                                // if (mScanning)
                                {
                                    mDevListAdapter.scan_jdy_ble( false );;
                                    mScanning = false;
                                }
                                startActivity(intent1);
                                break;
                            }
                            case JDY_LED1:// LED灯 APP 测试版本
                            {/*
							 BluetoothDevice device1 = mDevListAdapter.get_item_dev(position);
						        if (device1 == null) return;
						        Intent intent1 = new Intent(DeviceScanActivity.this,MainActivity.class);
						        intent1.putExtra( MainActivity.EXTRAS_DEVICE_NAME, device1.getName() );
						        intent1.putExtra( MainActivity.EXTRAS_DEVICE_ADDRESS, device1.getAddress() );
						       // if (mScanning)
						        {
						        	mDevListAdapter.scan_jdy_ble( false );;
						            mScanning = false;
						        }
						        startActivity(intent1);*/
                                break;
                            }
                            case JDY_LED2:// LED灯 APP 正试版本
                            {

                                break;
                            }


                            default:
                                break;
                        }


					/*
					BluetoothDevice device = mDevListAdapter.getItem(position);
					Intent intent = new Intent(DeviceScanActivity.this,
							DeviceControlActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("BLEDevName", device.getName());
					bundle.putString("BLEDevAddress", device.getAddress());
					intent.putExtras(bundle);
					DeviceScanActivity.this.startActivity(intent);
					*/


                }
            }
        });

        Message message = new Message();
        message.what = 100;
        handler.sendMessage(message);
        
        mIvMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickIvMenu();
			}
		});
    }

    @Override
    protected void onResume() {
        super.onResume();
//        scanLeDevice(true);
        LogUtils.d(TAG, "onResume");
        mReceiveConnectResponse = false;
        mDeviceModels = DeviceLogic.getAllBleDevices(this);
        mAdapter.notifyDataSetChanged();
        updateEmptyView();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	mDevListAdapter.mJdyDeviceListener = null;
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
        LogUtils.d(TAG, "点击添加蓝牙设备按钮");
        AlertDialog.Builder builder = new AlertDialog.Builder(PageHomeActivity.this);
        builder.setCancelable(true);
        View dialogView = LayoutInflater.from(PageHomeActivity.this).inflate(R.layout.dialog_edittext, null);
        final EditText etdInput = (EditText) dialogView.findViewById(R.id.edt_input);
        builder.setView(dialogView);
        builder.setTitle("请填写设备上的设备号");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = etdInput.getText().toString();
                if (TextUtils.isEmpty(str)){
                    ToastUtil.show(PageHomeActivity.this, "设备号不能为空");
                    return;
                }
                LogUtils.d(TAG, " 准备添加设备moduleId: " + str + " cacheAddress: " + 
                		ApplicationStaticValues.deviceAddress);
                Set<BluetoothDevice> bds = mBluetoothAdapter.getBondedDevices();
                
                ApplicationStaticValues.moduleId = str;
                devicebinding = false;
                showLoading();
                scanLeDevice(true);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
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
    
    
    
    
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 100)
            {
            }


            super.handleMessage(msg);
        }

        private void setTitle(String hdf) {

        };
    };

    public static boolean turnOnBluetooth()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null)
        {
            return bluetoothAdapter.enable();
        }
        return false;
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mDevListAdapter.scan_jdy_ble( false );
                    //invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mDevListAdapter.scan_jdy_ble( true );
        } else {
            mScanning = false;
            mDevListAdapter.scan_jdy_ble( false );
        }
    }

//    @Override
//    protected void onResume() {//打开APP时扫描设备
//        super.onResume();
//        scanLeDevice(true);
//        LogUtils.d(TAG, "onResume");
//        //mDevListAdapter.scan_jdy_ble( false );
//    }

    @Override
    protected void onPause() {//停止扫描
        super.onPause();
        LogUtils.d(TAG, "onPause");
        //scanLeDevice(false);
        mDevListAdapter.scan_jdy_ble( false );
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

	@Override
	public void onDeviceFound(String deviceName, String deviceAddress) {
		LogUtils.d(TAG, "devicebinding: " + devicebinding + " deviceName: " 
					+ deviceName + " deviceAddress: " + deviceAddress);
		hideLoading();
		if (devicebinding) {
			return;
		}
		devicebinding = true;
		ApplicationStaticValues.deviceName = deviceName;
		ApplicationStaticValues.deviceAddress = deviceAddress;
      
		bindBleService();
	}
}
