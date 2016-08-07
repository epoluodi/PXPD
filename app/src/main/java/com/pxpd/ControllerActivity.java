package com.pxpd;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pxpd.App.App;
import com.pxpd.App.Common;
import com.pxpd.App.Config;
import com.pxpd.App.CustomPopWindowPlugin;
import com.pxpd.App.DB;
import com.pxpd.http.YYHttpClient;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.pda.scan.ScanThread;

public class ControllerActivity extends Activity {

    private ImageView btnreturn;
    private TextView title;
    private Spinner sp1, sp2, sp3, sp4;
    private List<String> stringList1 = new ArrayList<String>();
    private ArrayAdapter<String> adapter1;
    private List<String> stringList2 = new ArrayList<String>();
    private ArrayAdapter<String> adapter2;
    private List<String> stringList3 = new ArrayList<String>();
    private ArrayAdapter<String> adapter3;
    private List<String> stringList4 = new ArrayList<String>();
    private ArrayAdapter<String> adapter4;
    private String StoreroomID, SAreaID, CompactShelfID,
            ColNum;
    private DB db;

    private Button btnopen,btnclose,btntfopen,btntfclose;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ontroller);
        btnreturn = (ImageView)findViewById(R.id.btn_return);
        btnreturn.setOnClickListener(onClickListenerreturn);
        title = (TextView)findViewById(R.id.title);
        title.setText("密集架控制");
        btnopen= (Button)findViewById(R.id.btnstart);
        btnclose= (Button)findViewById(R.id.btn_close);
        btntfopen= (Button)findViewById(R.id.btntf);
        btntfclose= (Button)findViewById(R.id.btnclosetf);
        btnopen.setOnClickListener(onClickListener);
        btnclose.setOnClickListener(onClickListener);
        btntfopen.setOnClickListener(onClickListener);
        btntfclose.setOnClickListener(onClickListener);

        db = new DB(App.getAPP(), App.getAPP().getFilesDir() + File.separator + "basedb.db");
        sp1 = (Spinner) findViewById(R.id.sp1);
        sp2 = (Spinner) findViewById(R.id.sp2);
        sp3 = (Spinner) findViewById(R.id.sp3);
        sp4 = (Spinner) findViewById(R.id.sp4);
        sp1.setOnItemSelectedListener(onItemSelectedListener1);
        sp2.setOnItemSelectedListener(onItemSelectedListener2);
        sp3.setOnItemSelectedListener(onItemSelectedListener3);
        sp4.setOnItemSelectedListener(onItemSelectedListener4);
        initSp();
    }


    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CustomPopWindowPlugin.ShowPopWindow(btnopen,getLayoutInflater(),"提交指令");
            switch (view.getId())
            {
                case R.id.btnstart:

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OpenShelfCol();
                        }
                    }).start();
                    break;
                case R.id.btn_close:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            CloseShelf();
                        }
                    }).start();
                    break;
                case R.id.btntf:

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ShelfBreeze();
                        }
                    }).start();

                    break;
                case R.id.btnclosetf:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ShelfBreeze();
                        }
                    }).start();
                    break;
            }

        }
    };

    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CustomPopWindowPlugin.CLosePopwindow();
            switch (msg.what)
            {
                case 0:
                    Toast.makeText(ControllerActivity.this,"控制失败",Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(ControllerActivity.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(ControllerActivity.this,"控制成功",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    /**
     * 开启
     */
    private void OpenShelfCol()
    {
        try {
            YYHttpClient yyHttpClient = new YYHttpClient();
            yyHttpClient.openRequest(Config.getSrvUrl("OpenShelfCol"), YYHttpClient.REQ_METHOD_POST);
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
            yyHttpClient.closeRequest();
            String result = new String(buffer, "utf-8");
            result = Common.getjsonForXML(result);
            Log.d("json返回:",result);
            JSONObject jsonObject=new JSONObject(result);

            if (jsonObject.getString("success").equals("true")) {
                handler.sendEmptyMessage(1);
            }
            else
                throw new Exception(jsonObject.getJSONObject("data").getString("runMsg"));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Message message=handler.obtainMessage();
            message.obj=e.getMessage();
            message.what=-1;
            handler.sendMessage(message);
        }

    }


    /**
     * 关闭
     */
    private void CloseShelf()
    {
        try {
            YYHttpClient yyHttpClient = new YYHttpClient();
            yyHttpClient.openRequest(Config.getSrvUrl("CloseShelf"), YYHttpClient.REQ_METHOD_POST);
            yyHttpClient.setPostValuesForKey("compactShelfID", CompactShelfID);
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
            yyHttpClient.closeRequest();
            String result = new String(buffer, "utf-8");
            result = Common.getjsonForXML(result);
            Log.d("json返回:",result);
            JSONObject jsonObject=new JSONObject(result);

            if (jsonObject.getString("success").equals("true")) {
                handler.sendEmptyMessage(1);
            }
            else
                throw new Exception(jsonObject.getJSONObject("data").getString("runMsg"));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Message message=handler.obtainMessage();
            message.obj=e.getMessage();
            message.what=-1;
            handler.sendMessage(message);
        }

    }


    /**
     * 打开通风
     */
    private void ShelfBreeze()
    {
        try {
            YYHttpClient yyHttpClient = new YYHttpClient();
            yyHttpClient.openRequest(Config.getSrvUrl("ShelfBreeze"), YYHttpClient.REQ_METHOD_POST);
            yyHttpClient.setPostValuesForKey("compactShelfID", CompactShelfID);
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
            yyHttpClient.closeRequest();
            String result = new String(buffer, "utf-8");
            result = Common.getjsonForXML(result);
            Log.d("json返回:",result);
            JSONObject jsonObject=new JSONObject(result);

            if (jsonObject.getString("success").equals("true")) {
                handler.sendEmptyMessage(1);
            }
            else
                throw new Exception(jsonObject.getJSONObject("data").getString("runMsg"));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Message message=handler.obtainMessage();
            message.obj=e.getMessage();
            message.what=-1;
            handler.sendMessage(message);
        }

    }





    /**
     * 初始化层级数据
     */
    private void initSp() {

        try {
            Cursor cursor;
            cursor = db.getStoreroomManager();
            if (cursor == null)
                throw new Exception();
            stringList1 = new ArrayList<String>();
            while (cursor.moveToNext()) {
                stringList1.add(cursor.getString(1));
            }
            cursor.close();
            adapter1 = new ArrayAdapter<String>(ControllerActivity.this, R.layout.type_list,
                    stringList1);
            sp1.setAdapter(adapter1);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();

        }

    }

    AdapterView.OnItemSelectedListener onItemSelectedListener1 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Log.i("name ", stringList1.get(i));
            StoreroomID="";
            SAreaID="";
            CompactShelfID="";
            ColNum="";


            StoreroomID = db.getStoreroomManagerid(stringList1.get(i));
            Cursor cursor;
            cursor = db.getAreaManager(StoreroomID);
            if (cursor == null) {
                Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();
                return;
            }
            stringList2 = new ArrayList<String>();
            while (cursor.moveToNext()) {
                stringList2.add(cursor.getString(1));
            }
            cursor.close();
            adapter2 = new ArrayAdapter<String>(ControllerActivity.this, R.layout.type_list,
                    stringList2);
            sp2.setAdapter(adapter2);


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


    AdapterView.OnItemSelectedListener onItemSelectedListener2 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            SAreaID="";
            CompactShelfID="";
            ColNum="";
            SAreaID = db.getAreaManagerid(StoreroomID, stringList2.get(i));
            Cursor cursor;
            cursor = db.getCompactShelfManage(StoreroomID, SAreaID);
            if (cursor == null) {
                Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();
                return;
            }
            stringList3 = new ArrayList<String>();
            while (cursor.moveToNext()) {
                stringList3.add(cursor.getString(1));
            }
            cursor.close();
            adapter3 = new ArrayAdapter<String>(ControllerActivity.this, R.layout.type_list,
                    stringList3);
            sp3.setAdapter(adapter3);


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


    AdapterView.OnItemSelectedListener onItemSelectedListener3 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            CompactShelfID="";
            ColNum="";
            CompactShelfID = db.getCompactShelfManageid(StoreroomID,
                    SAreaID, stringList3.get(i));
            Cursor cursor;
            cursor = db.getCompactShelfCol(CompactShelfID);
            if (cursor == null) {

                Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();
                return;
            }
            stringList4 = new ArrayList<String>();
            while (cursor.moveToNext()) {
                stringList4.add(cursor.getString(0));
            }
            cursor.close();
            adapter4 = new ArrayAdapter<String>(ControllerActivity.this, R.layout.type_list,
                    stringList4);
            sp4.setAdapter(adapter4);


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


    AdapterView.OnItemSelectedListener onItemSelectedListener4 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            ColNum="";
            ColNum = stringList4.get(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4)
        {
            finish();
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
        return super.onKeyDown(keyCode, event);
    }

}
