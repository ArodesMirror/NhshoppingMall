package com.nhcar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhcar.adapter.CartAdapter;
import com.nhcar.entity.ECartItem;
import com.nhcar.utils.Const;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityCart extends AppCompatActivity {
    private OkHttpClient okHttpClient=new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(3,TimeUnit.SECONDS)
            .build(); //第三方网络工具okhttp3对象
    private Gson gson =new Gson();//JSON工具对象
    private SharedPreferences sp;
    private LinearLayout cart_linear_notlogin,cart_linear_nonetwork,cart_linear_empty,cart_linear_notempty;
    private Button cart_login,cart_market,btnorder;
    private TextView tvcart,tvtotal;
    private ListView lvcart;
    private List<ECartItem> listCart=new ArrayList<ECartItem>();
    private CartAdapter adapter;
    private String uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        initView();
        initData();
        initListener();
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }
    private void initView() {
        cart_linear_notlogin=this.findViewById(R.id.cart_linear_notlogin);
        cart_linear_nonetwork=this.findViewById(R.id.cart_linear_nonetwork);
        cart_linear_empty=this.findViewById(R.id.cart_linear_empty);
        cart_linear_notempty=this.findViewById(R.id.cart_linear_notempty);
        cart_login=this.findViewById(R.id.cart_login);
        cart_market=this.findViewById(R.id.cart_market);
        btnorder=this.findViewById(R.id.btnorder);
        tvcart=this.findViewById(R.id.tvcart);
        lvcart=this.findViewById(R.id.lvcart);
        tvtotal=this.findViewById(R.id.tvtotal);
    }
    private void initData() {
        sp=this.getSharedPreferences("nhcarsp",MODE_PRIVATE);
        checkLogin();
    }
    private void initListener() {
        cart_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ActivityLogin.class);
                startActivity(intent);
            }
        });
        cart_market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ActivityIndex.class);
                startActivity(intent);
                finish();
            }
        });
        btnorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityCart.this,"正在调试中，目前暂不支持在线支付，只能货到付款。",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void checkLogin(){
        Boolean isLogin=sp.getBoolean("isLogin",false);
        uname=sp.getString("loginUserName","");
        if (isLogin){
            tvcart.setText(uname+"的购物车");
            fillCartDetails();
        }else {
            cart_linear_notlogin.setVisibility(View.VISIBLE);
            cart_linear_nonetwork.setVisibility(View.GONE);
            cart_linear_empty.setVisibility(View.GONE);
            cart_linear_notempty.setVisibility(View.GONE);
            tvcart.setText("购物车");
        }
    }
    public void  fillCartDetails(){
        String url= Const.SERVER_URL+Const.SERVLET_URL+"getCarListByUname";
        FormBody.Builder formBody=new FormBody.Builder();
        formBody.add("uname",uname);
        Request request=new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("loginUser返回值:",e.getMessage());
                cart_linear_notlogin.setVisibility(View.GONE);
                cart_linear_nonetwork.setVisibility(View.VISIBLE);
                cart_linear_empty.setVisibility(View.GONE);
                cart_linear_notempty.setVisibility(View.GONE);
                tvcart.setText(uname+"的购物车");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                Log.d("getCartListByUname返回值",result);
                listCart=gson.fromJson(result,new TypeToken<List<ECartItem>>(){
                }.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listCart.size()>0){
                            cart_linear_notlogin.setVisibility(View.GONE);
                            cart_linear_nonetwork.setVisibility(View.GONE);
                            cart_linear_empty.setVisibility(View.GONE);
                            cart_linear_notempty.setVisibility(View.VISIBLE);
                            tvcart.setText(uname+"的购物车");
                            adapter=new CartAdapter(listCart,ActivityCart.this);
                            lvcart.setAdapter(adapter);
                            float sum=0;
                            for(int i=0;i<listCart.size();i++){
                                sum+=listCart.get(i).getPprice()*listCart.get(i).getCnum();
                            }
                            tvtotal.setText(String.valueOf(sum)+"万元");
                        }else {
                            cart_linear_notlogin.setVisibility(View.GONE);
                            cart_linear_nonetwork.setVisibility(View.GONE);
                            cart_linear_empty.setVisibility(View.VISIBLE);
                            cart_linear_notempty.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}
