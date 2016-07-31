package com.pxpd;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pxpd.App.App;
import com.pxpd.App.DB;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定义筛选对话框
 *
 * @author YXG
 */
public class Find_Dialog extends Dialog {
    private Spinner sp1, sp2, sp3, sp4, sp5, sp6, sp7;
    private Button btn_cancel, btn_ok;
    private IDialogReslut iDialogReslut;
    private DB db;

    private List<String> stringList1 = new ArrayList<String>();
    private ArrayAdapter<String> adapter1;
    private List<String> stringList2 = new ArrayList<String>();
    private ArrayAdapter<String> adapter2;
    private List<String> stringList3 = new ArrayList<String>();
    private ArrayAdapter<String> adapter3;
    private List<String> stringList4 = new ArrayList<String>();
    private ArrayAdapter<String> adapter4;
    private List<String> stringList5 = new ArrayList<String>();
    private ArrayAdapter<String> adapter5;
    private List<String> stringList6 = new ArrayList<String>();
    private ArrayAdapter<String> adapter6;
    private List<String> stringList7 = new ArrayList<String>();
    private ArrayAdapter<String> adapter7;

    private String StoreroomID, SAreaID, CompactShelfID,
            ColNum, ABSide,GroupNum,CaseNum;


    public void setiDialogReslut(IDialogReslut iDialogReslut) {
        this.iDialogReslut = iDialogReslut;
    }

    public Find_Dialog(Context context) {
        super(context, R.style.dialog);
        setContentView(R.layout.find_dialog);
        db = new DB(App.getAPP(), App.getAPP().getFilesDir() + File.separator + "basedb.db");
        sp1 = (Spinner) findViewById(R.id.sp1);
        sp2 = (Spinner) findViewById(R.id.sp2);
        sp3 = (Spinner) findViewById(R.id.sp3);
        sp4 = (Spinner) findViewById(R.id.sp4);
        sp5 = (Spinner) findViewById(R.id.sp5);
        sp6 = (Spinner) findViewById(R.id.sp6);
        sp7 = (Spinner) findViewById(R.id.sp7);


        sp1.setOnItemSelectedListener(onItemSelectedListener1);
        sp2.setOnItemSelectedListener(onItemSelectedListener2);
        sp3.setOnItemSelectedListener(onItemSelectedListener3);
        sp4.setOnItemSelectedListener(onItemSelectedListener4);
        sp5.setOnItemSelectedListener(onItemSelectedListener5);
        sp6.setOnItemSelectedListener(onItemSelectedListener6);
        sp7.setOnItemSelectedListener(onItemSelectedListener7);

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_cancel.setOnClickListener(onClickListenercancel);
        btn_ok.setOnClickListener(onClickListenerok);


        initSp();
        setTitle("海选");
        setCanceledOnTouchOutside(false);    //设置点击Dialog外部任意区域不能关闭Dialog
        setCancelable(false);        // 设置为false，按返回键不能退出
    }


    AdapterView.OnItemSelectedListener onItemSelectedListener1 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Log.i("name ", stringList1.get(i));
            StoreroomID = db.getStoreroomManagerid(stringList1.get(i));
            Cursor cursor;
            cursor = db.getAreaManager(StoreroomID);
            if (cursor == null) {
                dismiss();
                Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();
                return;
            }
            stringList2 = new ArrayList<String>();
            while (cursor.moveToNext()) {
                stringList2.add(cursor.getString(1));
            }
            cursor.close();
            adapter2 = new ArrayAdapter<String>(getContext(), R.layout.type_list,
                    stringList2);
            sp2.setAdapter(adapter2);


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


    AdapterView.OnItemSelectedListener onItemSelectedListener2 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            SAreaID = db.getAreaManagerid(StoreroomID, stringList2.get(i));
            Cursor cursor;
            cursor = db.getCompactShelfManage(StoreroomID, SAreaID);
            if (cursor == null) {
                dismiss();
                Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();
                return;
            }
            stringList3 = new ArrayList<String>();
            while (cursor.moveToNext()) {
                stringList3.add(cursor.getString(1));
            }
            cursor.close();
            adapter3 = new ArrayAdapter<String>(getContext(), R.layout.type_list,
                    stringList3);
            sp3.setAdapter(adapter3);


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


