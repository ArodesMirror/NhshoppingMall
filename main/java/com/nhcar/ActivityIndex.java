package com.nhcar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhcar.adapter.CarNewsAdapter;
import com.nhcar.adapter.CatAdapter;
import com.nhcar.adapter.SummerAdapter;
import com.nhcar.entity.ECarNews;
import com.nhcar.entity.ECarNewResult;
import com.nhcar.entity.ECategory;
import com.nhcar.entity.EProduct;
import com.nhcar.utils.Const;

import org.dkn.view.ImageCycleView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import loopj.android.image.SmartImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityIndex extends AppCompatActivity {
    // 声明控件、对象


   private OkHttpClient okHttpClient=new OkHttpClient.Builder()
           .connectTimeout(5, TimeUnit.SECONDS)
           .readTimeout(3,TimeUnit.SECONDS)
           .build(); //第三方网络工具okhttp3对象
    private Gson gson =new Gson();//JSON工具对象

    private ImageCycleView icv_topView;//轮播控件
    private GridView grvfunctionlist;//热销品牌网格控件
    private Gallery index_jingqiu_gallery; //夏季风暴画廊控件
    private ListView lvcarnews;

    private List<ECategory> listCat=new ArrayList<ECategory>();//存放热销品牌数据
    private List<EProduct> listSummmer=new ArrayList<EProduct>();//存放夏季风暴数据
    private List<ECarNews>listCarNews= new ArrayList<ECarNews>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_index);

        initView();
        initData();
        initLisener();


    }// onCreate End
    private void initView(){
        icv_topView=this.findViewById(R.id.icv_topView);
        grvfunctionlist=this.findViewById(R.id.grvfunctionlist);
        index_jingqiu_gallery=this.findViewById(R.id.index_jingqiu_gallery);
        lvcarnews=this.findViewById(R.id.lvcarnews);
    }
    private void initData(){
        //给轮播转载图片
        //自定义图片方法
        loadImages();
        loadHotCategory();
        loadSummerProduct();
        loadCarNews();
    }
    private void initLisener() {
       grvfunctionlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent=new Intent(getApplicationContext(),ActivityPoductList0.class);
               int cid=listCat.get(position).getCid();
               intent.putExtra("cid",cid);
               startActivity(intent);
           }
       });
        index_jingqiu_gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ActivityIndex.this, "你点击的是"+listSummmer.get(position).getPname(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //用自定义方法用于给轮播控件转载图片
    private void loadImages(){
        //第一步：准备好图片并载入一个List<>集合中
        List<ImageCycleView.ImageInfo> list=new ArrayList<ImageCycleView.ImageInfo>();
        //本地图片
        /*
        list.add(new ImageCycleView.ImageInfo(R.drawable.ad1,"",""));
        list.add(new ImageCycleView.ImageInfo(R.drawable.ad2,"",""));
        list.add(new ImageCycleView.ImageInfo(R.drawable.ad3,"",""));
        list.add(new ImageCycleView.ImageInfo(R.drawable.ad5,"",""));
        list.add(new ImageCycleView.ImageInfo(R.drawable.ad6,"",""));
        */

        //网络图片
        String imgUrl="http://110.64.48.115:8080/uploads/";
        list.add(new ImageCycleView.ImageInfo(imgUrl+"ad1.jpg","",""));
        list.add(new ImageCycleView.ImageInfo(imgUrl+"ad2.jpg","",""));
        list.add(new ImageCycleView.ImageInfo(imgUrl+"ad3.jpg","",""));

        //第二步：在轮播控件中装入图片
        icv_topView.loadData(list, new ImageCycleView.LoadImageCallBack() {
            @Override
            public ImageView loadAndDisplay(ImageCycleView.ImageInfo imageInfo) {

                //装载网络图片
                SmartImageView smv=new SmartImageView(getApplicationContext());
                smv.setImageUrl(imageInfo.image.toString());
                return smv;
            }
        });
    }

    private void loadHotCategory(){
       String url = Const.SERVER_URL+Const.SERVLET_URL+"getAllCategory";
        Log.d("<<<<>>>>",url);
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
                        CatAdapter catAdapter=new CatAdapter(getApplicationContext(),listCat);
                        grvfunctionlist.setAdapter(catAdapter);
                    }
                });
            }
        });
    }

    private void loadSummerProduct(){
        String url = Const.SERVER_URL+Const.SERVLET_URL+"getproductListByType";
        Log.d("<<<<>>>>",url);

        FormBody.Builder formBody=new FormBody.Builder();   //表单参数对象
        formBody.add("type","3");     //参数1，还可以添加更多参数

        Request request=new Request.Builder()
                .url(url)
                .post(formBody.build())
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
                Log.d("getproductListByType返回：",result);

                //将接收返回的JSON数据还原为LIST<E>
                listSummmer=gson.fromJson(result,new TypeToken<List<EProduct>>(){}.getType());
                Log.d("<<<listSummer行数>>>",listSummmer.size()+"");
                //在UI线程中更新
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //此处更新UI：数据绑定到GridView
                        SummerAdapter adapter=new SummerAdapter(getApplicationContext(),listSummmer);
                        index_jingqiu_gallery.setAdapter(adapter);
                        index_jingqiu_gallery.setSelection(2);
                    }
                });
            }
        });
    }

    private void loadCarNews(){
        String url = Const.SERVER_URL+Const.SERVLET_URL+"getNewsListByPageNo";
        Log.d("<<<<>>>>",url);

        FormBody.Builder formBody=new FormBody.Builder();   //表单参数对象
        formBody.add("pageno","1");
        formBody.add("pagesize","6");//参数1，还可以添加更多参数

        Request request=new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.d("loadCarNews返回结果：",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result= response.body().string();//接收接口返回的内
                Log.d("loadCarNews返回：",result);

                //将接收返回的JSON数据还原为LIST<E>
                ECarNewResult eCarNewResult=gson.fromJson(result,ECarNewResult.class);
                listCarNews=eCarNewResult.getDataResult();
                Log.d("<<listCarNews行数>>",listCarNews.size()+"");
                Log.d("<<listCarNews>>>",gson.toJson(listCarNews));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //此处更新UI：数据绑定到GridView
                        CarNewsAdapter adapter=new CarNewsAdapter(getApplicationContext(),listCarNews);
                        lvcarnews.setAdapter(adapter);
                    }
                });
            }
        });
    }

}

