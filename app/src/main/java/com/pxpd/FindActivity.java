package com.pxpd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.UhfReader;
import com.pxpd.App.App;
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
import cn.pda.serialport.Util;

public class FindActivity extends Activity {
    private ImageView btnreturn;

    private TextView title;
    private Button btn_saixuan, btn_find;
    private String StoreroomID = "", SAreaID = "", CompactShelfID = "",
            ColNum = "";
    private ImageView btn_right;
    private int menumode = 1;
    private SearchView searchView;
    private DB db;
    private ListView listView;
    private List<Map<String, String>> mapList;
    private Map<String, Integer> mapqueue;
    private MyAdpter myAdpter;
    private UhfReader reader;
    private InventoryThread inventoryThread;
    private Boolean isstartrfid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        db = new DB(App.getAPP(), App.getAPP().getFilesDir() + File.separator + "basedb.db");
        title = (TextView) findViewById(R.id.title);
        title.setText("定向查找");
        btnreturn = (ImageView) findViewById(R.id.btn_return);
        btnreturn.setOnClickListener(onClickListenerreturn);
        btn_saixuan = (Button) findViewById(R.id.btn_saixuan);
        btn_saixuan.setOnClickListener(onClickListenerbtn_saixuan);
        btn_find = (Button) findViewById(R.id.btn_find);
        btn_find.setOnClickListener(onClickListenerfind);
        btn_right = (ImageView) findViewById(R.id.btn_right);
        btn_right.setVisibility(View.VISIBLE);
        btn_right.setBackground(getResources().getDrawable(R.drawable.btn_find_menu_select));
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOptionsMenu();

            }
        });
        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.onActionViewExpanded();
        searchView.setQueryHint("档案号");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                onClickListenerfind.onClick(btn_find);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        Cursor cursor;
        cursor = db.getStoreroomManager();
        if (cursor == null) {
            Toast.makeText(App.getAPP(), "读取基础数据错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        while (cursor.moveToNext()) {
            StoreroomID = String.valueOf(cursor.getInt(0));
            break;
        }
        cursor.close();

        listView = (ListView)findViewById(R.id.list);
        openuhf();
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
        inventoryThread = new InventoryThread(reader, handlerscan);
        inventoryThread.start();

        Util.initSoundPool(this);


    }

    Handler handlerscan = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mapqueue.containsKey(msg.obj.toString())) {
                int index = mapqueue.get(msg.obj);
                Map<String, String> map = mapList.get(index);
                if (!map.get("state").equals("1")) {
                    map.remove("state");
                    map.put("state", "1");

                    myAdpter.notifyDataSetChanged();

                }
            }
        }
    };


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

        return super.onKeyDown(keyCode, event);
    }


    /**
     * 点击查找
     */
    View.OnClickListener onClickListenerfind = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (searchView.getQuery().toString().equals("")) {
                Toast.makeText(App.getAPP(), "请输入搜索关键字", Toast.LENGTH_SHORT).show();
                return;
            }
            CustomPopWindowPlugin.ShowPopWindow(btn_find,getLayoutInflater(),"正在查找");
            new Thread(findtask).start();

        }
    };


    Runnable findtask = new Runnable() {
        @Override
        public void run() {
            try {


                String findname = (menumode == 1) ? "GetArchivesByArchivesNumToSqlite" : "GetArchivesByFileTitleToSqlite";
                YYHttpClient yyHttpClient = new YYHttpClient();
                yyHttpClient.openRequest(Config.getSrvUrl(findname), YYHttpClient.REQ_METHOD_POST);
                if (menumode == 1)
                    yyHttpClient.setPostValuesForKey("archivesNumKeyword", searchView.getQuery().toString());
                else
                    yyHttpClient.setPostValuesForKey("fileTitleKeyword", searchView.getQuery().toString());
                yyHttpClient.setPostValuesForKey("storeroomID", StoreroomID);
                yyHttpClient.setPostValuesForKey("sAreaID", SAreaID);
                yyHttpClient.setPostValuesForKey("compactShelfID", CompactShelfID);
                yyHttpClient.setPostValuesForKey("colNum", ColNum);

                yyHttpClient.setEntity(yyHttpClient.getPostData());
                Boolean r = yyHttpClient.sendRequest();
                if (!r) {
                    handler.sendEmptyMessage(0);
                    yyHttpClient.closeRequest();
                    return;
                }
                byte[] buffer = yyHttpClient.getRespBodyData();
                if (buffer == null) {
                    handler.sendEmptyMessage(0);
                    yyHttpClient.closeRequest();
                    return;
                }

                String result = new String(buffer, "utf-8");
                result = Common.getjsonForXML(result);
                Log.i("json返回:", result);


                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("success").equals("true")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    result = String.format("http://%1$s:%2$s/%3$s",
                            Config.ServerIP, Config.ServerPort, data.getString("ArchivesDataPath"));


                    FileDownload fileDownload = new FileDownload(result, new FileDownload.IFileDownload() {
                        @Override
                        public void OnFileDownloadEvent(int r) {
                            if (r == 1) {
                                handler.sendEmptyMessage(0);
                                return;
                            } else
                                handler.sendEmptyMessage(1);
                        }
                    });
                    fileDownload.streamDownLoadFile("finddb.db");
                } else
                    handler.sendEmptyMessage(0);
                yyHttpClient.closeRequest();
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(0);
            }
        }
    };
    /**
     * ui线程操作
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CustomPopWindowPlugin.CLosePopwindow();
            if (msg.what == 0) {
                Toast.makeText(FindActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                return;
            }
            if (msg.what == 1) {

                db = new DB(App.getAPP(), App.getAPP().getFilesDir() + File.separator + "finddb.db");
                mapList=new ArrayList<Map<String, String>>();
                mapqueue = new HashMap<String, Integer>();
                myAdpter = new MyAdpter(FindActivity.this);

                loadlist();

                return;
            }

        }
    };


    private void  loadlist()
    {

        Cursor cursor = db.getArchivesMange();
        if (cursor == null || cursor.getCount()==0)
        {
            Toast.makeText(FindActivity.this, "没有查找到数据", Toast.LENGTH_SHORT).show();
            return;
        }
        mapqueue.clear();
        mapList.clear();

        int i=0;
        while (cursor.moveToNext())
        {
            Map<String ,String >map=new HashMap<String, String>();
            map.put("ArchivesNum",cursor.getString(0));
            map.put("RFIDLabelID",cursor.getString(1));
            map.put("FileTitle",cursor.getString(6));
            map.put("location",db.getCompactShelfName(cursor.getString(13)));
            map.put("state","0");
            mapList.add(map);
            mapqueue.put(cursor.getString(1),i);

        }
        listView.setAdapter(myAdpter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.createtask, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.archivesNumKeyword:
                menumode = 1;
                searchView.setQueryHint("档案号");


                break;
            case R.id.fileTitleKeyword:
                menumode = 2;
                searchView.setQueryHint("案卷名称");
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * 点击筛选
     */
    View.OnClickListener onClickListenerbtn_saixuan = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(FindActivity.this, Find_Query.class);
            startActivityForResult(intent, 0);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            StoreroomID = String.valueOf(data.getIntExtra("_StoreroomID", 0));
            SAreaID = data.getStringExtra("_SAreaID");
            if (SAreaID.equals(""))
                SAreaID = "0";
            CompactShelfID = data.getStringExtra("_CompactShelfID");
            if (CompactShelfID.equals(""))
                CompactShelfID = "0";
            ColNum = data.getStringExtra("_ColNum");
            if (ColNum.equals(""))
                ColNum = "0";
        }

    }

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


    private class MyAdpter extends BaseAdapter {

        TextView title, rfid, docid,location, state;


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
            location = (TextView) view.findViewById(R.id.location);


            Map<String, String> map = mapList.get(i);
            title.setText(map.get("FileTitle"));
            rfid.setText("RIFD标签：" + map.get("RFIDLabelID"));
            docid.setText("档案号：" + map.get("docid"));



            if (map.get("state").equals("0")) {
                state.setText("扫描状态：未扫描");
                state.setTextColor(getResources().getColor(R.color.red));
            } else {
                state.setText("扫描状态：已扫描");
                state.setTextColor(getResources().getColor(R.color.green1));
            }

            return view;
        }


    }



}
