package com.pxpd.App;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * sqlite 控制类
 * Created by Administrator on 14-6-5.
 */
public class DB {


    public String userdbpath = "";
    SQLiteDatabase db;
    private Context context;
    SQLiteOpenHelper sqLiteOpenHelper;


    public DB(Context context, String userDbpath) {
        sqLiteOpenHelper = new SQLiteOpenHelper(context, userDbpath, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

            }
        };

        userdbpath = userDbpath;
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        this.context = context;

        db = sqLiteOpenHelper.getWritableDatabase();
    }

    public void closeDB() {
        if (db != null)
            db.close();
    }


    public SQLiteDatabase getDb() {
        return db;
    }


    /**
     * 获取档案数据信息
     * @return
     */
    public Cursor getArchives() {
        try {
            Cursor cursor = db.rawQuery("select * from ArchivesManage", null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
