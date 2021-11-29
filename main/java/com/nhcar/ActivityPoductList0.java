package com.nhcar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.nhcar.adapter.CarNewsAdapter;
import com.nhcar.adapter.ProductListAdapter;
import com.nhcar.entity.ECarNewResult;
import com.nhcar.entity.ECategory;
import com.nhcar.entity.EProduct;
import com.nhcar.entity.EProductListResult;
import com.nhcar.utils.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityPoductList0 extends AppCompatActivity {
	private OkHttpClient okHttpClient=new OkHttpClient.Builder()
			.connectTimeout(5, TimeUnit.SECONDS)
			.readTimeout(3,TimeUnit.SECONDS)
			.build(); //第三方网络工具okhttp3对象
	private Gson gson =new Gson();//JSON工具对象
	private ListView product_listview;
	private Button productback,btnmore;
	private int pageNo=0;
	private List<EProduct> listproduct=new ArrayList<EProduct>();
	private ProductListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_productlist0);

		initView();
		initData();
		initListener();

	}// onCreate End

	private void initView() {

		product_listview=this.findViewById(R.id.productlist_listview);
		productback=this.findViewById(R.id.productback);
		btnmore=this.findViewById(R.id.btnmore);
	}

	private void initData() {
		Intent intent=getIntent();
		int cid=intent.getIntExtra("cid",0);
		loadProductList(cid);

	}

	private void initListener() {
       productback.setOnClickListener(new View.OnClickListener() {
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
	private void loadProductList(int cid){
		String url = Const.SERVER_URL+Const.SERVLET_URL+"getproductListByCid";
		pageNo=1;

		FormBody.Builder formBody=new FormBody.Builder();   //表单参数对象
		formBody.add("cid",String.valueOf(cid));
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
				String result= response.body().string();//接收接口返回的内
				Log.d("loadCarNews返回：",result);

				//将接收返回的JSON数据还原为LIST<E>
				EProductListResult eProductListResult=gson.fromJson(result,EProductListResult.class);
				listproduct=eProductListResult.getDataResult();
				Log.d("<<listproduct>>",listproduct.size()+"");
				Log.d("<<listproduct>>>",gson.toJson(listproduct));
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//此处更新UI：数据绑定到GridView
						adapter=new ProductListAdapter(ActivityPoductList0.this,listproduct);
						product_listview.setAdapter(adapter);
					}
				});
			}
		});
	}

	private void loadMoreProductList(int cid){
		String url = Const.SERVER_URL+Const.SERVLET_URL+"getproductListByCid";
		pageNo++;

		FormBody.Builder formBody=new FormBody.Builder();   //表单参数对象
		formBody.add("cid",String.valueOf(cid));
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

				Log.d("loadMoreProductList返回：",e.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String result= response.body().string();//接收接口返回的内
				Log.d("loadMoreProductList返回：",result);

				//将接收返回的JSON数据还原为LIST<E>
				EProductListResult eProductListResult=gson.fromJson(result,EProductListResult.class);
				listproduct.addAll(eProductListResult.getDataResult());
				Log.d("<loadMoreProductList行数>",listproduct.size()+"");

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
