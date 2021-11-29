package com.nhcar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.stmt.query.In;
import com.nhcar.adapter.CartAdapter;
import com.nhcar.db.ORMDAOMessage;
import com.nhcar.entity.ECartItem;
import com.nhcar.entity.EMessage;
import com.nhcar.utils.Const;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityMessageList extends AppCompatActivity implements View.OnClickListener {

    private LayoutInflater mLayoutInflater;
    private ListView lvMessage;
    private List<EMessage>list=new ArrayList<EMessage>();
    private Button productback,btnAdd,btnGetAll;
    private ORMDAOMessage dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        lvMessage=this.findViewById(R.id.lvMessage);
        productback=this.findViewById(R.id.productback);
        btnAdd=this.findViewById(R.id.btnAdd);
        btnGetAll=this.findViewById(R.id.btnGetAll);
    }
    private void initData() {
        dao = new ORMDAOMessage(getApplicationContext());//必须在此实例化，此时已经onCreateView有了view
    }
    private void initListener() {
        productback.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnGetAll.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.productback:
                this.finish();
                break;
            case R.id.btnAdd:
               Intent intent=new Intent(getApplicationContext(),ActivityMessageAdd.class);
               intent.putExtra("mid",0);
               startActivity(intent);
               break;
            case R.id.btnGetAll:
                FillMessage();
                break;
        }
    }
    class ViewHolder {
        TextView tvMid;
        TextView tvTitle;
        TextView tvContent;
        TextView tvDate;
        Button btnEdit;
    }
    class MessageAdapter extends BaseAdapter {

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            mLayoutInflater = LayoutInflater.from(getApplicationContext());
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.activity_message_item, null);
                holder.tvMid = (TextView) convertView.findViewById(R.id.tvMid);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
                holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
                holder.btnEdit=(Button)convertView.findViewById(R.id.btnEdit);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvMid.setText(list.get(position).getMid()+"");
            holder.tvTitle.setText(list.get(position).getMtitle());
            holder.tvContent.setText(list.get(position).getMcontent());
            if (list.get(position).getMdate()!=null){
                SimpleDateFormat df=new SimpleDateFormat("MM-dd HH:mm");
                holder.tvDate.setText(df.format(list.get(position).getMdate()));
            }

            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),ActivityMessageAdd.class);
                    intent.putExtra("mid",list.get(position).getMid());
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }
    private void FillMessage() {
        list.clear();
        list = dao.getMessageList();
        MessageAdapter adapter = new MessageAdapter();
        lvMessage.setAdapter(adapter);
    }


}
