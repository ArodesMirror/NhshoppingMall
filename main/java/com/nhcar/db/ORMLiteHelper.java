package com.nhcar.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import com.nhcar.entity.EMessage;

import java.sql.SQLException;

/**
 * Created by Administrator on 2019-06-08.
 */

public class ORMLiteHelper extends OrmLiteSqliteOpenHelper {
    private static final String DBNAME = "nhcar";//数据库名称
    private static final int DBVERSION = 1;//版本号，修改表则升级此号

    //userDao ，每张表对应一个
    private Dao<EMessage, Integer> messagesDao;
    private ORMLiteHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);//构造函数中建库
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource,EMessage.class);//入口方法中建表。表先按格式建实体

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, EMessage.class, true);

            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static ORMLiteHelper instance;
    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized ORMLiteHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (ORMLiteHelper.class) {
                if (instance == null)
                    instance = new ORMLiteHelper(context);
            }
        }
        return instance;
    }
    /**
     * 每个实体都要建立一个get***Dao，用于对该实体进行增删改查操作
     *
     *Dao<T,ID>：T为实体类名，ID为主键数据类型
     * @return
     * @throws SQLException
     */
    public Dao<EMessage, Integer> getUserDao() throws SQLException {
        if (messagesDao == null) {
            messagesDao = getDao(EMessage.class);
        }
        return messagesDao;
    }
    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        messagesDao = null;
    }
}
