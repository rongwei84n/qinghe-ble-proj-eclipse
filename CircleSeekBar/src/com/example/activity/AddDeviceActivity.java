package com.example.activity;

import java.util.List;
import java.util.Timer;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.activity.status.OBDHomeActivity;
import com.example.ble.DeviceListAdapter;
import com.example.db.logic.DeviceLogic;
import com.example.jdy_touchuang.AV_Stick;
import com.example.jdy_touchuang.jdy_switch_Activity;
import com.example.jdy_touchuang.shengjiangji;
import com.example.jdy_type.Get_type;
import com.example.model.BleDeviceModel;
import com.example.sensor.jdy_ibeacon_Activity;
import com.example.set.set;
import com.lee.circleseekbar.R;
import com.example.utils.ApplicationStaticValues;
import com.example.utils.BleCommandManager;
import com.example.utils.ListUtils;
import com.example.utils.LogUtils;
import com.example.utils.ToastUtil;

public class AddDeviceActivity extends JdyBaseActivity implements View.OnClickListener {
    // private LeDeviceListAdapter mLeDeviceListAdapter;
    Get_type mGet_type;
    private boolean mScanning;
    private Handler mHandler;

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
        	LogUtils.d(TAG, "onMessageReceive 模块号匹配成功");
        	List<BleDeviceModel> devices = DeviceLogic.getAllBleDevices(AddDeviceActivity.this);
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
                DeviceLogic.addDevice(AddDeviceActivity.this, bleDeviceModel);
			}

            ToastUtil.show(AddDeviceActivity.this, "连接成功");
            Intent intent = new Intent(AddDeviceActivity.this, OBDHomeActivity.class);
            startActivity(intent);
            finish();
        }else {
        	//这个界面只有连接蓝牙操作，所以这个ERR肯定是点击连接蓝牙传回来的。
        	LogUtils.d(TAG, "onMessageReceive 模块号匹配失败");
            ToastUtil.show(AddDeviceActivity.this,
                    "模块号" + ApplicationStaticValues.moduleId + "匹配失败,请重新添加蓝牙");
        }
    }

    @Override
    public void initLayout(Bundle savedInstanceState) {
        LogUtils.d(TAG, "initLayout");
        setContentView(R.layout.activity_add_device_main);
    }

    @Override
    public void afterInitView() {
        super.afterInitView();
        mTvTitle.setText("蓝牙绑定");

        mHandler = new Handler();
        Button btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d(TAG, "点击搜索蓝牙按钮");
                scanLeDevice(true);
            }
        });

        lv_bleList = (ListView) findViewById(R.id.lv_bleList);


//		//tb_on_off = (ToggleButton) findViewById(R.id.tb_on_off);
//		btn_searchDev = (TextView) findViewById(R.id.btn_searchDev);
//		btn_aboutUs = (Button) findViewById(R.id.btn_aboutUs);
//
//		btn_aboutUs.setText("");
//		btn_aboutUs.setOnClickListener(this);
//		btn_searchDev.setOnClickListener(this);

        mDevListAdapter = new DeviceListAdapter(mBluetoothAdapter, AddDeviceActivity.this );
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddDeviceActivity.this);
                                builder.setCancelable(true);
                                View dialogView = LayoutInflater.from(AddDeviceActivity.this).inflate(R.layout.dialog_edittext, null);
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
                                            ToastUtil.show(AddDeviceActivity.this, "设备号不能为空");
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
                                Intent intent1 = new Intent(AddDeviceActivity.this, jdy_ibeacon_Activity.class);;
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
                                Intent intent1 = new Intent(AddDeviceActivity.this, jdy_switch_Activity.class);;
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
                                Intent intent1 = new Intent(AddDeviceActivity.this, shengjiangji.class);;
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
                                Intent intent1 = new Intent(AddDeviceActivity.this, AV_Stick.class);
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

    public void onClick(View v) {
        switch (v.getId()) {
            case 0:
                break;
//		case R.id.btn_searchDev:
//			//scanLeDevice(true);
//			break;

//		case R.id.btn_aboutUs:
//			 Intent intent = new Intent();
//		        intent.setAction("android.intent.action.VIEW");
//		        Uri content_url = Uri.parse("https://item.taobao.com/item.htm?spm=a1z10.1-c.w4004-11559702484.2.uKkX9H&id=44163359933");
//		        intent.setData(content_url);
//		        startActivity(intent);
//			break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);

        menu.findItem(R.id.scan_menu_set).setVisible(true);
        menu.findItem(R.id.scan_menu_id).setActionView(null);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_menu_set:
                //mDevListAdapter.clear();
                //mDevListAdapter.scan_jdy_ble( true );
                Intent intent1 = new Intent(AddDeviceActivity.this, set.class);;
                startActivity(intent1);
                break;
            case R.id.scan_menu_set1:
            {
                mDevListAdapter.clear();
                scanLeDevice( true );
            }
            break;
        }
        return true;
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

    @Override
    protected void onResume() {//打开APP时扫描设备
        super.onResume();
        scanLeDevice(true);
        LogUtils.d(TAG, "onResume");
        //mDevListAdapter.scan_jdy_ble( false );
    }

    @Override
    protected void onPause() {//停止扫描
        super.onPause();
        LogUtils.d(TAG, "onPause");
        //scanLeDevice(false);
        mDevListAdapter.scan_jdy_ble( false );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy");
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
}
