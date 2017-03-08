package com.workspace.bin.wheelviewdemo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.workspace.bin.wheelviewdemo.datepicker.OnWheelChangedListener;
import com.workspace.bin.wheelviewdemo.datepicker.OnWheelScrollListener;
import com.workspace.bin.wheelviewdemo.datepicker.WheelView;


import org.json.JSONObject;

import java.util.Calendar;

/*
 * 二级联动 自定义弹出框
 */
public class CustomCitiesDialog extends Dialog implements android.view.View.OnClickListener{

	private boolean scrolling = false;//是否正在滑动
	private int mActiveCities[] = new int[] { 0, 1, 1, 1 };//保存年对应的月选中的下标
	private int mActiveCountry;//记录年选中下标
	private String countries[] = new String[] { "0", "1", "2","3" };//年
	private String cities[][] = new String[][] {
			new String[] { "1", "2", "3", "4", "5","6","7","8","9","10","11" },
			new String[] { "0","1", "2", "3", "4", "5","6","7","8","9","10","11" },
			new String[] { "0","1", "2", "3", "4", "5","6","7","8","9","10","11" },
			new String[] { "0" }};
	
	private NumericWheelAdapter countryAdapter;//年适配器
	private NumericWheelAdapter cityAdapter;//月适配器
	private WheelView country;//年控件
	private WheelView city;//月控件
	private Button btnCancel;//取消按钮
	private Button btnConfirm;//确定按钮
	
	private Context mContext;
	private ConfirmCustomDialogInterface mConfirmCustomDialogInterface;//自定义接口
	private boolean isType = false;//false 参数为范围值  true 参数为集合
	private String obj;//参数为范围值的json字符串
	private String selectYear;
	private String selectMonth;
	private String id;
	private int minYear = 0;
	private int maxYear = 0;
	
	public CustomCitiesDialog(Context mContext, ConfirmCustomDialogInterface mConfirmCustomDialogInterface){
		super(mContext,R.style.customList);
		this.mContext = mContext;
		this.mConfirmCustomDialogInterface = mConfirmCustomDialogInterface;
		isType = true;
	}
	
	public CustomCitiesDialog(Context mContext, String[] countries, String[][] cities, ConfirmCustomDialogInterface mConfirmCustomDialogInterface){
		super(mContext,R.style.customList);
		this.countries = countries;
		this.cities = cities;
		this.mConfirmCustomDialogInterface = mConfirmCustomDialogInterface;
		isType = true;
	}
	
