package com.pxpd;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.UhfReader;
import com.pxpd.App.DB;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import UHF.InventoryThread;
import cn.pda.scan.ScanThread;
import cn.pda.serialport.Util;

//201409000000000000020991
//E2001026750401840890BE8B
//E2001026750401850890BE8F


public class online_pd extends Activity {
    private ImageView btnreturn, btnscan;
    private TextView weizhi,kufang,mijijia,lie,
            yingyou,zaiku,jieyue,scaned;
    private TextView title;
    private ListView list;
    private DB db;
    private UhfReader reader ;
    private Boolean isstartrfid=false;
    private InventoryThread inventoryThread;
    private List<Map<String,String>> mapList;
    private Map<String,Integer>mapqueue;
    private MyAdpter myAdpter;


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

        mapList = new ArrayList<Map<String, String>>();
        mapqueue = new HashMap<String, Integer>();
        list = (ListView)findViewById(R.id.list);
        myAdpter = new MyAdpter(this);
        list.setAdapter(myAdpter);
        db=new DB(online_pd.this,getFilesDir()+ File.separator+"data.db");
        openuhf();
        refreshData();
    }


    private void refreshData()
    {
        Cursor cursor=db.getArchives();
        if (cursor==null)
        {
            Toast.makeText(this,"加载档案数据失败,请退出重新进入",Toast.LENGTH_SHORT).show();
            return ;
        }
        int i=0;
        while (cursor.moveToNext())
        {
            Map<String,String> map = new HashMap<String, String>();
            Log.i("RFIDLabelID",cursor.getString(1));
            map.put("ArchivesNum",cursor.getString(0));
            map.put("RFIDLabelID",cursor.getString(1));
            map.put("TwoDCLabelID",cursor.getString(2));
            map.put("FMaterial",cursor.getString(3));
            map.put("ArchivesRoomNum",cursor.getString(4));
            map.put("MiniatureNum",cursor.getString(5));
            map.put("FileTitle",cursor.getString(6));
            map.put("PreparationUnit",cursor.getString(7));
            map.put("PreparationTime",cursor.getString(8));
            map.put("DurationOfStorage",cursor.getString(9));
            map.put("SecurityLevel",cursor.getString(10));
            map.put("StoreroomID",cursor.getString(11));
            map.put("SAreaID",cursor.getString(12));
            map.put("CompactShelfID",cursor.getString(13));
            map.put("ColNum",cursor.getString(14));
            map.put("ABSide",cursor.getString(15));
            map.put("GroupNum",cursor.getString(16));
            map.put("ArchivesState",cursor.getString(18));
            map.put("Remark",cursor.getString(19));
            map.put("state","0");
            mapList.add(map);
            mapqueue.put(cursor.getString(1),i);
            i++;
        }
        myAdpter.notifyDataSetChanged();
    }



    /**
     * 打开UHF
     */
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
        inventoryThread = new InventoryThread(reader,handler);
        inventoryThread.start();

        Util.initSoundPool(this);


    }


    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mapqueue.containsKey(msg.obj.toString())) {
                int index = mapqueue.get(msg.obj);
                Map<String,String> map = mapList.get(index);
                map.remove("state");
                map.put("state","1");
                myAdpter.notifyDataSetChanged();
            }


        }
    };

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.createtask, menu);
//        return true;
//    }
//
//
//
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        closescan();
//        switch (id) {
//            case R.id.barcode:
//                if (scanmode==1)
//                    return true;
//                if (inventoryThread !=null)
//                {
//                    inventoryThread.CloseScan();
//                    inventoryThread.interrupt();
//                    inventoryThread=null;
//                }
//                UhfReader.getInstance().close();
//                if (scanThread !=null) {
//                    closescan();
//                    scanThread=null;
//                }
//                openscan();
//                Toast.makeText(online_pd.this,
//                        "条码", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.rfid:
//                if (scanmode==2)
//                    return true;
//                if (scanThread !=null) {
//                    closescan();
//                    scanThread=null;
//                }
//                UhfReader.getInstance().close();
//                if (inventoryThread !=null)
//                {
//                    inventoryThread.CloseScan();
//                    inventoryThread.interrupt();
//                    inventoryThread=null;
//                }
//                openuhf();
//                isstartrfid=false;
//                Toast.makeText(online_pd.this,
//                        "RFID", Toast.LENGTH_SHORT).show();
//                return true;
//
//        }
//
//
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == 133 || keyCode == 134) {
            if (isstartrfid) {
                isstartrfid =false;
                inventoryThread.PauseScan();
            }
            else {
                isstartrfid=true;
                inventoryThread.StartScan();
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
        //关闭uhf
        if (inventoryThread !=null)
        {
            inventoryThread.CloseScan();
            inventoryThread.interrupt();
            inventoryThread=null;
        }


    }



    private class MyAdpter extends BaseAdapter
    {

        TextView title,rfid,docid,state,pdstate;


        LayoutInflater layoutInflater;
        public MyAdpter(Context context)
        {
            layoutInflater=LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mapList.size();
        }

        @Override
        public Object getItem(int i) {
            return mapList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = layoutInflater.inflate(R.layout.list_pd,null);

            title=(TextView)view.findViewById(R.id.title);
            rfid=(TextView)view.findViewById(R.id.rfid);
            docid=(TextView)view.findViewById(R.id.docid);
            state=(TextView)view.findViewById(R.id.state);
            pdstate=(TextView)view.findViewById(R.id.pdstate);

            Map<String,String> map = mapList.get(i);
            title.setText(map.get("FileTitle"));
            rfid.setText("RIFD标签："+map.get("RFIDLabelID"));
            docid.setText("档案号：" + map.get("docid"));
            if (map.get("ArchivesState").equals("1"))
                state.setText("状态：在架");
            if (map.get("ArchivesState").equals("2"))
                state.setText("状态：出库");
            if (map.get("ArchivesState").equals("3"))
                state.setText("状态：借阅");
            if (map.get("ArchivesState").equals("4"))
                state.setText("状态：注销");

            if (map.get("state").equals("0"))
            {
                pdstate.setText("盘点状态：为扫描");
                pdstate.setTextColor(getResources().getColor(R.color.red1));
            }else
            {
                pdstate.setText("盘点状态：已扫描");
                pdstate.setTextColor(getResources().getColor(R.color.green1));
            }

            return view;
        }
    }

}
