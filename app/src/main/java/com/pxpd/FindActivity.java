package com.pxpd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.Map;

public class FindActivity extends Activity {
    private ImageView btnreturn;

    private TextView title;
    private Button btn_saixuan;
    private String StoreroomID, SAreaID, CompactShelfID,
            ColNum, ABSide,GroupNum,CaseNum;
    private ImageView btn_right;
    private int menumode= 1;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        title = (TextView)findViewById(R.id.title);
        title.setText("定向查找");
        btnreturn = (ImageView)findViewById(R.id.btn_return);
        btnreturn.setOnClickListener(onClickListenerreturn);
        btn_saixuan = (Button)findViewById(R.id.btn_saixuan);
        btn_saixuan.setOnClickListener(onClickListenerbtn_saixuan);
        btn_right = (ImageView)findViewById(R.id.btn_right);
        btn_right.setVisibility(View.VISIBLE);
        btn_right.setBackground(getResources().getDrawable(R.drawable.btn_find_menu_select));
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOptionsMenu();

            }
        });
        searchView = (SearchView)findViewById(R.id.search_view);
        searchView.onActionViewExpanded();
        searchView.setQueryHint("档案号");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.createtask, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.archivesNumKeyword:
                menumode=1;
                searchView.setQueryHint("档案号");


                break;
            case R.id.fileTitleKeyword:
                menumode=2;
                searchView.setQueryHint("案卷名称");
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * 点击筛选
     */
    View.OnClickListener onClickListenerbtn_saixuan = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent=new Intent(FindActivity.this,Find_Query.class);
            startActivityForResult(intent,0);
        }
    };

    Find_Dialog.IDialogReslut iDialogReslut=new Find_Dialog.IDialogReslut() {
        @Override
        public void OnClickOk(Map<String, String> map) {
            StoreroomID = map.get("StoreroomID");
            SAreaID = map.get("SAreaID");
            CompactShelfID = map.get("CompactShelfID");
            ColNum = map.get("ColNum");
            ABSide = map.get("ABSide");
            GroupNum = map.get("GroupNum");
            CaseNum = map.get("CaseNum");

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




}
