package com.nhcar.db;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import com.nhcar.entity.EMessage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019-06-07.
 * 数据层，完成增删改查操作
 */
public class ORMDAOMessage {
    private Context context;
    private Dao<EMessage, Integer> messagesDao;  //用于该表操作的数据访问对象Dao

    public ORMDAOMessage(Context context) {
        this.context = context;
        try {
            messagesDao = ORMLiteHelper.getHelper(context).getUserDao();//构造方法中获取 Dao
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int insert(EMessage message) {
        int num = 0;
        try {
            num = messagesDao.create(message);
            Log.d("<<<添加记录后返回值：>>>",num+"");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return num;
    }
    public int delete(int no) {
        int num = 0;
        try {
            num = messagesDao.deleteById(no);
            Log.d("<<<删除记录后返回值：>>>",num+"");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return num;
    }
    public int update(EMessage message) {
        int num = 0;
        try {
            num = messagesDao.update(message);
            Log.d("<<<更新记录后返回值：>>>",num+"");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return num;
    }
    public List<EMessage> getMessageList() {
        List<EMessage> list = new ArrayList<EMessage>();
        try {
            list= messagesDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public EMessage getMessageByMid(int no) {
        EMessage message = new EMessage();
        try {
            message= messagesDao.queryForId(no);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }
}
