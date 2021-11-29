package com.nhcar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.nhcar.entity.EResult;
import com.nhcar.entity.EUser;
import com.nhcar.utils.Const;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityLogin extends AppCompatActivity {
    private OkHttpClient okHttpClient=new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(3,TimeUnit.SECONDS)
            .build(); //第三方网络工具okhttp3对象
    private SharedPreferences sp;
    private Gson gson =new Gson();//JSON工具对象
    private Button personal_back_button,login,register;
    private ToggleButton isShowPassword;
    private EditText loginaccount,loginpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
        initListener();
    }
    private void initView() {

        personal_back_button=this.findViewById(R.id.personal_back_button);
        login=this.findViewById(R.id.login);
        register=this.findViewById(R.id.register);
        loginaccount=this.findViewById(R.id.loginaccount);
        loginpassword=this.findViewById(R.id.loginpassword);
        isShowPassword=this.findViewById(R.id.isShowPassword);
    }
    private void initData() {
        sp=this.getSharedPreferences("nhcarsp",MODE_PRIVATE);
    }
    private void initListener() {
        personal_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(getApplicationContext(),ActivityRegisterNormal0.class);
               startActivity(intent);
               finish();
           }
       });
       login.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               loginUser();
           }
       });
       isShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (isChecked){
                   loginpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
               }else {
                   loginpassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
               }
           }
       });
    }
    public void loginUser(){
        String uname=loginaccount.getText().toString().trim();
        if (uname.equals("")){
            Toast.makeText(this,"账号不能为空！",Toast.LENGTH_LONG).show();
            return;
        }
        String psw=loginpassword.getText().toString().trim();
        if (psw.equals("")){
            Toast.makeText(this,"密码不能为空！",Toast.LENGTH_LONG).show();
            return;
        }
        final EUser user=new EUser();
        user.setUname(uname);
        user.setUpwd(psw);

        String url= Const.SERVER_URL+Const.SERVLET_URL+"checkLogin";
        Log.d("<<<<<url>>>",url);
        String userJson=gson.toJson(user);
        String userJsonEncode="";
        try{
            userJsonEncode= URLEncoder.encode(userJson,"utf-8");
            Log.d("<<<<<<userJsonEncode>>>",userJsonEncode);
        }catch (Exception e){
            Toast.makeText(this,"数据编码错误",Toast.LENGTH_LONG).show();
            return;
        }
        FormBody.Builder formBody=new FormBody.Builder();
        formBody.add("userstr",userJsonEncode);
        Request request=new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("<<loginUser返回值：>>",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                Log.d("loginUser返回值：",result);
                final EResult eResult=gson.fromJson(result,EResult.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (eResult.getResult()>0){
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putBoolean("isLogin",true);
                            editor.putString("loginUserName",user.getUname());
                            editor.putInt("loginUserId",eResult.getResult());
                            editor.commit();
                            Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_LONG).show();
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), eResult.getMsg(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}