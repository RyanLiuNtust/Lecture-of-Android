package com.example.lectureofandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;

public class fragmentActivity extends FragmentActivity{

	public Fragment newInstance(int tag) {
		return null;
	}
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.fragment1);
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return super.onCreateView(name, context, attrs);
	}

}
