package com.pxpd;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pxpd.App.Common;
import com.pxpd.App.Config;
import com.pxpd.App.CustomPopWindowPlugin;
import com.pxpd.App.FileDownload;
import com.pxpd.http.YYHttpClient;

import org.json.JSONObject;

public class Login extends Activity {

    private Button btnlogin;
    private ImageView btnsetting, btnoffline;
    private TextView loginuser, loginpwd;
    private String username, userpwd;
    private int mode;
    private RadioButton inside, outside;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnlogin = (Button) findViewById(R.id.btn_login);
        btnlogin.setOnClickListener(onClickListenerlogin);
        btnsetting = (ImageView) findViewById(R.id.btnsetting);
        btnsetting.setOnClickListener(onClickListenersetting);
        btnoffline = (ImageView) findViewById(R.id.btnoffline);
        btnoffline.setOnClickListener(onClickListeneroffline);

        loginuser = (TextView) findViewById(R.id.loginuser);
        loginpwd = (TextView) findViewById(R.id.loginpwd);
        inside = (RadioButton) findViewById(R.id.inside);
        outside = (RadioButton) findViewById(R.id.outside);
        username = Config.getKeyShareVarForString(this, "username");
        userpwd = Config.getKeyShareVarForString(this, "userpwd");
        mode = Config.getKeyShareVarForint(this, "mode");
        if (!username.equals("null"))
            loginuser.setText(username);
        if (!userpwd.equals("null"))
            loginpwd.setText(userpwd);

        if (mode == 0)
            inside.setChecked(true);
        if (mode == 1)
            outside.setChecked(true);


    }

    /**
     * 点击离线
     */
    View.OnClickListener onClickListeneroffline = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            Intent intent = new Intent(Login.this, OfflineFileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha, R.anim.alpha_exit);
        }
    };


    /**
     * 点击登录
     */
    View.OnClickListener onClickListenerlogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            username = loginuser.getText().toString();
            userpwd = loginpwd.getText().toString();

            if (username.equals("") || userpwd.equals("")) {
                Toast.makeText(Login.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                return;
            }


            if(inside.isChecked()) {
                mode=0;
                Config.Mode = 0;
                Config.ServerIP = Config.getKeyShareVarForString(Login.this,"in_ip");
                Config.ServerPort = Config.getKeyShareVarForString(Login.this,"in_port");
            }
            if(outside.isChecked()) {
                mode=1;
                Config.Mode = 1;
                Config.ServerIP = Config.getKeyShareVarForString(Login.this,"out_ip");
                Config.ServerPort = Config.getKeyShareVarForString(Login.this,"out_port");
            }

            Config.setServerInfo();

            CustomPopWindowPlugin.ShowPopWindow(btnlogin,getLayoutInflater(),"正在登录");
            new Thread(runnable).start();


        }
    };


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
                Toast.makeText(Login.this, "登录失败", Toast.LENGTH_SHORT).show();
                return;
            }
            if (msg.what==1)
            {
                Config.RunMode=0;
                Config.setKeyShareVar(Login.this,"mode",mode);
                Config.setKeyShareVar(Login.this,"username",username);
                Config.setKeyShareVar(Login.this,"userpwd",userpwd);
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.alpha, R.anim.alpha_exit);
                return;
            }

        }
    };
    /**
     * 登录线程
     */
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            LoginTask();
        }
    };

    private void LoginTask()
    {
        try {
            YYHttpClient yyHttpClient = new YYHttpClient();
            yyHttpClient.openRequest(Config.getSrvUrl("UserLogin"), YYHttpClient.REQ_METHOD_POST);
            yyHttpClient.setPostValuesForKey("userName", username);
            yyHttpClient.setPostValuesForKey("userPwd", userpwd);
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
                Config.ClerkID=data.getString("ClerkID");
                Config.ClerkStationID= data.getString("ClerkStationID");

                yyHttpClient.closeRequest();
                yyHttpClient=new YYHttpClient();
                yyHttpClient.openRequest(Config.getSrvUrl("GetPositionData"), YYHttpClient.REQ_METHOD_POST);
                yyHttpClient.setPostValuesForKey("clerkStationID,", Config.ClerkStationID);
                yyHttpClient.setPostValuesForKey("clerkID)", Config.ClerkID);
                yyHttpClient.setEntity(yyHttpClient.getPostData());
                r = yyHttpClient.sendRequest();
                if (!r) {
                    handler.sendEmptyMessage(0);
                    yyHttpClient.closeRequest();
                    return;
                }
                buffer = yyHttpClient.getRespBodyData();
                if (buffer == null) {
                    handler.sendEmptyMessage(0);
                    yyHttpClient.closeRequest();
                    return;
                }

                result = new String(buffer, "utf-8");
                result = Common.getjsonForXML(result);
                Log.i("json返回:",result);


                FileDownload fileDownload=new FileDownload(result, new FileDownload.IFileDownload() {
                    @Override
                    public void OnFileDownloadEvent(int r) {
                        if (r==1)
                        {
                            Toast.makeText(Login.this, "下载基本数据错误", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else
                            handler.sendEmptyMessage(1);
                    }
                });
                fileDownload.streamDownLoadFile();
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
     * 点击设置
     */
    View.OnClickListener onClickListenersetting = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Login.this, Setting.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha, R.anim.alpha_exit);
        }
    };


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 4) {

            AlertDialog.Builder builder=new AlertDialog.Builder(Login.this);
            builder.setTitle("提示").setMessage("确定退出应用！");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    System.exit(0);
                    return;
                }
            });
            builder.setNegativeButton("取消",null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }
}
