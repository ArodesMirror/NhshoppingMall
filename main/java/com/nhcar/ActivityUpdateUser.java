package com.nhcar;

import android.content.SharedPreferences;
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

public class ActivityUpdateUser extends AppCompatActivity {
    private OkHttpClient okHttpClient=new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(3,TimeUnit.SECONDS)
            .build(); //第三方网络工具okhttp3对象
    private Gson gson =new Gson();//JSON工具对象
    private SharedPreferences sp;
    private EUser user;
    private Button alter,productback1;
    private EditText account1,email1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateuser);
        initView();
        initData();
        initListener();
    }
    private void initView() {
        alter=this.findViewById(R.id.alter);
        account1=this.findViewById(R.id.account1);
        email1=this.findViewById(R.id.emai11);
        productback1=this.findViewById(R.id.productback1);
    }
    private void initData() {
        sp=this.getSharedPreferences("nhcarsp",MODE_PRIVATE);
        getAndShowMyInfo();
    }
    private void initListener() {
        productback1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
      alter.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              updateuser();
          }
      });
    }


    public void updateuser(){

        String email=email1.getText().toString().trim();
        if (email.equals("")){
            Toast.makeText(this,"Email不能为空！",Toast.LENGTH_LONG).show();
            return;
        }

        user.setUemail(email);

        String url= Const.SERVER_URL+Const.SERVLET_URL+"updateUsers";
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
                Log.d("<<UpdateUser返回：>>",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               String result=response.body().string();
               Log.d("UpdateUser返回：",result);
               final EResult eResult=gson.fromJson(result,EResult.class);
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       if(eResult.getResult()==1){
                           Toast.makeText(ActivityUpdateUser.this, "修改我的信息成功", Toast.LENGTH_SHORT).show();
                       }else {
                           Toast.makeText(ActivityUpdateUser.this,eResult.getMsg(),Toast.LENGTH_LONG).show();
                       }

                   }
               });
            }
        });
    }
    private void getAndShowMyInfo(){
        Boolean isLogin=sp.getBoolean("isLogin",false);
        int uid=sp.getInt("loginUserId",0);
        if (!isLogin||uid<0){
            Toast.makeText(this,"请先登录。",Toast.LENGTH_LONG).show();
            return;
        }
        String url= Const.SERVER_URL+Const.SERVLET_URL+"getUserInfoByUid";


        FormBody.Builder formBody=new FormBody.Builder();
        formBody.add("uid",String.valueOf(uid));
        Request request=new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("<<getAndShowMyInfo返回：>>",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                Log.d("getAndShowMyInfo返回：",result);
                user=gson.fromJson(result,EUser.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(user!=null){
                            account1.setText(user.getUname());
                            account1.setKeyListener(null);
                            email1.setText(user.getUemail());

                        }else {
                            Toast.makeText(ActivityUpdateUser.this,"当前用户信息无法获取",Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });
    }
}
