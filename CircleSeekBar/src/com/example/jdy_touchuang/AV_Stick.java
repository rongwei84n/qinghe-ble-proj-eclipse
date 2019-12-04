
/**
 * 
 */

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jdy_touchuang;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ble.BluetoothLeService;
import com.example.utils.LogUtils;
import com.lee.circleseekbar.R;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class AV_Stick extends Activity implements SeekBar.OnSeekBarChangeListener
{
    private final static String TAG = jdy_Activity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    
    
    private String mDeviceName;
    private String mDeviceAddress;

    private BluetoothLeService mBluetoothLeService;

    private boolean mConnected = false;
  
    
    
    boolean connect_status_bit=false;
    


    private Handler mHandler;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000;
    

    
 
    
    
    
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                LogUtils.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //mConnected = true;
                
                
                connect_status_bit=true;
               
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                
                updateConnectionState(R.string.disconnected);
                connect_status_bit=false;
                show_view(false);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } 
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) //����FFE1����͸������ͨ������
            {
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            	//byte data1;
            	//intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);//  .getByteExtra(BluetoothLeService.EXTRA_DATA, data1);
                displayData( intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA) );
            } 
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE1.equals(action)) //����FFE2�������÷��ص�����
            {
                displayData1( intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA1) );
            }
            //Log.d("", msg)
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                	
//                	Log.i("tag", "uu");
//                    if (mGattCharacteristics != null) {
//                        final BluetoothGattCharacteristic characteristic =
//                                mGattCharacteristics.get(groupPosition).get(childPosition);
//                        final int charaProp = characteristic.getProperties();
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//                            // If there is an active notification on a characteristic, clear
//                            // it first so it doesn't update the data field on the user interface.
//                            if (mNotifyCharacteristic != null) {
//                                mBluetoothLeService.setCharacteristicNotification(
//                                        mNotifyCharacteristic, false);
//                                mNotifyCharacteristic = null;
//                            }
//                            mBluetoothLeService.readCharacteristic(characteristic);
//                        }
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//                            mNotifyCharacteristic = characteristic;
//                            mBluetoothLeService.setCharacteristicNotification(
//                                    characteristic, true);
//                        }
//                        return true;
//                    }
                    return false;
                }
    };

    private void clearUI() {
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        //mDataField.setText(R.string.no_data);
    }

 
    
    
    
    Timer timer = new Timer();  
    
 
   
   
    
    void show_view( boolean p )
    {
//    	if(p){
//    	
//    		key1.setEnabled(true);
//    		key2.setEnabled(true);
//    		key3.setEnabled(true);
//    		key4.setEnabled(true);
//    	
//    	}else{
//    	
//    		key1.setEnabled(false);
//    		key2.setEnabled(false);
//    		key3.setEnabled(false);
//    		key4.setEnabled(false);
//    		
//    	}
    }
    
    public void delay(int ms){
		try {
            Thread.currentThread();
			Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
	 }	
    

    private int[] color_arry; 

    

    
//    TextView switch_status_txt;
    
    ImageButton av_stick_button1,av_stick_button2,av_stick_button3;
    ImageButton av_stick_button4;
    TextView av_stick_textView1,mConnectionState;
    SeekBar av_stick_seekBar1;
    
    
    int md1=255;
    int md2=90;
    int md3=120;
    
    int select_bit = 0;
    int power_off=0;
    
   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.av_stick_view );
        
        setTitle("��Ħ��");
        
        color_arry=new int[]{R.color.gray,R.color.bule,R.color.green,R.color.yellow}; 
        int myColor=getResources().getColor(color_arry[0]);  
        
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        //setTitle( mDeviceName );
        
        
//        // Sets up UI references.
       // ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
