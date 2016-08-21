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
     *
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


    /**
     * 获得档案号状态
     *
     * @param ArchivesNum
     * @return
     */
    public String getArchivesState(String ArchivesNum) {
        try {
            Cursor cursor = db.rawQuery("select ArchivesState from ArchivesManage where ArchivesNum = ?",
                    new String[]{ArchivesNum});
            cursor.moveToNext();
            String ArchivesState = cursor.getString(0);
            cursor.close();

            return ArchivesState;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 更新档案状态
     *
     * @param ArchivesNum
     * @param ArchivesState
     */
    public void updateArchivesState(String ArchivesNum, String ArchivesState) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("ArchivesState", ArchivesState);
            db.update("ArchivesManage", cv, "ArchivesNum= ?", new String[]{ArchivesNum});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 剔除记录
     *
     * @param ArchivesNum
     */
    public void deleteArchivesState(String ArchivesNum) {
        try {

            db.delete("ArchivesManage", "ArchivesNum= ?", new String[]{ArchivesNum});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 获取库房表
     *
     * @return
     */
    public Cursor getStoreroomManager() {
        try {
            Cursor cursor = db.rawQuery("select * from StoreroomManager", null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getStoreroomManagerid(String name) {
        try {
            Cursor cursor = db.rawQuery("select StoreroomID from StoreroomManager " +
                            "where StoreroomName = ?",
                    new String[]{name});
            cursor.moveToNext();
            String s = cursor.getString(0);
            cursor.close();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 获取区数据
     *
     * @return
     */
    public Cursor getAreaManager(String StoreroomID) {
        try {
            Cursor cursor = db.rawQuery("select * from AreaManager where StoreroomID= ?",
                    new String[]{StoreroomID});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getAreaManagerid(String StoreroomID, String id) {
        try {
            Cursor cursor = db.rawQuery("select SAreaID from AreaManager where StoreroomID= ? and " +
                            "SAreaID = ?",
                    new String[]{StoreroomID, String.valueOf(id)});
            cursor.moveToNext();
            String s = cursor.getString(0);
            cursor.close();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 获取密集架数据
     *
     * @return
     */
    public Cursor getCompactShelfManage(String StoreroomID, String SAreaID) {
        try {
            Cursor cursor = db.rawQuery("select * from CompactShelfManage where " +
                    "StoreroomID = ? and SAreaID = ?", new String[]{StoreroomID, SAreaID});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getCompactShelfManageid(String StoreroomID, String SAreaID, String CompactShelfName) {
        try {
            Cursor cursor = db.rawQuery("select CompactShelfID from CompactShelfManage where " +
                            "StoreroomID = ? and SAreaID = ? and CompactShelfName = ?",
                    new String[]{StoreroomID, SAreaID, CompactShelfName});
            cursor.moveToNext();
            String s = cursor.getString(0);
            cursor.close();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 获取密集架列数据
     *
     * @return
     */
    public Cursor getCompactShelfCol(String CompactShelfID) {
        try {
            Cursor cursor = db.rawQuery("select * from CompactShelfCol where" +
                    " CompactShelfID = ?", new String[]{CompactShelfID});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 获取AB面列数据
     *
     * @return
     */
    public Cursor getColABSideManage(String CompactShelfID, String ColNum) {
        try {
            Cursor cursor = db.rawQuery("select ABSide from ColABSideManage where " +
                            "CompactShelfID = ? and ColNum = ?",
                    new String[]{CompactShelfID, ColNum});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 获取组列数据
     *
     * @return
     */
    public Cursor getColGroupManage(String CompactShelfID, String ColNum, String ABSide) {
        try {
            Cursor cursor = db.rawQuery("select GroupNum from ColGroupManage where " +
                            " CompactShelfID = ? and ColNum = ? and ABSide = ?",
                    new String[]{CompactShelfID, ColNum, ABSide});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 获取格数据
     *
     * @return
     */
    public Cursor getColCaseManage(String CompactShelfID, String ColNum, String ABSide
            , String GroupNum) {
        try {
            Cursor cursor = db.rawQuery("select CaseNum from ColCaseManage where " +
                    "CompactShelfID = ? and ColNum = ? and ABSide = ? and " +
                    "GroupNum = ? ", new String[]{CompactShelfID, ColNum, ABSide, GroupNum});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public String getCompactShelfName(String CompactShelfId)
    {
        try {
            Cursor cursor = db.rawQuery("select CompactShelfName from CompactShelfCol where " +
                    "CompactShelfID = ? ", new String[]{CompactShelfId});
            if (cursor.getCount()==0)
            {
                cursor.close();
                return "";
            }
            cursor.moveToNext();
            String r = cursor.getString(0);
            cursor.close();
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 读取定向查找
     * @return
     */
    public Cursor getArchivesMange() {
        try {
            Cursor cursor = db.rawQuery("select * from ArchivesManage", null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


}
