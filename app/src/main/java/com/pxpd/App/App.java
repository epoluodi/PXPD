package com.pxpd.App;

import android.app.Application;

/**
 * APP启动
 * Created by Stereo on 16/7/8.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //读取APP 信息环境，第一次运行创建
        Boolean isrun = Config.getKeyShareVarForBoolean(getApplicationContext(),"IsRun");//判断程序是否运行
        if (!isrun)
        {
            Config.setKeyShareVar(getApplicationContext(),"username","null");//用户名
            Config.setKeyShareVar(getApplicationContext(),"userpwd","null");//密码
//			Config.setKeyShareVar(getApplicationContext(),"isautologin",true);
            Config.setKeyShareVar(getApplicationContext(),"IsRun",true);//第一次运行
            Config.setKeyShareVar(getApplicationContext(),"in_ip","192.168.1.1");//内网ip
            Config.setKeyShareVar(getApplicationContext(),"in_port","86");//内网端口
            Config.setKeyShareVar(getApplicationContext(),"out_ip","14546223xi.51mypc.cn");//外网ip
            Config.setKeyShareVar(getApplicationContext(),"out_port","86");//外网端口
            Config.setKeyShareVar(this,"mode",0);//网络模式
        }




    }
}
