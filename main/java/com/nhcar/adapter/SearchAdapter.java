package com.nhcar.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nhcar.ActivityLogin;
import com.nhcar.R;
import com.nhcar.entity.ECarNews;
import com.nhcar.entity.ECart;
import com.nhcar.entity.EProduct;
import com.nhcar.entity.EResult;
import com.nhcar.utils.Const;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.concurrent.TimeUnit;

import loopj.android.image.SmartImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchAdapter extends BaseAdapter {
    private OkHttpClient okHttpClient=new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5,TimeUnit.SECONDS)
            .build();
    private Gson gson=new Gson();
    private SharedPreferences sp;
    private Activity context; //用于存放上下文
    private List<EProduct> list= new ArrayList<EProduct>();  //用于存放数据源
    private LayoutInflater layoutInflater;

    public SearchAdapter(Activity context, List<EProduct> list) {
        this.context = context;
        this.list = list;
    }

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
        ViewHolder holder=new ViewHolder();
        layoutInflater=LayoutInflater.from(context);
        sp=context.getSharedPreferences("nhcarsp",Context.MODE_PRIVATE);
        //如果视图不存在
        if(convertView==null){
            //获取项布局
            convertView=layoutInflater.inflate(R.layout.activity_searchcar_item ,null);
            //获取项布局中的控件
            holder.smv=convertView.findViewById(R.id.productlist_image);
            holder.title=convertView.findViewById(R.id.productlist_title);
            holder.price=convertView.findViewById(R.id.productlist_price);
            holder.addToCart=(Button) convertView.findViewById(R.id.addToCart);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        //视图中控件的数据绑定
        String imgUrl= Const.SERVER_URL+Const.PIC_URL+list.get(position).getPpic();
        holder.smv.setImageUrl(imgUrl);
        holder.title.setText(list.get(position).getPname());
        holder.price.setText(list.get(position).getPprice()+"");
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLogin=sp.getBoolean("isLogin",false);
                String uname=sp.getString("isLoginName","");
                int uid=sp.getInt("loginUserId",0);
                if (isLogin){
                    ECart eCart=new ECart();
                    eCart.setCid(0);
                    eCart.setCpid(list.get(position).getPid());
                    eCart.setCuid(uid);
                    eCart.setCnum(1);
                    String cartJson=gson.toJson(eCart);
                    String url=Const.SERVER_URL+Const.SERVLET_URL+"addCart";
                    Log.d("<<<放入购物车接口地址:>>>",url);
                    FormBody.Builder formBody=new FormBody.Builder();
                    formBody.add("cartstr",cartJson);
                    Request request=new Request.Builder()
                            .url(url)
                            .post(formBody.build())
                            .build();
                    Call call=okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("addCart返回值：",e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                            Log.d("addCart返回值：",result);
                            final EResult eResult=gson.fromJson(result,EResult.class);
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,eResult.getMsg(),Toast.LENGTH_LONG).show();
                                }
                            });
                            Log.d("<<addCart结果>>",eResult.getMsg());
                        }
                    });
                }else {
                    Intent intent=new Intent(context, ActivityLogin.class);
                    context.startActivity(intent);
                }
            }
        });
        return convertView;

        //ViewHolder类的成员与项布局中的控件一一对应：名称可以不同但控件类型必须相同
    }
    class ViewHolder{
        SmartImageView smv;
        TextView title;
        TextView price;
        Button addToCart;
    }
}
