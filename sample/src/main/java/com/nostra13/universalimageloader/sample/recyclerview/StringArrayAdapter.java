package com.nostra13.universalimageloader.sample.recyclerview;

/**
 * Created by Matt Allen
 * 16/08/15
 * mattallen092@gmail.com
 */
public class StringArrayAdapter extends BaseAdapter<String[]>
{
	@Override
	public void onBindViewHolder(ViewHolder holder, int position)
	{
		holder.setImage(getItems().get(position));
	}
}
