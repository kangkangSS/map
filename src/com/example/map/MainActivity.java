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
		//��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
        SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_main);
		
		context=this.context;
		
		mMapView = (MapView) findViewById(R.id.bmapView); 
		mBitMap=mMapView.getMap();
		MapStatusUpdate factory=MapStatusUpdateFactory.zoomTo(15.0f);//������µ�ͼ�Ķ����������ŵȼ�
		mBitMap.setMapStatus(factory);//���õ�ͼ״̬
		
		initLocation();//��λ
		
	

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
	        	item.setTitle("ʵʱ��ͨ��ON��");
	        }else{
	        	mBitMap.setTrafficEnabled(true);
	        	item.setTitle("ʵʱ��ͨ��OFF��");
	        }
	        break;
        case R.id.action_mylocation:
        	centerToMyLocation();//��λ���ҵ�λ��
        	break;

		default:
			break;
		}
		return false;
		
	}

	private void centerToMyLocation() {
		LatLng latLng=new LatLng(mLatitude, mLongtitude);//��ȡ���Ⱥ�γ��
		MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latLng);//��λ
		mBitMap.animateMapStatus(msu);//��ͼʹ�ö�����Ч��
	//	Toast.makeText(getApplicationContext(), location2.getAddrStr(), Toast.LENGTH_SHORT).show();
	}
	@Override
	protected void onStart() {
		// TODO �Զ����ɵķ������
		super.onStart();
		mBitMap.setMyLocationEnabled(true);//������λ
		mLocationClient.start();
		myOrientationListener.start();
	}

	protected void onDestroy() {
		// TODO �Զ����ɵķ������
		super.onDestroy();
		//��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy(); 
	}
	
	@Override
	protected void onPause() {
		// TODO �Զ����ɵķ������
		super.onPause();
		//��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause(); 
	}
	
	@Override
	protected void onResume() {
		// TODO �Զ����ɵķ������
		super.onResume();
		//��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
	}
	protected void onStop() {
		// TODO �Զ����ɵķ������
		super.onStop();
		mBitMap.setMyLocationEnabled(false);//ֹͣ��λ
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
        //�����Զ���ͼ��
        MyLocationConfiguration config=new MyLocationConfiguration(com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL, true, mIconLocation);
        mBitMap.setMyLocationConfigeration(config);
        //ÿ�ζ�λ�ɹ������һ�¾�γ��
        mLatitude=location.getLatitude();
        mLongtitude=location.getLongitude();
        //���ǵ�һ�ζ��壬��λ���û���ǰλ��
        if(isFirstIn){
        	
        	LatLng latLng=new LatLng(location.getLatitude(), location.getLongitude());//��ȡ���Ⱥ�γ��
        	MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latLng);//��λ
        	mBitMap.animateMapStatus(msu);//��ͼʹ�ö�����Ч��
        	isFirstIn=false;
        	Toast.makeText(getApplicationContext(), location.getAddrStr(), Toast.LENGTH_SHORT).show();
        }
        		
			
		}
		
	}
}
