package com.pxpd;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import cn.pda.scan.ScanThread;
import cn.pda.serialport.Util;


public class online_pd extends Activity {
    private ImageView btnreturn,btnscan;

    private ScanThread scanThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(0x80000000);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_pd);
        btnreturn = (ImageView)findViewById(R.id.btn_return);
        btnreturn.setOnClickListener(onClickListenerreturn);
        btnscan = (ImageView)findViewById(R.id.btn_scan);
        btnscan.setOnClickListener(onClickListenerscan);
//        openscan();
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == ScanThread.SCAN) {
                String data = msg.getData().getString("data");
				Toast.makeText(online_pd.this, data, Toast.LENGTH_SHORT).show();


                Util.play(1, 0);
            }
        };
    };



    /**
     * 点击扫描模式
     */
    View.OnClickListener onClickListenerscan = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openOptionsMenu();
        }
    };



    /**
     * 点击返回按钮
     */
    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.createtask, menu);
        return true;
    }


    private void openscan()
    {
        try {
            scanThread = new ScanThread(mHandler);
        } catch (Exception e) {
            // �����쳣
            Toast.makeText(getApplicationContext(), "serialport init fail", 0)
                    .show();
            return ;
            // e.printStackTrace();
        }
        scanThread.start();
        //init sound
        Util.initSoundPool(this);
    }

    /**
     * 关闭扫描
     */
    private void closescan()
    {
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread.close();
            scanThread = null;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        closescan();
        switch (id)
        {
            case R.id.barcode:
                    Toast.makeText(online_pd.this,
                            "条码",Toast.LENGTH_SHORT).show();
                openscan();
                return true;
            case R.id.rfid:
                Toast.makeText(online_pd.this,
                        "RFID",Toast.LENGTH_SHORT).show();
                return true;

        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode ==133|| keyCode==134) {
            scanThread.scan();
        }
        return super.onKeyDown(keyCode, event);
    }



}
