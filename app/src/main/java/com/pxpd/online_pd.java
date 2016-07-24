package com.pxpd;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.UhfReader;
import com.pxpd.App.DB;

import java.io.File;

import UHF.InventoryThread;
import cn.pda.scan.ScanThread;
import cn.pda.serialport.Util;


public class online_pd extends Activity {
    private ImageView btnreturn, btnscan;

    private ScanThread scanThread;
    private TextView weizhi,kufang,mijijia,lie,
            yingyou,zaiku,jieyue,scaned;
    private TextView title;
    private ListView list;
    private DB db;
    private UhfReader reader ;
    private Boolean isstartrfid=false;
    private int scanmode=0;
    private InventoryThread inventoryThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(0x80000000);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_pd);
        title = (TextView)findViewById(R.id.title);
        btnreturn = (ImageView) findViewById(R.id.btn_return);
        btnreturn.setOnClickListener(onClickListenerreturn);
        btnscan = (ImageView) findViewById(R.id.btn_scan);
        btnscan.setOnClickListener(onClickListenerscan);
        weizhi = (TextView) findViewById(R.id.weizhi);
        kufang = (TextView) findViewById(R.id.kufang);
        mijijia = (TextView) findViewById(R.id.mijijia);
        lie = (TextView) findViewById(R.id.lie);
        yingyou = (TextView) findViewById(R.id.yingyou);
        zaiku = (TextView) findViewById(R.id.zaiku);
        jieyue = (TextView) findViewById(R.id.jieyue);
        scaned = (TextView) findViewById(R.id.scaned);

        Bundle bundle=getIntent().getExtras();
        title.setText(bundle.getString("Position"));
        weizhi.setText(bundle.getString("sAreaID"));
        kufang.setText(bundle.getString("storeroomID"));
        mijijia.setText(bundle.getString("compactShelfID"));
        lie.setText(bundle.getString("colNum"));
        yingyou.setText(bundle.getString("TotalOf"));
        zaiku.setText(bundle.getString("OnShelf"));
        jieyue.setText(bundle.getString("Borrow"));

        list = (ListView)findViewById(R.id.list);

        db=new DB(online_pd.this,getFilesDir()+ File.separator+"data.db");

    }

    private void  openuhf()
    {
        reader = UhfReader.getInstance();
        if(reader == null){
            Toast.makeText(this,"打开RFID失败",Toast.LENGTH_SHORT).show();
            return ;
        }
        try {
            Thread.sleep(300);
            reader.setOutputPower(26);
        }
        catch (Exception e)
        {e.printStackTrace();}
        Util.initSoundPool(this);
        inventoryThread = new InventoryThread();
        inventoryThread.start();
        scanmode=2;


    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == ScanThread.SCAN) {
                String data = msg.getData().getString("data");
                Toast.makeText(online_pd.this, data, Toast.LENGTH_SHORT).show();
                Log.e("扫描到的数据", data);

                Util.play(1, 0);
            }
        }

        ;
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
            overridePendingTransition(R.anim.alpha, R.anim.alpha_exit);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.createtask, menu);
        return true;
    }


    private void openscan() {
        try {
            scanThread = new ScanThread(mHandler);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "serialport init fail", Toast.LENGTH_SHORT)
                    .show();
            return;

        }
        scanThread.start();
        //init sound
        Util.initSoundPool(this);
        scanmode=1;
    }

    /**
     * 关闭扫描
     */
    private void closescan() {
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
        switch (id) {
            case R.id.barcode:
                if (scanmode==1)
                    return true;
                if (inventoryThread !=null)
                {
                    inventoryThread.CloseScan();
                    inventoryThread.interrupt();
                    inventoryThread=null;
                }
                UhfReader.getInstance().close();
                if (scanThread !=null) {
                    closescan();
                    scanThread=null;
                }
                openscan();
                Toast.makeText(online_pd.this,
                        "条码", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.rfid:
                if (scanmode==2)
                    return true;
                if (scanThread !=null) {
                    closescan();
                    scanThread=null;
                }
                UhfReader.getInstance().close();
                if (inventoryThread !=null)
                {
                    inventoryThread.CloseScan();
                    inventoryThread.interrupt();
                    inventoryThread=null;
                }
                openuhf();
                isstartrfid=false;
                Toast.makeText(online_pd.this,
                        "RFID", Toast.LENGTH_SHORT).show();
                return true;

        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == 133 || keyCode == 134) {
            if (scanmode==1)
                scanThread.scan();
            if (scanmode==2)
            {
                if (isstartrfid) {
                    isstartrfid =false;
                    inventoryThread.PauseScan();
                }
                else {
                    isstartrfid=true;
                    inventoryThread.StartScan();
                }

            }
        }
        if (keyCode==4)
        {

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (inventoryThread !=null)
        {
            inventoryThread.CloseScan();
            inventoryThread.interrupt();
            inventoryThread=null;
        }

        if (scanThread!=null)
        {
            closescan();
            scanThread=null;

        }
    }
}
