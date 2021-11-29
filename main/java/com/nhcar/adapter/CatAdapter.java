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
import com.nhcar.utils.Const;

import java.util.ArrayList;
import java.util.List;

import loopj.android.image.SmartImageView;

public class CatAdapter extends BaseAdapter {

    private Context context;//用于存放上下文
    private List<ECategory> listCat=new ArrayList<ECategory>();//存放数据
    private LayoutInflater layoutInflater;//获取布局

    //构造方法：需要上下文、数据源集合进行初始化
    public CatAdapter(Context context,List<ECategory> list){
        this.context=context;
        this.listCat=list;
    }
    @Override
    public int getCount() {
        return listCat.size();
    }

    @Override
    public Object getItem(int position) {
        return listCat.get(position);
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
            convertView=layoutInflater.inflate(R.layout.activity_index_category_item,null);
            //获取项布局的控件
            holder.smv=convertView.findViewById(R.id.imageView8);
            holder.tv=convertView.findViewById(R.id.textView14);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }
        //视图控件的数据绑定
        String imgUrl= Const.SERVER_URL+Const.PIC_URL+listCat.get(position).getCpic();
        Log.d("<<<<Cat imgURL>>>>",imgUrl);
        holder.smv.setImageUrl(imgUrl);
        holder.tv.setText(listCat.get(position).getCname());
        return convertView;
    }
    class ViewHolder{
        SmartImageView smv;
        TextView tv;
    }
}