	public CustomCitiesDialog(Context mContext, String obj, ConfirmCustomDialogInterface mConfirmCustomDialogInterface){
		super(mContext,R.style.customList);
		this.mContext = mContext;
		this.mConfirmCustomDialogInterface = mConfirmCustomDialogInterface;
		isType = false;
		this.obj = obj;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repopulating_data);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		country = (WheelView) findViewById(R.id.country);
		city = (WheelView) findViewById(R.id.city);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		
		btnCancel.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
	}

	private void initData() {
		// TODO Auto-generated method stub
		if(isType){
			countryAdapter = new NumericWheelAdapter(mContext, countries);
			countryAdapter.setLabel("年");//设置单位
			countryAdapter.setWheel(country);
			country.setCyclic(false);
			country.setWheelForeground(R.color.transparent);
			country.setViewAdapter(countryAdapter);
			country.addChangingListener(new OnWheelChangedListener() {

				@Override
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					// TODO Auto-generated method stub
					if (!scrolling) {
						updateCities(city, cities, newValue);
					}
				}
			});
			country.addScrollingListener(new OnWheelScrollListener() {
				@Override
				public void onScrollingStarted(WheelView wheel) {
					// TODO Auto-generated method stub
					scrolling = true;
				}

				@Override
				public void onScrollingFinished(WheelView wheel) {
					// TODO Auto-generated method stub
					scrolling = false;
					updateCities(city, cities, country.getCurrentItem());
				}
			});
			country.setVisibleItems(3);
			country.setCurrentItem(0);

			if(cityAdapter == null){
				cityAdapter = new NumericWheelAdapter(mContext, cities[0]);
			}
			cityAdapter.setWheel(city);
			cityAdapter.setList(cities[0]);
			cityAdapter.setLabel("月");
			city.setViewAdapter(cityAdapter);
			city.setCurrentItem(0);
			city.setVisibleItems(3);
			city.setCyclic(false);
			city.addChangingListener(new OnWheelChangedListener() {
				@Override
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					// TODO Auto-generated method stub
					Log.d("TAG", "addChangingListener===city==" + newValue);
					if (!scrolling) {
						mActiveCities[mActiveCountry] = newValue;
					}
				}
			});
		}else{
			Calendar c = Calendar.getInstance();
			int nowYear = c.get(Calendar.YEAR);
			int nowMonth = c.get(Calendar.MONTH)+1;
			try {
				JSONObject json = new JSONObject(obj);
				minYear = Integer.parseInt(json.getString("minYear"));
				maxYear = Integer.parseInt(json.getString("maxYear"));
				id = json.getString("id");
				if(minYear == maxYear){
					selectYear = minYear +"";
				}else{
					if(minYear < nowYear) {
						selectYear = nowYear + "";
					}else{
						selectYear = minYear + "";
					}
				}
				selectMonth = nowMonth+"";
			} catch (Exception e) {
				// TODO: handle exception
			}
			countryAdapter = new NumericWheelAdapter(mContext, minYear,maxYear);
			countryAdapter.setWheel(country);
			countryAdapter.setLabel("年");//设置单位
			country.setCyclic(false);
			country.setViewAdapter(countryAdapter);
			country.setVisibleItems(3);

			country.setCurrentItem(nowYear - minYear);
			country.addChangingListener(new OnWheelChangedListener() {

				@Override
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					// TODO Auto-generated method stub

					selectYear = newValue+minYear+"";

				}
			});
			
			cityAdapter = new NumericWheelAdapter(mContext, 1,12);
			cityAdapter.setWheel(city);
			cityAdapter.setLabel("月");
			city.setViewAdapter(cityAdapter);
			city.setVisibleItems(3);
			city.setCyclic(false);
			city.setCurrentItem(nowMonth);
			city.addChangingListener(new OnWheelChangedListener() {
				@Override
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					// TODO Auto-generated method stub

					selectMonth = newValue+"";
				}
			});
		}
	}

	/**
	 * 更新月份数据
	 */
	private void updateCities(WheelView city, String cities[][], int index) {
		mActiveCountry = index;
		if(cityAdapter == null){
			cityAdapter = new NumericWheelAdapter(mContext, cities[index]);
		}
		cityAdapter.setWheel(city);
		cityAdapter.setList(cities[index]);
		cityAdapter.setLabel("月");
		city.setViewAdapter(cityAdapter);
		city.setCurrentItem(mActiveCities[index]);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnCancel:
			dismiss();
			break;
		case R.id.btnConfirm:
			if(isType){
				String currentCountry = countries[mActiveCountry];//年选中
				Log.d("TAG", "currentCountry=="+currentCountry);
				String currentCity = "";
				if("3".equals(currentCountry)){
					currentCity = "0";
				}else{
					currentCity = cities[mActiveCountry][mActiveCities[mActiveCountry]];//月选中
				}
				Log.d("TAG", "currentCity=="+currentCity);
				String sumMonth = Integer.parseInt(currentCountry)*12 + Integer.parseInt(currentCity) + "";
				if(mConfirmCustomDialogInterface != null){
					mConfirmCustomDialogInterface.confirmResult(sumMonth);
				}
			}else{
				if(mConfirmCustomDialogInterface != null){
					mConfirmCustomDialogInterface.confirmResult(selectYear+","+selectMonth+","+id);
				}
			}
			dismiss();
			break;
		default:
			break;
		}
	}
	
	public interface ConfirmCustomDialogInterface{
		public void confirmResult(String sumMonth);
	}
}
