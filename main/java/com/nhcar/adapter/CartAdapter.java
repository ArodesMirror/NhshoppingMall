package com.nhcar.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorAdditionalInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nhcar.ActivityCart;
import com.nhcar.R;
import com.nhcar.entity.ECart;
import com.nhcar.entity.ECartItem;
import com.nhcar.entity.EResult;
import com.nhcar.utils.Const;

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

public class CartAdapter extends BaseAdapter {

    private OkHttpClient okHttpClient=new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5,TimeUnit.SECONDS)
            .build();
    private Gson gson=new Gson();
    private List<ECartItem> list=new ArrayList<ECartItem>();
    private ActivityCart context;
    private LayoutInflater layoutInflater;
    private int mNum;

    public CartAdapter(List<ECartItem> list, ActivityCart context) {
        this.list = list;
        this.context = context;
        Log.d("<<<listCart>>>",gson.toJson(list));
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
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder holder=new ViewHolder();
       layoutInflater=LayoutInflater.from(context);
       if (convertView==null){
           convertView=layoutInflater.inflate(R.layout.activity_cart_item,null);
           holder.image=convertView.findViewById(R.id.product_image);
           holder.title=convertView.findViewById(R.id.product_name);
           holder.price=convertView.findViewById(R.id.product_price);
           holder.num=convertView.findViewById(R.id.product_num);
           holder.minus=convertView.findViewById(R.id.cartminus);
           holder.add=convertView.findViewById(R.id.cartadd);
           holder.del=convertView.findViewById(R.id.cartdel);
           convertView.setTag(holder);
       }else {
           holder= (ViewHolder) convertView.getTag();
       }
       String picUrl= Const.SERVER_URL+Const.PIC_URL+list.get(position).getPpic();
       holder.image.setImageUrl(picUrl);
       holder.title.setText(list.get(position).getPname());
       holder.price.setText(list.get(position).getPprice()+"万元");
       holder.num.setText(list.get(position).getCnum()+"");
       MyClickListener listener=new MyClickListener(list.get(position));
       holder.minus.setOnClickListener(listener);
       holder.add.setOnClickListener(listener);
       holder.del.setOnClickListener(listener);
        return convertView;
    }
    class ViewHolder{
       SmartImageView image;
       TextView title;
       TextView price;
       TextView num;
       Button minus;
       Button add;
       Button del;
    }
    class MyClickListener implements  View.OnClickListener {
        private ECartItem eCartItem = new ECartItem();
        private int pid;
        private String pname = "";
        private SharedPreferences sp = context.getSharedPreferences("nhcarsp", Context.MODE_PRIVATE);
        int uid = sp.getInt("loginUserId", 0);

        public MyClickListener(ECartItem eCartItem) {
            super();
            this.eCartItem = eCartItem;
            pid = eCartItem.getPid();
            pname = eCartItem.getPname();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cartminus:
                    if (eCartItem.getCnum() > 1) {
                        mNum = -1;
                        addintoCart(pid);
                    }
                    break;
                case R.id.cartadd:
                    mNum = 1;
                    addintoCart(pid);
                    break;
                case R.id.cartdel:
                    delCart(pid);
                    break;
            }
        }

        private void addintoCart(int pid) {
            ECart eCart = new ECart();
            eCart.setCuid(uid);
            eCart.setCpid(pid);
            eCart.setCnum(mNum);
            if (eCart.getCuid() == 0) {
                Toast.makeText(context, "用户账号缺失，放入购车失败", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            if (pid == 0) {
                Toast.makeText(context, "用户商品id，放入购物车失败。", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            if (eCart.getCnum() == 0) {
                Toast.makeText(context, "购物车数量为０，放入购物车失败。", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            String cartStr = "";
            try {
                cartStr = gson.toJson(eCart);
                Log.d("----carStr-----", cartStr);
            } catch (Exception e) {
                Toast.makeText(context, "Json转换失败，注册不成功。", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            String url = Const.SERVER_URL + Const.SERVLET_URL + "addCart";
            String cartJson = gson.toJson(eCart);
            FormBody.Builder formBody = new FormBody.Builder();
            formBody.add("cartstr", cartJson);
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody.build())
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("addintoCart返回值：", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Log.d("additoCart返回值:", result);
                    final EResult eResult = gson.fromJson(result, EResult.class);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, eResult.getMsg(), Toast.LENGTH_LONG).show();
                            context.fillCartDetails();
                        }
                    });
                }
            });
        }

        private void delCart(int pid) {
            if (uid == 0) {
                Toast.makeText(context, "用户账号缺失，删除购物车商品失败。", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            if (pid == 0) {
                Toast.makeText(context, "商品id缺失，删除购物车商品失败。", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            String url = Const.SERVER_URL + Const.SERVLET_URL + "deleteCartByPid";
            FormBody.Builder formBody = new FormBody.Builder();
            formBody.add("pid", String.valueOf(pid));
            formBody.add("uid", String.valueOf(uid));
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody.build())
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("delCart返回值：", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Log.d("additoCart返回值:", result);
                    final EResult eResult = gson.fromJson(result, EResult.class);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, eResult.getMsg(), Toast.LENGTH_LONG).show();
                            context.fillCartDetails();
                        }
                    });
                }
            });
        }
    }
    }