    AdapterView.OnItemSelectedListener onItemSelectedListener3 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            CompactShelfID = db.getCompactShelfManageid(StoreroomID,
                    SAreaID, stringList3.get(i));
            Cursor cursor;
            cursor = db.getCompactShelfCol(CompactShelfID);
            if (cursor == null) {
                dismiss();
                Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();
                return;
            }
            stringList4 = new ArrayList<String>();
            while (cursor.moveToNext()) {
                stringList4.add(cursor.getString(0));
            }
            cursor.close();
            adapter4 = new ArrayAdapter<String>(getContext(), R.layout.type_list,
                    stringList4);
            sp4.setAdapter(adapter4);


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


    AdapterView.OnItemSelectedListener onItemSelectedListener4 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


            ColNum = stringList4.get(i);
            Cursor cursor;
            cursor = db.getColABSideManage(CompactShelfID, ColNum);
            if (cursor == null) {
                dismiss();
                Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();
                return;
            }
            stringList5 = new ArrayList<String>();
            while (cursor.moveToNext()) {
                if (cursor.getString(0).equals("1"))
                    stringList5.add("A面");
                if (cursor.getString(0).equals("2"))
                    stringList5.add("B面");
            }
            cursor.close();
            adapter5 = new ArrayAdapter<String>(getContext(), R.layout.type_list,
                    stringList5);
            sp5.setAdapter(adapter5);


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListener5 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            if (stringList5.get(i).equals("A面"))
                ABSide = "1";
            if (stringList5.get(i).equals("B面"))
                ABSide = "2";

            Cursor cursor;
            cursor = db.getColGroupManage(CompactShelfID, ColNum, ABSide);
            if (cursor == null) {
                dismiss();
                Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();
                return;
            }
            stringList6 = new ArrayList<String>();
            while (cursor.moveToNext()) {

                stringList6.add(cursor.getString(0));

            }
            cursor.close();
            adapter6 = new ArrayAdapter<String>(getContext(), R.layout.type_list,
                    stringList6);
            sp6.setAdapter(adapter6);


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListener6 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            GroupNum = stringList6.get(i);
            Cursor cursor;
            cursor = db.getColCaseManage(CompactShelfID, ColNum, ABSide,GroupNum);
            if (cursor == null) {
                dismiss();
                Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();
                return;
            }
            stringList7 = new ArrayList<String>();
            while (cursor.moveToNext()) {

                stringList7.add(cursor.getString(0));

            }
            cursor.close();
            adapter7 = new ArrayAdapter<String>(getContext(), R.layout.type_list,
                    stringList7);
            sp7.setAdapter(adapter7);


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListener7 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            CaseNum = stringList7.get(i);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


    /**
     * 初始化层级数据
     */
    private void initSp() {

        try {
            Cursor cursor;
            cursor = db.getStoreroomManager();
            if (cursor == null)
                throw new Exception();
            stringList1 = new ArrayList<String>();
            while (cursor.moveToNext()) {
                stringList1.add(cursor.getString(1));
            }
            cursor.close();
            adapter1 = new ArrayAdapter<String>(getContext(), R.layout.type_list,
                    stringList1);
            sp1.setAdapter(adapter1);

        } catch (Exception e) {
            e.printStackTrace();
            dismiss();
            Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();

        }

    }


    View.OnClickListener onClickListenercancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

    View.OnClickListener onClickListenerok = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
            Map<String ,String> map=new HashMap<String, String>();
            map.put("StoreroomID",StoreroomID);
            map.put("SAreaID",SAreaID);
            map.put("CompactShelfID",CompactShelfID);
            map.put("ColNum",ColNum);
            map.put("ABSide",ABSide);
            map.put("GroupNum",GroupNum);
            map.put("CaseNum",CaseNum);
            iDialogReslut.OnClickOk(map);
        }
    };


    public interface IDialogReslut {
        void OnClickOk(Map<String, String> map);
    }
}
