package com.pxpd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pxpd.App.Config;

public class MainActivity extends Activity {

    private ImageView btnreturn;
    private RelativeLayout btnsetting,btndataasyanc,btnolpd,btnfind,btnup;
    private TextView title,netstate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = (TextView)findViewById(R.id.title);
        title.setText("平行盘点综合管理系统");
        btnreturn = (ImageView)findViewById(R.id.btn_return);
        btnreturn.setOnClickListener(onClickListenerreturn);
        btndataasyanc = (RelativeLayout)findViewById(R.id.btndataasync);
        btndataasyanc.setOnClickListener(onClickListenerbtndataasync);
        btnsetting = (RelativeLayout)findViewById(R.id.btnseting);
        btnsetting.setOnClickListener(onClickListenersetting);
        btnolpd = (RelativeLayout)findViewById(R.id.olpd);
        btnolpd.setOnClickListener(onClickListenerolpd);
        btnfind = (RelativeLayout)findViewById(R.id.btnfind);
        btnfind.setOnClickListener(onClickListenerolfind);
        btnup = (RelativeLayout)findViewById(R.id.btnup);
        btnup.setOnClickListener(onClickListenerolup);
        netstate = (TextView)findViewById(R.id.netstate);
        if (Config.Mode==0)
            netstate.setText("连接状态：内网");
        if (Config.Mode==1)
            netstate.setText("连接状态：外网");

    }

    /**
     * 点击档案上架
     */
    View.OnClickListener onClickListenerolup = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(MainActivity.this,UpJiaAvtivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };


    /**
     * 点击定向查找
     */
    View.OnClickListener onClickListenerolfind = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(MainActivity.this,FindActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };



    /**
     * 点击综合盘点
     */
    View.OnClickListener onClickListenerolpd = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(MainActivity.this,PDBeforeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };


    /**
     * 点击返回按钮
     */
    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            onKeyUp(4,null);
        }
    };

    /**
     * 点击设置按钮
     */
    View.OnClickListener onClickListenersetting = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(MainActivity.this,Setting.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };


    /**
     * 点击数据同步
     */
    View.OnClickListener onClickListenerbtndataasync = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(MainActivity.this,DataAsync.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };


    /**
     *  返回登录界面
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 4) {

            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("提示").setMessage("返回到登录界面吗！");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                    overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
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
