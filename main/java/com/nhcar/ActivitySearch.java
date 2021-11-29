package com.nhcar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.nhcar.adapter.ProductListAdapter;
import com.nhcar.adapter.SearchAdapter;
import com.nhcar.entity.EProduct;
import com.nhcar.entity.EProductListResult;
import com.nhcar.utils.Const;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivitySearch extends AppCompatActivity {
    private OkHttpClient okHttpClient=new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(3,TimeUnit.SECONDS)
            .build(); //第三方网络工具okhttp3对象
    private Gson gson =new Gson();//JSON工具对象
    private EditText edt;
    private ListView productlist_listview;
    private Button btnmore,btnsearch,productback1;
    private int pageNo=0;
    private List<EProduct> listproduct=new ArrayList<EProduct>();
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initData();
        initListener();
    }
    private void initView() {
        productback1=this.findViewById(R.id.productback1);
        edt=this.findViewById(R.id.edt);
        btnsearch=this.findViewById(R.id.btnsearch);
        btnmore=this.findViewById(R.id.btnmore);
        productlist_listview=this.findViewById(R.id.productlist_listview);
    }
    private void initData() {
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadProductList();
            }
        });

    }
    private void initListener() {
        productback1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=getIntent();
                int cid=intent.getIntExtra("cid",0);
                loadMoreProductList(cid);
            }
        });
    }
    private void loadProductList(){
        String url = Const.SERVER_URL+Const.SERVLET_URL+"searchProductList";
        String searchText=edt.getText().toString().trim();
        pageNo=1;
        Log.d("<<<>>>>",url);
        FormBody.Builder formBody=new FormBody.Builder();   //表单参数对象
        String userJsonEncode="";
        try {
            userJsonEncode=URLEncoder.encode(searchText,"utf-8");
            Log.d("<<<userJsonEncode>>>",userJsonEncode);
        }catch (Exception e){
            Toast.makeText(this,"数据编码错误",Toast.LENGTH_SHORT).show();
            return;
        }
        formBody.add("key",searchText);
        formBody.add("pageno",String.valueOf(pageNo));
        formBody.add("pagesize","6");//参数1，还可以添加更多参数

        Request request=new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.d("loadProductList返回结果：",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();//接收接口返回的内
                Log.d("loadCarNews返回：", result);

                //将接收返回的JSON数据还原为LIST<E>
                EProductListResult eProductListResult=gson.fromJson(result,EProductListResult.class);
                listproduct=eProductListResult.getDataResult();
                Log.d("<<listproduct>>",listproduct.size()+"");
                Log.d("<<listproduct>>>",gson.toJson(listproduct));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //此处更新UI：数据绑定到GridView
                        adapter = new SearchAdapter(ActivitySearch.this, listproduct);
                        productlist_listview.setAdapter(adapter);
                    }
                });
            }
        });
    }
    private void loadMoreProductList(int cid){
        String url = Const.SERVER_URL+Const.SERVLET_URL+"searchProductList";
        String searchText=edt.getText().toString().trim();
        pageNo++;
        Log.d("<<<>>>>",url);
        FormBody.Builder formBody=new FormBody.Builder();   //表单参数对象
        String userJsonEncode="";
        try {
            userJsonEncode=URLEncoder.encode(searchText,"utf-8");
            Log.d("<<<userJsonEncode>>>",userJsonEncode);
        }catch (Exception e){
            Toast.makeText(this,"数据编码错误",Toast.LENGTH_SHORT).show();
            return;
        }
        formBody.add("key",searchText);
        formBody.add("pageno",String.valueOf(pageNo));
        formBody.add("pagesize","6");//参数1，还可以添加更多参数

        Request request=new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.d("loadProductList返回结果：",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();//接收接口返回的内
                Log.d("loadCarNews返回：", result);

                //将接收返回的JSON数据还原为LIST<E>
                EProductListResult eProductListResult=gson.fromJson(result,EProductListResult.class);
                listproduct.addAll(eProductListResult.getDataResult());
                Log.d("<<listproduct>>",listproduct.size()+"");
                Log.d("<<listproduct>>>",gson.toJson(listproduct));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //此处更新UI：数据绑定到GridView
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    }