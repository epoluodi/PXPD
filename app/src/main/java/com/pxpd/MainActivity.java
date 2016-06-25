package com.pxpd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private ImageView btnreturn;
    private RelativeLayout btnsetting,btndataasyanc,btnolpd,btnfind,btnup;
    private TextView title;
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
            Intent intent=new Intent(MainActivity.this,online_pd.class);
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
            finish();
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
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



}