//        //mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
//       // mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
//       // mDataField = (TextView) findViewById(R.id.data_value);

        //switch_status_txt = (TextView) findViewById(R.id.switch_status_txt);
        
        
        av_stick_button1 = (ImageButton)findViewById(R.id.av_stick_button1);
        av_stick_button2 = (ImageButton)findViewById(R.id.av_stick_button2);
        av_stick_button3 = (ImageButton)findViewById(R.id.av_stick_button3);
        
        myColor=getResources().getColor(color_arry[0]); 
        //av_stick_button1.setTextColor( myColor );
        
        myColor=getResources().getColor(color_arry[0]); 
        //av_stick_button2.setTextColor( myColor );
        
        myColor=getResources().getColor(color_arry[0]); 
        //av_stick_button3.setTextColor( myColor );
        
        
        av_stick_button4 = (ImageButton)findViewById(R.id.av_stick_button41);
        
        av_stick_textView1 = (TextView)findViewById(R.id.av_stick_textView1);
        av_stick_seekBar1 = (SeekBar)findViewById(R.id.av_stick_seekBar1);
        av_stick_seekBar1.setOnSeekBarChangeListener(this);
        av_stick_seekBar1.setMax(255);

        
        
        av_stick_button1.setOnClickListener( listener );//���ü���  
        av_stick_button2.setOnClickListener( listener );//���ü���  
        av_stick_button3.setOnClickListener( listener );//���ü���  
        av_stick_button4.setOnClickListener( listener );//���ü���  
        
     

        
        
        mHandler = new Handler();
        
        
        
        
        

        
        
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
        	
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            LogUtils.d(TAG, "Connect request result=" + result);
        }
        
        
        boolean sg;
        getActionBar().setTitle(mDeviceName+"  ��Ħ��");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        sg = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        //getActionBar().setTitle( "="+BluetoothLeService );
        //mDataField.setText("="+sg );
        updateConnectionState(R.string.connecting);
        
        
         //timer.schedule(task, 1000, 1000); // 1s��ִ��task,����1s�ٴ�ִ��  
        Message message = new Message();  
        message.what = 1;  
        handler.sendMessage(message);  
        
        show_view( true );
         
        get_pass();  
    }
    public void enable_pass()
    {
		 mBluetoothLeService.Delay_ms( 100 ); 
		 mBluetoothLeService.set_APP_PASSWORD( password_value );
    }
    String password_value = "123456";
    public void get_pass()
    {
        password_value = getSharedPreference( "DEV_PASSWORD_LEY_1000" );
        if( password_value!=null||password_value!="")
        {
        	if( password_value.length()==6 )
        	{
        		
        	}else password_value = "123456" ;
        }else password_value = "123456" ;
        
    }
	//---------------------------------------------------------------------------------Ӧ���ڴ洢ѡ��TAB���б�index
	public String getSharedPreference(String key) 
	{
		//ͬ�����ڶ�ȡSharedPreferences����ǰҪʵ������һ��SharedPreferences���� 
		SharedPreferences sharedPreferences= getSharedPreferences("test", 
		Activity.MODE_PRIVATE); 
		// ʹ��getString�������value��ע���2��������value��Ĭ��ֵ 
		String name =sharedPreferences.getString(key, ""); 
		return name;
	}
	public void setSharedPreference(String key, String values) 
	{
		//ʵ����SharedPreferences���󣨵�һ���� 
		SharedPreferences mySharedPreferences= getSharedPreferences("test", 
		Activity.MODE_PRIVATE);
		//ʵ����SharedPreferences.Editor���󣨵ڶ����� 
		SharedPreferences.Editor editor = mySharedPreferences.edit(); 
		//��putString�ķ����������� 
		editor.putString(key, values ); 
		//�ύ��ǰ���� 
		editor.commit(); 
		//ʹ��toast��Ϣ��ʾ����ʾ�ɹ�д������ 
		//Toast.makeText(this, values , 
		//Toast.LENGTH_LONG).show(); 
	}
    Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
        	if (msg.what == 1) 
        	{  
                //tvShow.setText(Integer.toString(i++));  
            	//scanLeDevice(true);
            	if (mBluetoothLeService != null) {
                	if( mConnected==false )
                	{
                		//updateConnectionState(R.string.connecting);
                		//final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                		//Log.d(TAG, "Connect request result=" + result);
                	}
                }
				
            }  
        	if (msg.what == 2) 
        	{
				
        	}
            super.handleMessage(msg);  
        };  
    };  
    TimerTask task = new TimerTask() {  
    	  
        @Override  
        public void run() {  
            // ��Ҫ������:������Ϣ  
            Message message = new Message();  
            message.what = 1;  
            handler.sendMessage(message);  
        }  
    }; 

    
    
    Button.OnClickListener listener = new Button.OnClickListener(){//������������    
        public void onClick(View v){    
            //String strTmp="���Button02";    
            //Ev1.setText(strTmp);   
        	switch( v.getId())
        	{
        	case R.id.av_stick_button1://��ܰģʽ
        	{
        		if( power_off==1&&mConnected==true )
        		{
	        		select_bit=1;
	        		av_stick_button1.setImageDrawable(getResources().getDrawable(R.drawable.green_round));
	        		av_stick_button2.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
	        		av_stick_button3.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
	        		
	        		av_stick_seekBar1.setProgress( md1 );
	        		av_stick_textView1.setText("ǿ�ȣ�"+md1);
	        		mBluetoothLeService.set_AV_OPEN( 1 );//��ܰģʽ
        		}else Toast.makeText(AV_Stick.this, "��򿪵�Դ", Toast.LENGTH_SHORT).show();
        		break;
        	}
        	case R.id.av_stick_button2://����ģʽ
        	{
        		if( power_off==1&&mConnected==true )
        		{
	        		select_bit=2;
	        		av_stick_button1.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
	        		av_stick_button2.setImageDrawable(getResources().getDrawable(R.drawable.green_round));
	        		av_stick_button3.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
	        		
	        		av_stick_seekBar1.setProgress( md2 );
	        		av_stick_textView1.setText("ǿ�ȣ�"+md2);
	        		
	        		mBluetoothLeService.set_AV_OPEN( 2 );//����ģʽ
        		}else Toast.makeText(AV_Stick.this, "��򿪵�Դ", Toast.LENGTH_SHORT).show();
        		break;
        	}
        	case R.id.av_stick_button3://����ģʽ
        	{
        		if( power_off==1&&mConnected==true )
        		{
	        		select_bit=3;
	        		av_stick_button1.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
	        		av_stick_button2.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
	        		av_stick_button3.setImageDrawable(getResources().getDrawable(R.drawable.green_round));
	        		
	        		av_stick_seekBar1.setProgress( md3 );
	        		av_stick_textView1.setText("ǿ�ȣ�"+md3);
	        		
	        		mBluetoothLeService.set_AV_OPEN( 3 );//����ģʽ
        		}else Toast.makeText(AV_Stick.this, "��򿪵�Դ", Toast.LENGTH_SHORT).show();
        		break;
        	}
        	case R.id.av_stick_button41://��Դ����
        	{
        		if( power_off==0&&mConnected==true )
        		{
        			av_stick_button4.setImageDrawable(getResources().getDrawable(R.drawable.power_off));
        			power_off = 1;
        			
            		select_bit=1;
            		av_stick_button1.setImageDrawable(getResources().getDrawable(R.drawable.green_round));
            		av_stick_button2.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
            		av_stick_button3.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
            		
            		mBluetoothLeService.set_AV_OPEN( 4 );//��ܰģʽ
        		}
        		else if( power_off==1 )//�رյ�Դ
        		{
        			av_stick_button4.setImageDrawable(getResources().getDrawable(R.drawable.power_off1));
        			power_off=0;
        			
            		select_bit=0;
            		av_stick_button1.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
            		av_stick_button2.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
            		av_stick_button3.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
            		
            		mBluetoothLeService.set_AV_OPEN( 0 );//�رյ�Դ
        		}
        		break;
        	}

        		default :
        			break;
        	}
        }    
  
    };  
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
//        mBluetoothLeService.disconnect();
//        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLeService.disconnect();
        //mBluetoothLeService.set_mem_data( mDeviceAddress, switch_pass_value.getText().toString() );
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        timer.cancel();
        timer=null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    } 
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }
String da="";
int len_g = 0;
    private void displayData( byte[] data1 ) //����FFE1����͸������ͨ������
    {


    	
    }
    private void displayData1( byte[] data1 ) //����FFE2�������÷��ص�����
    {
    	//String str = mBluetoothLeService.bytesToHexString1( data1 );//�����յ�ʮ����������ת����ʮ�������ַ���
    	//Toast.makeText(jdy_switch_Activity.this, "rx:"+str, Toast.LENGTH_SHORT).show();; 
    	
    	if( data1.length==5&&data1[0]==(byte) 0xf6 )//�ж��Ƿ��Ƕ�ȡIO״̬λ
    	{
    		
			
    		
    	}
    	if( data1.length==2&&data1[0]==(byte) 0x61 )//�����豸���ɹ�
    	{
 
    	}
    	else if( data1.length==7&&data1[0]==(byte) 0x52 )//��ȡ
    	{
    		//dev_password = mBluetoothLeService.byte_to_String( data1,1 );
    		//Toast.makeText(jdy_ibeacon_Activity.this, "function_rx:"+current_dev_password, Toast.LENGTH_SHORT).show(); 
    	}
    }
    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
       
    	
    	if (gattServices == null) return;

        if( gattServices.size()>0&&mBluetoothLeService.get_connected_status( gattServices )==2 )//��ʾΪJDY-06��JDY-08ϵ������ģ��
        {
	        if( connect_status_bit )
			  {
	        	mConnected = true;
	        	show_view( true );

				 mBluetoothLeService.enable_JDY_ble( 1 );
				 mBluetoothLeService.Delay_ms( 100 ); 
				 mBluetoothLeService.get_IO_ALL();
				 enable_pass();
				 
				 
				 LogUtils.d( "out_2","connected" );
				 
				 updateConnectionState(R.string.connected);
			  }else{
				  //Toast.makeText(this, "Deleted Successfully!", Toast.LENGTH_LONG).show(); 
				  Toast toast = Toast.makeText(AV_Stick.this, "�豸û�����ӣ�", Toast.LENGTH_SHORT); 
				  toast.show(); 
			  }
        }
        else if( gattServices.size()>0&&mBluetoothLeService.get_connected_status( gattServices )==1 )//��ʾΪJDY-09��JDY-10ϵ������ģ��
        {
	        if( connect_status_bit )
			  {
	        	mConnected = true;
	        	show_view( true );
				
				 mBluetoothLeService.enable_JDY_ble( 1 );
				 mBluetoothLeService.Delay_ms( 100 ); 
				 mBluetoothLeService.get_IO_ALL();
				 enable_pass();
				 
				 
				 updateConnectionState(R.string.connected);
			  }else{
				  //Toast.makeText(this, "Deleted Successfully!", Toast.LENGTH_LONG).show(); 
				  Toast toast = Toast.makeText(AV_Stick.this, "�豸û�����ӣ�", Toast.LENGTH_SHORT); 
				  toast.show(); 
			  }
        }else
        {
        	 Toast toast = Toast.makeText(AV_Stick.this, "��ʾ�����豸��ΪJDYϵ��BLEģ��", Toast.LENGTH_SHORT); 
			  toast.show(); 
        }
//        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
//                this,
//                gattServiceData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[] {LIST_NAME, LIST_UUID},
//                new int[] { android.R.id.text1, android.R.id.text2 },
//                gattCharacteristicData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[] {LIST_NAME, LIST_UUID},
//                new int[] { android.R.id.text1, android.R.id.text2 }
//        );
//        
//        mGattServicesList.setAdapter(gattServiceAdapter);
        
    }
 
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE1);
        return intentFilter;
    }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO �Զ����ɵķ������
		if( power_off==1&&mConnected==true )
		{
			mBluetoothLeService.set_AV_PULSE( progress );
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO �Զ����ɵķ������
		if( mConnected==true )
		{
			if( select_bit==1 )
			{
				md1 = seekBar.getProgress(); 
				av_stick_textView1.setText("ǿ�ȣ�"+md1 );
			}
			else if( select_bit==2 )
			{
				md2 = seekBar.getProgress();
				av_stick_textView1.setText("ǿ�ȣ�"+md2 );
			}
			else if( select_bit==3 )
			{
				md2 = seekBar.getProgress();
				av_stick_textView1.setText("ǿ�ȣ�"+md3 );
			}
		}
	}


}
