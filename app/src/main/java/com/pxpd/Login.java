package com.pxpd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Login extends Activity {

    private Button btnlogin;
    private ImageView btnsetting,btnoffline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnlogin = (Button)findViewById(R.id.btn_login);
        btnlogin.setOnClickListener(onClickListenerlogin);
        btnsetting = (ImageView)findViewById(R.id.btnsetting);
        btnsetting.setOnClickListener(onClickListenersetting);
        btnoffline = (ImageView)findViewById(R.id.btnoffline);
        btnoffline.setOnClickListener(onClickListeneroffline);


    }

    /**
     * 点击离线
     */
    View.OnClickListener onClickListeneroffline = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(Login.this,OfflineFileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };


    /**
     * 点击登录
     */
    View.OnClickListener onClickListenerlogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(Login.this,MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };

    /**
     * 点击设置
     */
    View.OnClickListener onClickListenersetting = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(Login.this,Setting.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };

}
