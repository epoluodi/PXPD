package com.pxpd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.UhfReader;
import com.pxpd.App.Common;
import com.pxpd.App.Config;
import com.pxpd.App.CustomPopWindowPlugin;
import com.pxpd.App.DB;
import com.pxpd.App.FileDownload;
import com.pxpd.http.YYHttpClient;

import org.json.JSONObject;

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
    private TextView weizhi, kufang, mijijia, lie,
            yingyou, zaiku, jieyue, scaned;
    private TextView title;
    private ListView list;
    private DB db;
    private UhfReader reader;
    private Boolean isstartrfid = false;
    private InventoryThread inventoryThread;
    private List<Map<String, String>> mapList;
    private Map<String, Integer> mapqueue;
    private MyAdpter myAdpter;
    private List<String> listchk;
    private List<Integer> listchk2;
    private Button btnxiugai,btntichu,btnshangjia;
    private String updatearchivesNums="";

    private int scancounts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(0x80000000);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_pd);
        title = (TextView) findViewById(R.id.title);
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

        btnxiugai = (Button) findViewById(R.id.btn_xiugai);
        btnxiugai.setOnClickListener(onClickListenerxiugai);
        btntichu = (Button) findViewById(R.id.btn_tichu);
        btntichu.setOnClickListener(onClickListenertichu);
        btnshangjia = (Button) findViewById(R.id.btn_shangjia);
        btnshangjia.setOnClickListener(onClickListenershangjia);

        Bundle bundle = getIntent().getExtras();
        title.setText(bundle.getString("Position"));
        weizhi.setText(bundle.getString("sAreaID"));
        kufang.setText(bundle.getString("storeroomID"));
        mijijia.setText(bundle.getString("compactShelfID"));
        lie.setText(bundle.getString("colNum"));
        yingyou.setText(bundle.getString("TotalOf"));
        zaiku.setText(bundle.getString("OnShelf"));
        jieyue.setText(bundle.getString("Borrow"));

        listchk = new ArrayList<String>();
        listchk2 =new ArrayList<Integer>();
        mapList = new ArrayList<Map<String, String>>();
        mapqueue = new HashMap<String, Integer>();
        list = (ListView) findViewById(R.id.list);
        myAdpter = new MyAdpter(this);
        list.setAdapter(myAdpter);
        db = new DB(online_pd.this, getFilesDir() + File.separator + "data.db");
        openuhf();
        refreshData();
    }


    /**
     * 修改状态
     */
    View.OnClickListener onClickListenerxiugai = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (listchk.size()==0)
                return;

            String archivesNums = "";
            for (String s : listchk) {
                archivesNums += s + ",";
            }
            archivesNums = archivesNums.substring(0, archivesNums.length() - 1);
            Log.i("选择中的记录", archivesNums);

            updatearchivesNums = archivesNums;
            AlertDialog.Builder builder = new AlertDialog.Builder(online_pd.this);
            builder.setTitle("修改状态");

            String m = String.format("选择了%1$s条记录，提交后系统自动更改为“在库”状态", listchk.size());
            builder.setMessage(m);
            builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //提交
                    dialogInterface.dismiss();
                    CustomPopWindowPlugin.ShowPopWindow(btnxiugai,getLayoutInflater()
                    ,"正在提交");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            updateStateTask();
                        }
                    }).start();
                }
            });
            builder.setNegativeButton("取消",null);
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        }
    };

    /**
     * 剔除状态
     */
    View.OnClickListener onClickListenertichu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (listchk.size()==0)
                return;

            AlertDialog.Builder builder = new AlertDialog.Builder(online_pd.this);
            builder.setTitle("剔除");
            String m = String.format("选择了%1$s条记录，确定剔除选中的记录吗", listchk.size());
            builder.setMessage(m);
            builder.setPositiveButton("剔除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //提交
                    dialogInterface.dismiss();

                    for (String s : listchk) {
                        db.deleteArchivesState(s);
                    }
                    refreshData();
                }
            });
            builder.setNegativeButton("取消",null);
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        }
    };

    /**
     * 上架
     */
    View.OnClickListener onClickListenershangjia = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (listchk.size()==0)
                return;

            AlertDialog.Builder builder = new AlertDialog.Builder(online_pd.this);
            builder.setTitle("档案上架");

            String m = String.format("选择了%1$s条记录，提交上架.", listchk.size());
            builder.setMessage(m);
            builder.setPositiveButton("上架", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //提交
                    dialogInterface.dismiss();
                    CustomPopWindowPlugin.ShowPopWindow(btnxiugai,getLayoutInflater()
                            ,"正在提交");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (String s : listchk) {

                            }

                        }
                    }).start();
                }
            });
            builder.setNegativeButton("取消",null);
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        }
    };



    private void refreshData() {
        Cursor cursor = db.getArchives();
        if (cursor == null) {
            Toast.makeText(this, "加载档案数据失败,请退出重新进入", Toast.LENGTH_SHORT).show();
            return;
        }
        int i = 0;
        listchk.clear();
        listchk2.clear();
        mapList.clear();
        mapqueue.clear();
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<String, String>();
            Log.i("RFIDLabelID", cursor.getString(1));
            map.put("ArchivesNum", cursor.getString(0));
            map.put("RFIDLabelID", cursor.getString(1));
            map.put("TwoDCLabelID", cursor.getString(2));
            map.put("FMaterial", cursor.getString(3));
            map.put("ArchivesRoomNum", cursor.getString(4));
            map.put("MiniatureNum", cursor.getString(5));
            map.put("FileTitle", cursor.getString(6));
            map.put("PreparationUnit", cursor.getString(7));
            map.put("PreparationTime", cursor.getString(8));
            map.put("DurationOfStorage", cursor.getString(9));
            map.put("SecurityLevel", cursor.getString(10));
            map.put("StoreroomID", cursor.getString(11));
            map.put("SAreaID", cursor.getString(12));
            map.put("CompactShelfID", cursor.getString(13));
            map.put("ColNum", cursor.getString(14));
            map.put("ABSide", cursor.getString(15));
            map.put("GroupNum", cursor.getString(16));
            map.put("CaseNum", cursor.getString(17));
            map.put("ArchivesState", cursor.getString(18));
            map.put("Remark", cursor.getString(19));
            map.put("state", "0");
            mapList.add(map);
            mapqueue.put(cursor.getString(1), i);
            i++;
        }
        myAdpter.notifyDataSetChanged();
    }


    /**
     * 打开UHF
     */
    private void openuhf() {
        reader = UhfReader.getInstance();
        if (reader == null) {
            Toast.makeText(this, "打开RFID失败", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Thread.sleep(300);
            reader.setOutputPower(26);
        } catch (Exception e) {
            e.printStackTrace();
        }
        inventoryThread = new InventoryThread(reader, handler);
        inventoryThread.start();

        Util.initSoundPool(this);


    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mapqueue.containsKey(msg.obj.toString())) {
                int index = mapqueue.get(msg.obj);
                Map<String, String> map = mapList.get(index);
                if (!map.get("state").equals("1")) {
                    map.remove("state");
                    map.put("state", "1");
                    scancounts++;
                    myAdpter.notifyDataSetChanged();
                    scaned.setText(String.valueOf(scancounts));
                }
            }
        }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == 133 || keyCode == 134) {
            if (isstartrfid) {
                isstartrfid = false;
                inventoryThread.PauseScan();
            } else {
                isstartrfid = true;
                inventoryThread.StartScan();
            }
        }
        if (keyCode == 4) {
            if (mapqueue.size()>0)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(online_pd.this);
                builder.setTitle("提示");
                builder.setMessage("当前正在盘点,确定要放弃本次盘点吗");
                builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();

                    }
                });
                builder.setNegativeButton("取消",null);
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭uhf
        if (inventoryThread != null) {
            inventoryThread.CloseScan();
            inventoryThread.interrupt();
            inventoryThread = null;
        }


    }


    /**
     * 更改状态请求
     */
    private void updateStateTask()
    {
        try {
            YYHttpClient yyHttpClient = new YYHttpClient();
            yyHttpClient.openRequest(Config.getSrvUrl("EditArchivesStateByOneState"), YYHttpClient.REQ_METHOD_POST);
            yyHttpClient.setPostValuesForKey("archivesNums", updatearchivesNums);
            yyHttpClient.setPostValuesForKey("archivesState", "1");
            yyHttpClient.setEntity(yyHttpClient.getPostData());
            Boolean r = yyHttpClient.sendRequest();
            if (!r) {
                httphandler.sendEmptyMessage(0);
                yyHttpClient.closeRequest();
                return;
            }
            byte[] buffer = yyHttpClient.getRespBodyData();
            if (buffer == null) {
                httphandler.sendEmptyMessage(0);
                yyHttpClient.closeRequest();
                return;
            }

            String result = new String(buffer, "utf-8");
            result = Common.getjsonForXML(result);
            Log.d("json返回:",result);
            JSONObject jsonObject=new JSONObject(result);

            if (jsonObject.getString("success").equals("true")) {

                for (String s:listchk)
                {
                    db.updateArchivesState(s,"1");
                }
                listchk.clear();
                listchk2.clear();
                httphandler.sendEmptyMessage(1);
            }
            else
                httphandler.sendEmptyMessage(-1);
            yyHttpClient.closeRequest();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            httphandler.sendEmptyMessage(-1);
        }

    }


    /**
     * 档案上架
     */
    private void fileShelvesTask(Map<String,String> map)
    {
        try {
            YYHttpClient yyHttpClient = new YYHttpClient();
            yyHttpClient.openRequest(Config.getSrvUrl("FileShelves"), YYHttpClient.REQ_METHOD_POST);
            yyHttpClient.setPostValuesForKey("archivesNum", map.get("ArchivesNum"));
            yyHttpClient.setPostValuesForKey("rFIDLabelID", map.get("RFIDLabelID"));
            yyHttpClient.setPostValuesForKey("twoDCLabelID", map.get("TwoDCLabelID"));
            yyHttpClient.setPostValuesForKey("fileTitle", map.get("FileTitle"));
            yyHttpClient.setPostValuesForKey("storeroomID", map.get("StoreroomID"));
            yyHttpClient.setPostValuesForKey("sAreaID", map.get("SAreaID"));
            yyHttpClient.setPostValuesForKey("compactShelfID", map.get("CompactShelfID"));
            yyHttpClient.setPostValuesForKey("colNum", map.get("ColNum"));
            yyHttpClient.setPostValuesForKey("aBSide", map.get("ABSide"));
            yyHttpClient.setPostValuesForKey("groupNum", map.get("GroupNum"));
            yyHttpClient.setPostValuesForKey("caseNum", map.get("CaseNum"));
            yyHttpClient.setPostValuesForKey("moreInfor", "");

            yyHttpClient.setEntity(yyHttpClient.getPostData());
            Boolean r = yyHttpClient.sendRequest();
            if (!r) {
                httphandler.sendEmptyMessage(0);
                yyHttpClient.closeRequest();
                return;
            }
            byte[] buffer = yyHttpClient.getRespBodyData();
            if (buffer == null) {
                httphandler.sendEmptyMessage(0);
                yyHttpClient.closeRequest();
                return;
            }

            String result = new String(buffer, "utf-8");
            result = Common.getjsonForXML(result);
            Log.d("json返回:",result);
            JSONObject jsonObject=new JSONObject(result);
            yyHttpClient.closeRequest();
            if (jsonObject.getString("success").equals("true")) {

                for (String s:listchk)
                {
                    db.updateArchivesState(s,"1");
                }
                listchk.clear();
                listchk2.clear();
                httphandler.sendEmptyMessage(1);

            }
            else {

                throw  new Exception("");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            httphandler.sendEmptyMessage(-1);
        }


    }




    Handler httphandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CustomPopWindowPlugin.CLosePopwindow();
            switch (msg.what)
            {
                case 0:
                    Toast.makeText(online_pd.this,"网络异常",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(online_pd.this,"修改成功",Toast.LENGTH_SHORT).show();
                    myAdpter.notifyDataSetChanged();
                    break;
                case -1:
                    Toast.makeText(online_pd.this,"修改状态失败，请重新尝试",Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    Toast.makeText(online_pd.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };





    private class MyAdpter extends BaseAdapter {

        TextView title, rfid, docid, state, pdstate;
        CheckBox chk;


        LayoutInflater layoutInflater;

        public MyAdpter(Context context) {
            layoutInflater = LayoutInflater.from(context);
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

            view = layoutInflater.inflate(R.layout.list_pd, null);

            title = (TextView) view.findViewById(R.id.title);
            rfid = (TextView) view.findViewById(R.id.rfid);
            docid = (TextView) view.findViewById(R.id.docid);
            state = (TextView) view.findViewById(R.id.state);
            pdstate = (TextView) view.findViewById(R.id.pdstate);
            chk = (CheckBox) view.findViewById(R.id.chk);
            chk.setTag(i);
            chk.setOnCheckedChangeListener(onCheckedChangeListener);


            Map<String, String> map = mapList.get(i);
            title.setText(map.get("FileTitle"));
            rfid.setText("RIFD标签：" + map.get("RFIDLabelID"));
            docid.setText("档案号：" + map.get("docid"));

            String ArchivesState = db.getArchivesState(map.get("ArchivesNum"));
            if (ArchivesState == null)
                state.setText("状态：未知");
            if (ArchivesState.equals("1"))
                state.setText("状态：在架");
            if (ArchivesState.equals("2"))
                state.setText("状态：出库");
            if (ArchivesState.equals("3"))
                state.setText("状态：借阅");
            if (ArchivesState.equals("4"))
                state.setText("状态：注销");

            if (map.get("state").equals("0")) {
                pdstate.setText("盘点状态：为扫描");
                pdstate.setTextColor(getResources().getColor(R.color.red1));
            } else {
                pdstate.setText("盘点状态：已扫描");
                pdstate.setTextColor(getResources().getColor(R.color.green1));
            }

            return view;
        }


        /**
         * 勾选
         */
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                int i = (Integer) compoundButton.getTag();
                Map<String, String> map = mapList.get(i);
                if (b) {

                    if (listchk.contains(map.get("ArchivesNum")))
                        return;
                    else {
                        listchk.add(map.get("ArchivesNum"));
                        listchk2.add(i);
                    }

                } else {
                    listchk.remove(map.get("ArchivesNum"));
                    listchk2.remove(i);
                }
            }
        };
    }

}
