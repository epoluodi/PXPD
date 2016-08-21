package com.pxpd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Find_Query extends Activity {
    private ImageView btnreturn;
    private TextView title;
    private ListView listView;
    private String StoreroomID="",
            SAreaID="",
            CompactShelfID="",
            ColNum="";

    private int _StoreroomID=0;

    private String        _SAreaID="",
            _CompactShelfID="",
            _ColNum="";

    private MyAdpter myAdpter;
    private ImageView btn_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__query);

        title = (TextView) findViewById(R.id.title);
        title.setText("筛选");
        btnreturn = (ImageView) findViewById(R.id.btn_return);
        btnreturn.setOnClickListener(onClickListenerreturn);
        listView = (ListView) findViewById(R.id.list);
        myAdpter = new MyAdpter();
        listView.setAdapter(myAdpter);
        listView.setOnItemClickListener(onItemClickListener);
        btn_right = (ImageView)findViewById(R.id.btn_right);
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
            Intent intent=new Intent();
            intent.putExtra("_StoreroomID",_StoreroomID);
            intent.putExtra("_SAreaID",_SAreaID);
            intent.putExtra("_CompactShelfID",_CompactShelfID);
            intent.putExtra("_ColNum",_ColNum);
            setResult(1,intent);
            finish();
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 0:
                if (resultCode ==1)
                {
                    _StoreroomID = data.getIntExtra("StoreroomID",0);
                    StoreroomID = data.getStringExtra("name");
                    myAdpter.notifyDataSetChanged();
                }
                break;
            case 1:
                if (resultCode ==1)
                {
                    _SAreaID = data.getStringExtra("SAreaID");
                    SAreaID = data.getStringExtra("name");
                    myAdpter.notifyDataSetChanged();
                }
                break;
            case 2:
                if (resultCode ==1)
                {
                    _CompactShelfID = data.getStringExtra("CompactShelfID");
                    CompactShelfID = data.getStringExtra("name");
                    myAdpter.notifyDataSetChanged();
                }
                break;
            case 3:
                if (resultCode ==1)
                {
                    _ColNum = data.getStringExtra("ColNum");
                    ColNum = data.getStringExtra("name");
                    myAdpter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * list item点击
     */
    AdapterView.OnItemClickListener onItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent=new Intent(Find_Query.this,Find_list2_Activity.class);
            intent.putExtra("mode",i);
            if (_StoreroomID == 0&& i !=0)
            {
                Toast.makeText(Find_Query.this,"请必须选择一个库房",Toast.LENGTH_SHORT).show();
                return;
            }

            if (i ==1)
            {
                intent.putExtra("_StoreroomID",_StoreroomID);
            }
            else if (i==2)
            {
                intent.putExtra("_StoreroomID",_StoreroomID);
                intent.putExtra("_SAreaID",_SAreaID);

            }
            else if (i==3)
            {
                intent.putExtra("_CompactShelfID",_CompactShelfID);
            }
            startActivityForResult(intent,i);
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



    class MyAdpter extends BaseAdapter
    {
        TextView title;
        TextView content;
        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.list_finditem1,null);
            title = (TextView)view.findViewById(R.id.title);
            content = (TextView)view.findViewById(R.id.content);

            switch (i)
            {
                case 0:
                    title.setText("库房");
                    content.setText(StoreroomID);
                    break;
                case 1:
                    title.setText("区");
                    content.setText(SAreaID);
                    break;
                case 2:
                    title.setText("密集架");
                    content.setText(CompactShelfID);
                    break;
                case 3:
                    title.setText("列");
                    content.setText(ColNum);
                    break;

            }

            return view;
        }
    }
}
