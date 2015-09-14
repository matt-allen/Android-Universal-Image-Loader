package com.nostra13.universalimageloader.core.listener;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

/**
 * // TODO Add class description
 *
 * @author Matt Allen
 * @project Versy Prototype
 */
public class MultipleImageLoadingListener implements ImageLoadingListener
{
	private ImageLoadingListener topLevelListener;
	private String[] urls;
	private int currentIndex = 0;
	private ImageAware imageView;

	public MultipleImageLoadingListener(ImageAware imageView, ImageLoadingListener topLevelListener, String[] urls)
	{
		this.imageView = imageView;
		this.topLevelListener = topLevelListener;
		this.urls = urls;
	}

	@Override
	public void onLoadingStarted(String imageUri, View view)
	{
		if (topLevelListener != null) topLevelListener.onLoadingStarted(imageUri, view);
	}

	@Override
	public void onLoadingFailed(String imageUri, View view, FailReason failReason)
	{
		if (topLevelListener != null) topLevelListener.onLoadingFailed(imageUri, view, failReason);
		currentIndex++;
		if (urls.length > currentIndex)
		{
			ImageLoader.getInstance().loadImage(urls[currentIndex], this);
		}
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
	{
		if (topLevelListener != null) topLevelListener.onLoadingComplete(imageUri, view, loadedImage);
		imageView.setImageBitmap(loadedImage);
	}

	@Override
	public void onLoadingCancelled(String imageUri, View view)
	{
		if (topLevelListener != null) topLevelListener.onLoadingCancelled(imageUri, view);
		currentIndex++;
		if (urls.length > currentIndex)
		{
			ImageLoader.getInstance().loadImage(urls[currentIndex], this);
		}
	}
}
