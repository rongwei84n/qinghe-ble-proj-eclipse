package com.example.set;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.circleseekbar.R;



public class set extends Activity
{
  String password_value = "123456";
  EditText pss_value_txt ;
  
  TextView textView11;
  String resultStr = "";
  
  private String path = "http://szony.blog.163.com/blog/static/24529305020151244823112/";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.set );
        
        pss_value_txt = (EditText)findViewById(R.id.set_pass_value);
//        textView11 = (TextView)findViewById(R.id.textView11);
        

        
        
       // textView11.setText("3432423423"); 
        
        
        
        
        setTitle("����");
        
        ActionBar actionBar = getActionBar();  
        actionBar.setDisplayHomeAsUpEnabled(true);  
        
        password_value = getSharedPreference( "DEV_PASSWORD_LEY_1000" );
        if( password_value!=null||password_value!="")
        {
        	if( password_value.length()==6 )
        	{
        		pss_value_txt.setText( password_value );
        	}
        	else password_value = "123456" ;
        }else pss_value_txt.setText( "123456" );
        
        
//        Message message = new Message();  
//        message.what = 1;  
//        handler.sendMessage(message);  
        
//		JDYHtmlService df = new JDYHtmlService();
//		String hdf = df.h5_url();
//		textView11.setText ( hdf );;
        
    }
    
	
	
    Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
        	if (msg.what == 1) 
        	{  
                
//                try {  
//                    String htmlContent = JDYHtmlService.getHtml(path);
//                    textView11.setText(htmlContent);  
//                } catch (Exception e) {     
//                	textView11.setText("��������쳣��"+e.toString());
//                }
        		
        		
                
                
            }  
        	
        	
            super.handleMessage(msg);  
        };  
    };  
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
    
    
	@Override
	protected void onResume() {//��APPʱɨ���豸
		super.onResume();

	}

	@Override
	protected void onPause() {//ֹͣɨ��
		super.onPause();

	}
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.set_menu, menu);
    	menu.findItem(R.id.set_menu).setVisible(true);
        return true;
    } 
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        	case R.id.set_menu:
        	{
        		String ts = pss_value_txt.getText().toString();
        		if( ts==null||ts==""){Toast.makeText(set.this, "��ʾ�����벻��Ϊ��", Toast.LENGTH_SHORT).show(); break;}
        		
        		int len = ts.length();
        		if(len!=6){Toast.makeText(set.this, "��ʾ���������Ϊ6λ����", Toast.LENGTH_SHORT).show(); break;}
        		Toast.makeText(set.this, "��ʾ�����뱣��ɹ�", Toast.LENGTH_SHORT).show(); 
        		
        		setSharedPreference( "DEV_PASSWORD_LEY_1000",ts );
        	}
            break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    
    
    
}
