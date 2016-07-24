package com.pxpd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pxpd.App.Common;
import com.pxpd.App.Config;
import com.pxpd.App.CustomPopWindowPlugin;
import com.pxpd.App.FileDownload;
import com.pxpd.http.YYHttpClient;

import org.json.JSONObject;

import cn.pda.scan.ScanThread;
import cn.pda.serialport.Util;

public class PDBeforeActivity extends Activity {
    private ImageView btnreturn;
    private ScanThread scanThread;
    private TextView title;
    private TextView t1,t2,t3,t4,t5,t6,t7,t8;
    private String twoDCLabelID,storeroomID,sAreaID,compactShelfID,
            colNum,aBSide,groupNum,caseNum;
    private String Position,TotalOf,OnShelf,Borrow,ArchivesDataPath;

    private Button btnstart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdbefore);
        btnreturn = (ImageView)findViewById(R.id.btn_return);
        btnreturn.setOnClickListener(onClickListenerreturn);
        title = (TextView)findViewById(R.id.title);
        title.setText("扫描二维码");
        btnstart = (Button)findViewById(R.id.btnstart);
        btnstart.setOnClickListener(onClickListenerbtnstart);
        openscan();


        t1 = (TextView)findViewById(R.id.t1);
        t2 = (TextView)findViewById(R.id.t2);
        t3 = (TextView)findViewById(R.id.t3);
        t4 = (TextView)findViewById(R.id.t4);
        t5 = (TextView)findViewById(R.id.t5);
        t6 = (TextView)findViewById(R.id.t6);
        t7 = (TextView)findViewById(R.id.t7);
        t8 = (TextView)findViewById(R.id.t8);


        t1.setText("");
        t2.setText("");
        t3.setText("");
        t4.setText("");
        t5.setText("");
        t6.setText("");
        t7.setText("");
        t8.setText("");


    }

    /**
     * 开始盘点
     */
    View.OnClickListener onClickListenerbtnstart =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CustomPopWindowPlugin.ShowPopWindow(btnstart,getLayoutInflater(),"获取信息");
            new Thread(runnable).start();
        }
    };

    /**
     * 登录线程
     */
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            GetArchivesToSqlite();
        }
    };


    private void GetArchivesToSqlite()
    {
        try {
            YYHttpClient yyHttpClient = new YYHttpClient();
            yyHttpClient.openRequest(Config.getSrvUrl("GetArchivesToSqlite"), YYHttpClient.REQ_METHOD_POST);
            yyHttpClient.setPostValuesForKey("clerkStationID", Config.ClerkStationID);
            yyHttpClient.setPostValuesForKey("clerkID", Config.ClerkID);
            yyHttpClient.setPostValuesForKey("twoDCLabelID", twoDCLabelID);
            yyHttpClient.setPostValuesForKey("storeroomID", storeroomID);
            yyHttpClient.setPostValuesForKey("sAreaID", sAreaID);
            yyHttpClient.setPostValuesForKey("compactShelfID", compactShelfID);
            yyHttpClient.setPostValuesForKey("colNum", colNum);
            yyHttpClient.setPostValuesForKey("aBSide", aBSide);
            yyHttpClient.setPostValuesForKey("groupNum", groupNum);
            yyHttpClient.setPostValuesForKey("caseNum", caseNum);
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
            Log.i("json返回:",result);



            JSONObject jsonObject=new JSONObject(result);
            if (jsonObject.getString("success").equals("true")) {
                JSONObject data= jsonObject.getJSONObject("data");
                Position =data.getString("Position");
                TotalOf =data.getString("TotalOf");
                OnShelf =data.getString("OnShelf");
                Borrow =data.getString("Borrow");
                ArchivesDataPath =data.getString("ArchivesDataPath");
                yyHttpClient.closeRequest();
                result  = String.format("http://%1$s:%2$s/%3$s",
                        Config.ServerIP,Config.ServerPort,ArchivesDataPath);


                FileDownload fileDownload=new FileDownload(result, new FileDownload.IFileDownload() {
                    @Override
                    public void OnFileDownloadEvent(int r) {
                        if (r==1)
                        {
                            handler.sendEmptyMessage(0);
                            return;
                        }
                        else
                            handler.sendEmptyMessage(1);
                    }
                });
                fileDownload.streamDownLoadFile("data.db");
            }
            else
                handler.sendEmptyMessage(0);
            yyHttpClient.closeRequest();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            handler.sendEmptyMessage(0);
        }

    }

    /**
     * ui线程操作
     */
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CustomPopWindowPlugin.CLosePopwindow();
            if (msg.what==0)
            {
                Toast.makeText(PDBeforeActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                return;
            }
            if (msg.what==1)
            {

                Intent intent = new Intent(PDBeforeActivity.this, online_pd.class);
                Bundle bundle=new Bundle();
                bundle.putString("Position",Position);
                bundle.putString("TotalOf",TotalOf);
                bundle.putString("OnShelf",OnShelf);
                bundle.putString("Borrow",Borrow);
                bundle.putString("twoDCLabelID", twoDCLabelID);
                bundle.putString("sAreaID",sAreaID);
                bundle.putString("storeroomID", storeroomID);
                bundle.putString("compactShelfID",compactShelfID);
                bundle.putString("colNum", colNum);
                intent.putExtras(bundle);
                startActivity(intent);


                finish();
                overridePendingTransition(R.anim.alpha, R.anim.alpha_exit);
                return;
            }

        }
    };





    /**
     * 二维码扫描
     */
    private void openscan()
    {
        try {
            scanThread = new ScanThread(mHandler);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "serialport init fail", Toast.LENGTH_SHORT)
                    .show();
            return ;

        }
        scanThread.start();
        //init sound
        Util.initSoundPool(this);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == ScanThread.SCAN) {
                String data = msg.getData().getString("data");

                Log.e("扫描到的数据",data);

                String[] qcode = data.split(";");
                if (qcode.length!= 8 )
                {
                    btnstart.setEnabled(false);
                    Toast.makeText(PDBeforeActivity.this, "扫描到不匹配的二维码", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] tag;
                //二维码编码
                tag = qcode[0].split("=");
                twoDCLabelID= tag[1];
                t1.setText("二维码： " + tag[1]);
                //库房编号
                tag = qcode[1].split("=");
                storeroomID= tag[1];
                t2.setText("库房编号： " + tag[1]);

                tag = qcode[2].split("=");
                sAreaID= tag[1];
                t3.setText("区编号： " + tag[1]);

                tag = qcode[3].split("=");
                compactShelfID= tag[1];
                t4.setText("密集架编号： " + tag[1]);

                tag = qcode[4].split("=");
                colNum= tag[1];
                t5.setText("列编号： " + tag[1]);

                tag = qcode[5].split("=");
                aBSide= tag[1];
                t6.setText("AB面编号： " + tag[1]);

                tag = qcode[6].split("=");
                groupNum= tag[1];
                t7.setText("组编号： " + tag[1]);

                tag = qcode[7].split("=");
                caseNum= tag[1];
                t8.setText("格编号： " + tag[1]);

                Util.play(1, 0);
                btnstart.setEnabled(true);
            }
        };
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        closescan();
        scanThread = null;
    }


    private void closescan() {
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread.close();
            scanThread = null;
        }
    }


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
        if (keyCode ==133|| keyCode==134) {
            scanThread.scan();
        }
        if (keyCode == 4)
        {
            finish();
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
        return super.onKeyDown(keyCode, event);
    }


}
