package com.nhcar.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nhcar.ActivityComment;
import com.nhcar.ActivityLogin;
import com.nhcar.R;
import com.nhcar.entity.ECarNews;
import com.nhcar.entity.ECart;
import com.nhcar.entity.EComment;
import com.nhcar.entity.EProduct;
import com.nhcar.entity.EResult;
import com.nhcar.utils.Const;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
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

public class ProductListAdapter extends BaseAdapter {
    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build();
    private Gson gson = new Gson();
    private SharedPreferences sp;
    private Activity context; //用于存放上下文
    private List<EProduct> list = new ArrayList<EProduct>();  //用于存放数据源
    private LayoutInflater layoutInflater;

    public ProductListAdapter(Activity context, List<EProduct> list) {
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
        ViewHolder holder = new ViewHolder();
        layoutInflater = LayoutInflater.from(context);
        sp = context.getSharedPreferences("nhcarsp", Context.MODE_PRIVATE);
        //如果视图不存在
        if (convertView == null) {
            //获取项布局
            convertView = layoutInflater.inflate(R.layout.activity_productlist_item, null);
            //获取项布局中的控件
            holder.smv = convertView.findViewById(R.id.productlist_image);
            holder.title = convertView.findViewById(R.id.productlist_title);
            holder.price = convertView.findViewById(R.id.productlist_price);
            holder.addToCart = (Button) convertView.findViewById(R.id.addToCart);
            holder.addToComment = (Button) convertView.findViewById(R.id.addToComment);
            holder.showComment = (Button) convertView.findViewById(R.id.showComment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //视图中控件的数据绑定
        String imgUrl = Const.SERVER_URL + Const.PIC_URL + list.get(position).getPpic();
        holder.smv.setImageUrl(imgUrl);
        holder.title.setText(list.get(position).getPname());
        holder.price.setText(list.get(position).getPprice() + "");
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pid = list.get(position).getPid();
                addToCart(pid);
            }
        });
        holder.addToComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pid = list.get(position).getPid();
                String pname = list.get(position).getPname();
                addToComment(pid, pname);
            }
        });
        holder.showComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp = context.getSharedPreferences("nhcarsp", Context.MODE_PRIVATE);
                Boolean isLogin = sp.getBoolean("isLogin", false);
                int uid = sp.getInt("loginUserId", 0);
                if (isLogin) {
                    Intent intent = new Intent(context, ActivityComment.class);
                    intent.putExtra("pid", list.get(position).getPid());
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ActivityLogin.class);
                    context.startActivity(intent);
                }
            }
        });
        return convertView;

        //ViewHolder类的成员与项布局中的控件一一对应：名称可以不同但控件类型必须相同
    }

    private void addToCart(int pid) {
        boolean isLogin = sp.getBoolean("isLogin", false);
        String uname = sp.getString("isLoginName", "");
        int uid = sp.getInt("loginUserId", 0);
        if (isLogin) {
            ECart eCart = new ECart();
            eCart.setCid(0);
            eCart.setCpid(pid);
            eCart.setCuid(uid);
            eCart.setCnum(1);
            String cartJson = gson.toJson(eCart);
            String url = Const.SERVER_URL + Const.SERVLET_URL + "addCart";
            Log.d("<<<放入购物车接口地址:>>>", url);
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
                    Log.d("addCart返回值：", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Log.d("addCart返回值：", result);
                    final EResult eResult = gson.fromJson(result, EResult.class);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, eResult.getMsg(), Toast.LENGTH_LONG).show();
                        }
                    });
                    Log.d("<<addCart结果>>", eResult.getMsg());
                }
            });
        } else {
            Intent intent = new Intent(context, ActivityLogin.class);
            context.startActivity(intent);
        }
    }

    private void addToComment(final int pid, String pname) {
        Boolean isLogin = sp.getBoolean("isLogin", false);
        final String loginuserName = sp.getString("loginUserName", "");
        if (!isLogin) {
            Intent intent = new Intent(context, ActivityLogin.class);
            context.startActivity(intent);
        } else {
            final EditText editText = new EditText(context);
            editText.setSingleLine(false);
            editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            editText.setGravity(Gravity.TOP);
            editText.setMinLines(3);
            editText.setMaxLines(5);
            editText.setHorizontallyScrolling(false);
            final AlertDialog.Builder editDialog = new AlertDialog.Builder(context);
            editDialog.setIcon(R.mipmap.ic_launcher_round);
            pname = pname.length() > 12 ? pname.substring(0, 12) + "..." : pname;
            editDialog.setTitle("评论" + pname).setView(editText);
            editDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String content = editText.getText().toString().trim();
                    if (content.isEmpty()) {
                        Toast.makeText(context, "输入的评论内容为空，评论失败！" + editText.getText().toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        EComment eComment = new EComment();
                        eComment.setCname(loginuserName);
                        eComment.setCpid(pid);
                        eComment.setCcontent(content);
                        addComment(eComment);
                    }
                }
            });
            editDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(context, "取消评论", Toast.LENGTH_SHORT).show();
                }
            });
            editDialog.show();
        }
    }

    private void addComment(EComment eComment) {
        String url = Const.SERVER_URL + Const.SERVLET_URL + "addComment";
        String commentJson = gson.toJson(eComment);
        String commentJsonCode = "";
        try {
            commentJsonCode = URLEncoder.encode(commentJson, "utf-8");
            Log.d("<<<commentJsonEncode>>>", commentJsonCode);
        } catch (Exception E) {
            Toast.makeText(context, "数据编码错误", Toast.LENGTH_SHORT).show();
        }
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("commentstr", commentJsonCode);
        Request request = new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("addComment返回值：", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.d("addComment返回值:", result);
                final EResult eResult = gson.fromJson(result, EResult.class);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, eResult.getMsg(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    class ViewHolder {
        SmartImageView smv;
        TextView title;
        TextView price;
        Button addToCart;
        Button addToComment;
        Button showComment;
    }
}
