package com.nostra13.universalimageloader.sample.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.image.ImageServiceOptions;
import com.nostra13.universalimageloader.sample.R;

/**
 * Created by Matt Allen
 * 08/08/15
 * mattallen092@gmail.com
 */
public class ViewHolder extends RecyclerView.ViewHolder
{
	private ImageView mImage;

	public ViewHolder(View itemView)
	{
		super(itemView);
		mImage = (ImageView)itemView.findViewById(R.id.image);
	}

	public void setImage(ImageServiceOptions image)
	{
		ImageLoader.getInstance().displayImage(image, mImage);
	}

	public void setImage(ImageServiceOptions[] images)
	{
		ImageLoader.getInstance().displayImage(images, mImage);
	}

	public void setImage(String imageUrl)
	{
		ImageLoader.getInstance().displayImage(imageUrl, mImage);
	}

	public void setImage(String[] images)
	{
		ImageLoader.getInstance().displayImage(images, mImage);
	}
}
