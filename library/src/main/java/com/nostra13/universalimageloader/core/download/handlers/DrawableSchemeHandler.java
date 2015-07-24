package com.nostra13.universalimageloader.core.download.handlers;

import android.content.Context;

import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.InputStream;

/**
 * Created by Matt Allen
 * 24/07/15
 * mattallen092@gmail.com
 */
public class DrawableSchemeHandler extends SchemeHandler
{
	@Override
	public InputStream getStreamForPath(Context context, String path, Object optionForDownloader, int connectTimeout, int readTimeout)
	{
		String drawableIdString = ImageDownloader.Scheme.DRAWABLE.crop(path);
		int drawableId = Integer.parseInt(drawableIdString);
		return context.getResources().openRawResource(drawableId);
	}
}
