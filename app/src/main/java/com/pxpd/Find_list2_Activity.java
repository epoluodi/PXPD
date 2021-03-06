package com.pxpd;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pxpd.App.App;
import com.pxpd.App.DB;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Find_list2_Activity extends Activity {

    private ImageView btnreturn;
    private TextView title;
    private ListView listView;
    private MyAdpter myAdpter;
    private int mode = 1;
    private ImageView btn_right;
    private DB db;
    private List<String> stringList;
    private List<Integer> idList;
    private List<String> idstrlist;
    private int _StoreroomID = 0;
    private String _SAreaID = "",_CompactShelfID="";
    private List<Integer> selectlist;
    private List<String> selectstridlist;
    private List<String> selectlistname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_list2_);
        title = (TextView) findViewById(R.id.title);
        title.setText("筛选");
        btnreturn = (ImageView) findViewById(R.id.btn_return);
        btnreturn.setOnClickListener(onClickListenerreturn);
        listView = (ListView) findViewById(R.id.list);
        mode = getIntent().getIntExtra("mode", 0);
        stringList = new ArrayList<String>();
        idList = new ArrayList<Integer>();
        idstrlist = new ArrayList<String>();
        selectlist = new ArrayList<Integer>();
        selectlistname = new ArrayList<String>();
        selectstridlist = new ArrayList<String>();
        db = new DB(App.getAPP(), App.getAPP().getFilesDir() + File.separator + "basedb.db");
        switch (mode) {
            case 0:
                title.setText("库房");
                try {
                    Cursor cursor;
                    cursor = db.getStoreroomManager();
                    if (cursor == null)
                        throw new Exception();

                    while (cursor.moveToNext()) {
                        idList.add(cursor.getInt(0));
                        stringList.add(cursor.getString(1));
                    }
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
            case 1:

                title.setText("区");
                _StoreroomID = getIntent().getIntExtra("_StoreroomID", 0);
                try {
                    Cursor cursor;
                    cursor = db.getAreaManager(String.valueOf(_StoreroomID));
                    if (cursor == null)
                        throw new Exception();

                    while (cursor.moveToNext()) {
                        idList.add(cursor.getInt(0));
                        stringList.add(cursor.getString(1));
                    }
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
            case 2:
                title.setText("密集架");
                _StoreroomID = getIntent().getIntExtra("_StoreroomID", 0);
                _SAreaID = getIntent().getStringExtra("_SAreaID");
                try {
                    Cursor cursor;
                    String[] strs = _SAreaID.split(",");
                    for (int i = 0; i < strs.length; i++) {
                        cursor = db.getCompactShelfManage(String.valueOf(_StoreroomID), strs[i]);
                        if (cursor == null)
                            throw new Exception();
                        while (cursor.moveToNext()) {
                            idstrlist.add(cursor.getString(0));
                            stringList.add(cursor.getString(1));
                        }
                        cursor.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();
                    return;
                }


                break;
            case 3:
                title.setText("列");
                _CompactShelfID = getIntent().getStringExtra("_CompactShelfID");
                try {
                    Cursor cursor;
                    String[] strs = _CompactShelfID.split(",");
                    for (int i = 0; i < strs.length; i++) {
                        cursor = db.getCompactShelfCol(strs[i]);
                        if (cursor == null)
                            throw new Exception();
                        while (cursor.moveToNext()) {
                            idstrlist.add(cursor.getString(3));
                            stringList.add(cursor.getString(4));
                        }
                        cursor.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(App.getAPP(), "获取基本数据失败", Toast.LENGTH_SHORT).show();
                    return;
                }


                break;

        }
        myAdpter = new MyAdpter();
        listView.setAdapter(myAdpter);
        btn_right = (ImageView) findViewById(R.id.btn_right);
        btn_right.setVisibility(View.VISIBLE);
        btn_right.setBackground(getResources().getDrawable(R.drawable.btn_ok_select));
        btn_right.setOnClickListener(onClickListenerright);


    }


    /**
     * 确认条件
     */
    View.OnClickListener onClickListenerright = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            if (mode == 1) {
                String name = "", id = "";

                for (int i = 0; i < selectlist.size(); i++) {
                    name += selectlistname.get(i) + ",";
                    id += selectlist.get(i) + ",";
                }
                name = name.substring(0, name.length() - 1);
                id = id.substring(0, id.length() - 1);
                intent.putExtra("SAreaID", id);
                intent.putExtra("name", name);
                setResult(1, intent);
                finish();
                return;
            }
            if (mode == 2) {
                String name = "", id = "";

                for (int i = 0; i < selectstridlist.size(); i++) {
                    name += selectlistname.get(i) + ",";
                    id += selectstridlist.get(i) + ",";
                }
                name = name.substring(0, name.length() - 1);
                id = id.substring(0, id.length() - 1);
                intent.putExtra("CompactShelfID", id);
                intent.putExtra("name", name);
                setResult(1, intent);
                finish();
                return;
            }
            if (mode == 3) {
                String name = "", id = "";

                for (int i = 0; i < selectstridlist.size(); i++) {
                    name += selectlistname.get(i) + ",";
                    id += selectstridlist.get(i) + ",";
                }
                name = name.substring(0, name.length() - 1);
                id = id.substring(0, id.length() - 1);
                intent.putExtra("ColNum", id);
                intent.putExtra("name", name);
                setResult(1, intent);
                finish();
                return;
            }
        }
    };


    /**
     * 点击返回按钮
     */
    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            overridePendingTransition(R.anim.alpha, R.anim.alpha_exit);
        }
    };


    /**
     * 多选
     */
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            Intent intent = new Intent();
            String str;
            String idstr;
            int id;
            if (mode == 0) {
                str = stringList.get((Integer) compoundButton.getTag());
                int StoreroomID = idList.get((Integer) compoundButton.getTag());
                intent.putExtra("StoreroomID", StoreroomID);
                intent.putExtra("name", str);
                setResult(1, intent);
                finish();
                return;
            }
            if (mode == 1) {
                str = stringList.get((Integer) compoundButton.getTag());
                id = idList.get((Integer) compoundButton.getTag());

                if (b) {
                    if (!selectlist.contains(id)) {
                        selectlist.add(id);
                        selectlistname.add(str);
                    }
                } else {
                    selectlist.remove(id);
                    selectlistname.remove(str);
                }
                return;
            }
            if (mode == 2) {
                str = stringList.get((Integer) compoundButton.getTag());
                idstr = idstrlist.get((Integer) compoundButton.getTag());

                if (b) {
                    if (!selectstridlist.contains(idstr)) {
                        selectstridlist.add(idstr);
                        selectlistname.add(str);
                    }
                } else {
                    selectstridlist.remove(idstr);
                    selectlistname.remove(str);
                }
                return;
            }
            if (mode == 3) {
                str = stringList.get((Integer) compoundButton.getTag());
                idstr = idstrlist.get((Integer) compoundButton.getTag());

                if (b) {
                    if (!selectstridlist.contains(idstr)) {
                        selectstridlist.add(idstr);
                        selectlistname.add(str);
                    }
                } else {
                    selectstridlist.remove(idstr);
                    selectlistname.remove(str);
                }
                return;
            }

        }
    };


    class MyAdpter extends BaseAdapter {
        CheckBox checkBox;
        TextView content;

        @Override
        public int getCount() {
            return stringList.size();
        }

        @Override
        public Object getItem(int i) {
            return stringList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.list_finditem2, null);
            content = (TextView) view.findViewById(R.id.content);
            checkBox = (CheckBox) view.findViewById(R.id.chk);
            String s = stringList.get(i);
            content.setText(s);
            checkBox.setTag(i);
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
            return view;
        }
    }


}
