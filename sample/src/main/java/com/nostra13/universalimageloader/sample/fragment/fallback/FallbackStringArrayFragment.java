package com.nostra13.universalimageloader.sample.fragment.fallback;

import com.nostra13.universalimageloader.sample.recyclerview.BaseAdapter;
import com.nostra13.universalimageloader.sample.recyclerview.StringArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt Allen
 * 16/08/15
 * mattallen092@gmail.com
 */
public class FallbackStringArrayFragment extends BaseFallbackImageFragment<String[]>
{

	@Override
	protected BaseAdapter<String[]> getAdapter()
	{
		return new StringArrayAdapter();
	}

	@Override
	protected List<String[]> getDataSet()
	{
		List<String[]> strings = new ArrayList<>();
		strings.add(new String[]{"https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg"});
		return strings;
	}
}
