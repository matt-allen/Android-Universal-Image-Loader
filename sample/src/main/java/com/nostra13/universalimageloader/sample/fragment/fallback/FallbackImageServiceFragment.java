package com.nostra13.universalimageloader.sample.fragment.fallback;

import com.nostra13.universalimageloader.image.GravatarImage;
import com.nostra13.universalimageloader.image.ImageServiceOptions;
import com.nostra13.universalimageloader.image.StaticMapImage;
import com.nostra13.universalimageloader.sample.recyclerview.BaseAdapter;
import com.nostra13.universalimageloader.sample.recyclerview.ServiceOptionsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt Allen
 * 16/08/15
 * mattallen092@gmail.com
 */
public class FallbackImageServiceFragment extends BaseFallbackImageFragment<ImageServiceOptions>
{
	@Override
	protected BaseAdapter<ImageServiceOptions> getAdapter()
	{
		return new ServiceOptionsAdapter();
	}

	@Override
	protected List<ImageServiceOptions> getDataSet()
	{
		List<ImageServiceOptions> data = new ArrayList<>();
		data.add(new GravatarImage("matt.allen@3sidedcube.com"));
		data.add(new StaticMapImage(50.29384, -1.92367));
		return data;
	}
}
