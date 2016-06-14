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
    private RelativeLayout btnsetting;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = (TextView)findViewById(R.id.title);
        title.setText("平行盘点综合管理系统");
        btnreturn = (ImageView)findViewById(R.id.btn_return);
        btnreturn.setOnClickListener(onClickListenerreturn);

        btnsetting = (RelativeLayout)findViewById(R.id.btnseting);
        btnsetting.setOnClickListener(onClickListenersetting);

    }


    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };

    View.OnClickListener onClickListenersetting = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(MainActivity.this,Setting.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };


}
