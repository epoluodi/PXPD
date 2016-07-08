package com.pxpd;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pxpd.App.Config;

public class Setting extends Activity {
    private ImageView btnreturn,btnright;
    private TextView title;
    private EditText inip,inport,outip,outport;
    private String _inip,_inport,_outip,_outport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        btnreturn = (ImageView)findViewById(R.id.btn_return);
        btnreturn.setOnClickListener(onClickListenerreturn);
        btnright = (ImageView)findViewById(R.id.btn_right);
        btnright.setOnClickListener(onClickListenersave);
        title = (TextView)findViewById(R.id.title);
        title.setText("设置");
        btnright.setVisibility(View.VISIBLE);
        btnright.setBackground(getResources().getDrawable(R.drawable.btn_save_select));

        inip = (EditText)findViewById(R.id.in_ip);
        inport = (EditText)findViewById(R.id.in_port);
        outip = (EditText)findViewById(R.id.out_ip);
        outport = (EditText)findViewById(R.id.out_port);

        _inip = Config.getKeyShareVarForString(this,"in_ip");
        _inport = Config.getKeyShareVarForString(this,"in_port");
        _outip = Config.getKeyShareVarForString(this,"out_ip");
        _outport = Config.getKeyShareVarForString(this,"out_port");

        inip.setText(_inip);
        inport.setText(_inport);
        outip.setText(_outip);
        outport.setText(_outport);

    }


    /**
     * 点击保存
     */
    View.OnClickListener onClickListenersave = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            _inip = inip.getText().toString();
            _inport = inport.getText().toString();
            _outip = outip.getText().toString();
            _outport = outport.getText().toString();

            if (_inip.equals("")||_inport.equals("")||
                    _outip.equals("")||_outport.equals(""))
            {
                Toast.makeText(Setting.this,"请设置内网和外网服务地址信息",Toast.LENGTH_SHORT).show();
                return;
            }

            Config.setKeyShareVar(Setting.this,"in_ip",_inip);
            Config.setKeyShareVar(Setting.this,"in_port",_inport);
            Config.setKeyShareVar(Setting.this,"out_ip",_outip);
            Config.setKeyShareVar(Setting.this,"out_port",_outport);

            Toast.makeText(Setting.this,"设置成功",Toast.LENGTH_SHORT).show();



            finish();
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };



    /**
     * 点击返回
     */
    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            overridePendingTransition(R.anim.alpha,R.anim.alpha_exit);
        }
    };

}
