package com.nostra13.universalimageloader.sample.fragment.fallback;

import com.nostra13.universalimageloader.image.GravatarImage;
import com.nostra13.universalimageloader.image.ImageServiceOptions;
import com.nostra13.universalimageloader.image.SimpleTextImage;
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
		data.add(new StaticMapImage(51.101797, -1.219482));
		data.add(new StaticMapImage(51.813709, -1.285400));
		data.add(new SimpleTextImage("Tom Jones", SimpleTextImage.COLOUR_DEEP_ORANGE));
		data.add(new StaticMapImage.Builder()
				.latitude(51.813709)
				.longitude(-1.285400)
				.addMarker(new StaticMapImage.Marker("One", 51.813709, -1.285405, StaticMapImage.GREEN))
				.defaultMarker(true)
				.build());
		data.add(new GravatarImage("callum@3sidedcube.com"));
		return data;
	}
}
