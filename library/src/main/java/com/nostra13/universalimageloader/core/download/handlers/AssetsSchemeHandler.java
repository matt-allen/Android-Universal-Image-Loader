package com.nostra13.universalimageloader.core.download.handlers;

import android.content.Context;

import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Base class for all image stream opening. Includes basic network connectivity methods
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public class AssetsSchemeHandler extends SchemeHandler
{
	@Override
	public InputStream getStreamForPath(Context context, String path, Object optionForDownloader, int connectTimeout, int readTimeout)
	{
		try
		{
			String filePath = ImageDownloader.Scheme.ASSETS.crop(path);
			return context.getAssets().open(filePath);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
