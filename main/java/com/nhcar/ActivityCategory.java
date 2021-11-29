package com.nhcar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhcar.adapter.CatAdapter;
import com.nhcar.adapter.CatAdapter1;
import com.nhcar.entity.ECategory;
import com.nhcar.utils.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityCategory extends AppCompatActivity {
    private OkHttpClient okHttpClient=new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(3,TimeUnit.SECONDS)
            .build(); //第三方网络工具okhttp3对象
    private Gson gson =new Gson();//JSON工具对象
    private ListView categorylist_listView;
    private List<ECategory>listCat=new ArrayList<ECategory>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        categorylist_listView=this.findViewById(R.id.categorylist_listview);
    }
    private void initData() {
        loadCategory();
    }
    private void initListener() {

       categorylist_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),ActivityPoductList0.class);
                intent.putExtra("cid",listCat.get(position).getCid());
                startActivity(intent);
            }
        });
    }

    private void loadCategory(){
        String url = Const.SERVER_URL+Const.SERVLET_URL+"getAllCategory";
        Log.d("<<<<loadCategory>>>>",url);
        Request request=new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("网络访问返回结果：",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result= response.body().string();//接收接口返回的内容
                Log.d("网络访问返回结果：",result);

                //将接收返回的JSON数据还原为LIST<E>
                listCat=gson.fromJson(result,new TypeToken<List<ECategory>>(){}.getType());
                Log.d("<<<Json 还原为List后的行数>>>",listCat.size()+"");
                //在UI线程中更新
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //此处更新UI：数据绑定到GridView
                        CatAdapter1 catAdapter1=new CatAdapter1(getApplicationContext(),listCat);
                        categorylist_listView.setAdapter(catAdapter1);
                    }
                });
            }
        });
    }
}
