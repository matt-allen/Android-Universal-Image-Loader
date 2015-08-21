package com.nostra13.universalimageloader.sample.recyclerview;

import com.nostra13.universalimageloader.image.ImageServiceOptions;

/**
 * Created by Matt Allen
 * 16/08/15
 * mattallen092@gmail.com
 */
public class ServiceOptionsArrayAdapter extends BaseAdapter<ImageServiceOptions[]>
{
	@Override
	public void onBindViewHolder(ViewHolder holder, int position)
	{
		holder.setImage(getItems().get(position));
	}
}
