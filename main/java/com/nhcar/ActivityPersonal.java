package com.nhcar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ActivityPersonal extends AppCompatActivity {
    private OkHttpClient okHttpClient=new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(3,TimeUnit.SECONDS)
            .build(); //第三方网络工具okhttp3对象
    private Gson gson =new Gson();//JSON工具对象
    private SharedPreferences sp;
    private TextView personal_welcome;
    private RelativeLayout relMessage;
    private Button personal_reg_button,personal_login_button,personal_alter_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initView();
        initData();
        initListener();
    }
    private void initView() {
        personal_reg_button=this.findViewById(R.id.personal_reg_button);
        personal_login_button=this.findViewById(R.id.personal_login_button);
        personal_welcome=this.findViewById(R.id.personal_welcome);
        personal_alter_button=this.findViewById(R.id.personal_alter_button);
        relMessage=this.findViewById(R.id.relMessage);
    }
    private void initData() {
        sp=this.getSharedPreferences("nhcarsp",MODE_PRIVATE);
        checkLogin();
    }
    private void initListener() {
        personal_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (personal_login_button.getText().toString().equals("登录")){
                    Intent intent=new Intent(getApplicationContext(),ActivityLogin.class);
                    startActivity(intent);
                }else {
                     SharedPreferences.Editor editor=sp.edit();
                     editor.remove("isLogin");
                     editor.remove("loginUserName");
                     editor.remove("loginUser");
                     editor.commit();
                     checkLogin();
                }
            }
        });
        personal_alter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ActivityUpdateUser.class);
                startActivity(intent);
            }
        });
        personal_reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ActivityRegisterNormal0.class);
                startActivity(intent);
            }
        });
        relMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ActivityMessageList.class);
                startActivity(intent);
            }
        });
    }
    private void checkLogin(){
        Boolean isLogin=sp.getBoolean("isLogin",false);
        String uname=sp.getString("loginUserName","");
        if (isLogin){
            personal_login_button.setText("注销");
            personal_welcome.setText(uname+",欢迎来到南华汽车商城！");
            personal_alter_button.setVisibility(View.VISIBLE);
        }else {
            personal_login_button.setText("登录");
            personal_welcome.setText("欢迎来到南华汽车商城！");
            personal_alter_button.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }
}
