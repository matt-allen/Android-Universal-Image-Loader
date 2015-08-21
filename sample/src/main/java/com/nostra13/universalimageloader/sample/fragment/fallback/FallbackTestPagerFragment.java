package com.nostra13.universalimageloader.sample.fragment.fallback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.sample.R;

/**
 * Created by Matt Allen
 * 16/08/15
 * mattallen092@gmail.com
 */
public class FallbackTestPagerFragment extends Fragment
{
	private ViewPager mViewPager;
	private TabLayout mTabLayout;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_viewpager, container, false);
		mViewPager = (ViewPager)view.findViewById(R.id.view_pager);
		mTabLayout = (TabLayout)view.findViewById(R.id.tab_layout);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		mViewPager.setAdapter(getAdapter());
		mTabLayout.setupWithViewPager(mViewPager);
	}

	protected FragmentPagerAdapter getAdapter()
	{
		return new FallbackTestPagerAdapter(getChildFragmentManager());
	}
}
