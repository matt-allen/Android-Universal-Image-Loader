package com.nostra13.universalimageloader.sample.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nostra13.universalimageloader.sample.R;
import com.nostra13.universalimageloader.sample.fragment.fallback.FallbackTestPagerFragment;

/**
 * Created by Matt Allen
 * 08/08/15
 * mattallen092@gmail.com
 */
public class FallbackTestActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_holder);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment_holder, new FallbackTestPagerFragment())
				.commit();
	}
}
