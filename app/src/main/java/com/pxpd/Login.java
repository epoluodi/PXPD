package com.pxpd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Login extends AppCompatActivity {

    private Button btnlogin;
    private ImageView btnsetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnlogin = (Button)findViewById(R.id.btn_login);
        btnlogin.setOnClickListener(onClickListenerlogin);
        btnsetting = (ImageView)findViewById(R.id.btnsetting);
        btnsetting.setOnClickListener(onClickListenersetting);

    }


    View.OnClickListener onClickListenerlogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(Login.this,MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };

    View.OnClickListener onClickListenersetting = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(Login.this,Setting.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };

}
