package com.nostra13.universalimageloader.sample.fragment.fallback;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Matt Allen
 * 16/08/15
 * mattallen092@gmail.com
 */
public class FallbackTestPagerAdapter extends FragmentPagerAdapter
{
	private Fragment mOptionsArray;
	private Fragment mOptions;
	private Fragment mStringArray;

	public FallbackTestPagerAdapter(FragmentManager fm)
	{
		super(fm);
	}

	@Override
	public Fragment getItem(int position)
	{
		switch (position)
		{
			case 0:
				if (mOptionsArray == null)
				{
					mOptionsArray = new FallbackImageServiceArrayFragment();
				}
				return mOptionsArray;

			case 1:
				if (mOptions == null)
				{
					mOptions = new FallbackImageServiceFragment();
				}
				return mOptions;

			case 2:
				if (mStringArray == null)
				{
					mStringArray = new FallbackStringArrayFragment();
				}
				return mStringArray;

			default:
				return null;
		}
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		switch (position)
		{
			case 0:
				return "Image Service Options Array";

			case 1:
				return "Image Service Options";

			case 2:
				return "String Array";
		}
		return super.getPageTitle(position);
	}

	@Override
	public int getCount()
	{
		return 3;
	}
}
