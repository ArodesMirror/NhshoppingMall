package com.nhcar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class ActivityRegisterNormal0 extends AppCompatActivity {
    private OkHttpClient okHttpClient=new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(3,TimeUnit.SECONDS)
            .build(); //第三方网络工具okhttp3对象
    private Gson gson =new Gson();//JSON工具对象
    private Button productback,login;
    private EditText account0,email0,psw0,repsw0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_normal0);
        initView();
        initData();
        initListener();
    }
    private void initView() {
        productback=this.findViewById(R.id.productback);
        login=this.findViewById(R.id.login);
        account0=this.findViewById(R.id.account0);
        email0=this.findViewById(R.id.email0);
        psw0=this.findViewById(R.id.psw0);
        repsw0=this.findViewById(R.id.repsw0);
    }
    private void initData() {
    }
    private void initListener() {
        productback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    public void register(){
        String uname=account0.getText().toString().trim();
        if (uname.equals("")){
            Toast.makeText(this,"账号不能为空！",Toast.LENGTH_LONG).show();
            return;
        }
        String email=email0.getText().toString().trim();
        if (email.equals("")){
            Toast.makeText(this,"Email不能为空！",Toast.LENGTH_LONG).show();
            return;
        }
        String psw=psw0.getText().toString().trim();
        if (psw.equals("")){
            Toast.makeText(this,"密码不能为空！",Toast.LENGTH_LONG).show();
            return;
        }
        String repsw=repsw0.getText().toString().trim();
        if (!repsw.equals(psw)){
            Toast.makeText(this,"两次密码不一致！",Toast.LENGTH_LONG).show();
            return;
        }
        EUser user=new EUser();
        user.setUname(uname);
        user.setUemail(email);
        user.setUpwd(psw);
        user.setUrepwd(repsw);
        user.setUid(0);
        user.setUlogo("");

        String url= Const.SERVER_URL+Const.SERVLET_URL+"addUser";
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
                Log.d("<<register返回：>>",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               String result=response.body().string();
               Log.d("register返回：",result);
               final EResult eResult=gson.fromJson(result,EResult.class);
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       if(eResult.getResult()==1){
                           Intent intent=new Intent(getApplicationContext(),ActivityLogin.class);
                           startActivity(intent);
                           finish();
                       }else {
                           Toast.makeText(ActivityRegisterNormal0.this,eResult.getMsg(),Toast.LENGTH_LONG).show();
                       }
                   }
               });
            }
        });
    }
}
