package com.nhcar.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nhcar.R;
import com.nhcar.entity.ECategory;
import com.nhcar.entity.EComment;
import com.nhcar.entity.ECommentResult;
import com.nhcar.utils.Const;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import loopj.android.image.SmartImageView;

public class CommentAdapter extends BaseAdapter {

    private Context context;//用于存放上下文
    private List<EComment> list=new ArrayList<EComment>();//存放数据
    private LayoutInflater layoutInflater;//获取布局

    //构造方法：需要上下文、数据源集合进行初始化
    public CommentAdapter(Context context,List<EComment> list){
        this.context=context;
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=new ViewHolder();
        layoutInflater=LayoutInflater.from(context);
        //如果视图不存在
        if (convertView==null){
            //获取项布局
            convertView=layoutInflater.inflate(R.layout.activity_comment_item,null);
            //获取项布局的控件

            holder.tv=convertView.findViewById(R.id.text_comment);
            holder.tv1=convertView.findViewById(R.id.text_date);
            holder.tv2=convertView.findViewById(R.id.text_user);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }
        //视图控件的数据绑定
        String imgUrl= Const.SERVER_URL+Const.PIC_URL+list.get(position).getCpid();
        Log.d("<<<<Comment imgURL>>>>",imgUrl);

        holder.tv.setText(list.get(position).getCcontent());
        Date date=new Date();
        holder.tv1.setText(date.toLocaleString());
        holder.tv2.setText(list.get(position).getCname());
        return convertView;
    }
    class ViewHolder{
        TextView tv;
        TextView tv1;
        TextView tv2;
    }
}
