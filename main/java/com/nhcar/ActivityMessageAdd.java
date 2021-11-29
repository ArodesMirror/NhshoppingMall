package com.nhcar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.j256.ormlite.stmt.query.In;
import com.nhcar.db.ORMDAOMessage;
import com.nhcar.entity.EMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ActivityMessageAdd extends AppCompatActivity implements  View.OnClickListener{
    private OkHttpClient okHttpClient=new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(3,TimeUnit.SECONDS)
            .build(); //第三方网络工具okhttp3对象
    private Gson gson =new Gson();//JSON工具对象
    private Button btnSave,productback;
    private EditText tvMid,etTitle,etContent,tvDate;
    private ORMDAOMessage dao;
    private EMessage message=new EMessage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_add_item);
        initView();
        initData();
        initListener();
    }


    private void initView() {
        btnSave=this.findViewById(R.id.btnSave);
        productback=this.findViewById(R.id.productback);
        tvMid=this.findViewById(R.id.tvMid);
        etTitle=this.findViewById(R.id.etTitle);
        etContent=this.findViewById(R.id.etContent);
        tvDate=this.findViewById(R.id.tvDate);

    }
    private void initData() {
        dao=new ORMDAOMessage(getApplicationContext());
        Intent intent=getIntent();
        int mid=intent.getIntExtra("mid",0);
        if (mid>0){
            message=dao.getMessageByMid(mid);
            tvMid.setText(message.getMid()+"");
            etTitle.setText(message.getMtitle());
            etContent.setText(message.getMcontent());
            if (message.getMdate()!=null){
                SimpleDateFormat df=new SimpleDateFormat("MM-dd HH:mm");
                tvDate.setText(df.format(message.getMdate()));
            }
        }else {
            tvDate.setText("");
            etTitle.setText("");
            etContent.setText("");
            tvDate.setText("");
        }
        etTitle.requestFocus();
    }
    private void initListener() {
        productback.setOnClickListener(this);
        btnSave.setOnClickListener(this);
   }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.btnSave:
                if (etTitle.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"标题不能为空。",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etContent.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"内容不能为空。",Toast.LENGTH_SHORT).show();
                    return;
                }
                message.setMtitle(etTitle.getText().toString().trim());
                message.setMcontent(etContent.getText().toString().trim());
                String mid=tvMid.getText().toString().trim();
                if (mid.equals("")){
                    Date d=new Date();
                    message.setMdate(d);
                    if (dao.insert(message)>0){
                        Toast.makeText(getApplicationContext(),"消息添加成功。",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(),"消息添加失败。",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    if (dao.update(message)>0){
                        Toast.makeText(getApplicationContext(),"消息更新成功。",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(),"消息更新失败。",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.productback:
                this.finish();
                break;
        }
    }

}
