package com.example.map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.example.map.MyOrientationListener.OnOrientationListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private MapView mMapView = null;
	private BaiduMap mBitMap;
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;
	private BDLocation location2;
	private boolean isFirstIn=true;
	
	private Context context;
	private double mLatitude;
	private double mLongtitude;
	
	private BitmapDescriptor mIconLocation;
	private MyLocationData myLocationOverlay;
	private MyOrientationListener myOrientationListener;
    private float mCurrentX;
//	public MapController mMapCtrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	//	requestWindowFeature(Window.FEATURE_NO_TITLE);
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_main);
		
		context=this.context;
		
		mMapView = (MapView) findViewById(R.id.bmapView); 
		mBitMap=mMapView.getMap();
		MapStatusUpdate factory=MapStatusUpdateFactory.zoomTo(15.0f);//构造更新地图的对象，设置缩放等级
		mBitMap.setMapStatus(factory);//设置地图状态
		
		initLocation();//定位
		
	

	}

	private void initLocation() {

		mLocationClient = new LocationClient(this);
		mLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mLocationListener);

		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
	
	mIconLocation=BitmapDescriptorFactory.fromResource(R.drawable.map2);
	myOrientationListener=new MyOrientationListener(context);
	myOrientationListener.setOnOrientationListener(new OnOrientationListener(){
		public void onOrientationChanged(float x){
			mCurrentX=x;
		}
	});
   }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_common:
			mBitMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;
        case R.id.action_size:
			mBitMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;
        case R.id.action_traffic:
	        if(mBitMap.isTrafficEnabled()){
	        	mBitMap.setTrafficEnabled(false);
	        	item.setTitle("实时交通（ON）");
	        }else{
	        	mBitMap.setTrafficEnabled(true);
	        	item.setTitle("实时交通（OFF）");
	        }
	        break;
        case R.id.action_mylocation:
        	centerToMyLocation();//定位到我的位置
        	break;

		default:
			break;
		}
		return false;
		
	}

	private void centerToMyLocation() {
		LatLng latLng=new LatLng(mLatitude, mLongtitude);//获取经度和纬度
		MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latLng);//定位
		mBitMap.animateMapStatus(msu);//地图使用动画的效果
	//	Toast.makeText(getApplicationContext(), location2.getAddrStr(), Toast.LENGTH_SHORT).show();
	}
	@Override
	protected void onStart() {
		// TODO 自动生成的方法存根
		super.onStart();
		mBitMap.setMyLocationEnabled(true);//开启定位
		mLocationClient.start();
		myOrientationListener.start();
	}

	protected void onDestroy() {
		// TODO 自动生成的方法存根
		super.onDestroy();
		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy(); 
	}
	
	@Override
	protected void onPause() {
		// TODO 自动生成的方法存根
		super.onPause();
		//在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause(); 
	}
	
	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();
		//在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
	}
	protected void onStop() {
		// TODO 自动生成的方法存根
		super.onStop();
		mBitMap.setMyLocationEnabled(false);//停止定位
		mLocationClient.stop();
		myOrientationListener.stop();
	}
	
	private class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {

        MyLocationData data=new MyLocationData.Builder()
                .direction(mCurrentX)
                .accuracy(location.getRadius())
        		.latitude(location.getLatitude())
        		.longitude(location.getLongitude()).build();
        mBitMap.setMyLocationData(data);
        //设置自定义图标
        MyLocationConfiguration config=new MyLocationConfiguration(com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL, true, mIconLocation);
        mBitMap.setMyLocationConfigeration(config);
        //每次定位成功后更新一下经纬度
        mLatitude=location.getLatitude();
        mLongtitude=location.getLongitude();
        //若是第一次定义，则定位到用户当前位置
        if(isFirstIn){
        	
        	LatLng latLng=new LatLng(location.getLatitude(), location.getLongitude());//获取经度和纬度
        	MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latLng);//定位
        	mBitMap.animateMapStatus(msu);//地图使用动画的效果
        	isFirstIn=false;
        	Toast.makeText(getApplicationContext(), location.getAddrStr(), Toast.LENGTH_SHORT).show();
        }
        		
			
		}
		
	}
}
