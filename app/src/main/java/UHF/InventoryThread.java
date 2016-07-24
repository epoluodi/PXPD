package UHF;

import android.util.Log;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
import com.pxpd.Login;

import java.util.ArrayList;
import java.util.List;

import cn.pda.serialport.Util;

/**
 * Created by cjw on 16/7/24.
 */
public class InventoryThread extends Thread{
    private List<byte[]> epcList;
    private boolean runFlag = true;
    private boolean startFlag = false;
    private UhfReader reader;




    public InventoryThread()
    {
        reader =UhfReader.getInstance();
    }


    public void StartScan()
    {
        startFlag =true;
    }
    /**
     * 暂停
     */
    public void PauseScan()
    {
        startFlag=false;
    }


    /**
     * 关闭
     */
    public void CloseScan()
    {
        startFlag=false;
        runFlag=false;

    }
    @Override
    public void run() {
        super.run();
        while(runFlag){
            if(startFlag){
                epcList = reader.inventoryRealTime();
                if(epcList != null && !epcList.isEmpty()){

                    Util.play(1, 0);
                    for(byte[] epc:epcList){
                        String epcStr = Tools.Bytes2HexString(epc, epc.length);
                        Log.i("rfid ",epcStr);
                    }
                }
                epcList = null ;
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}