package com.nostra13.universalimageloader.core.download.handlers;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * // TODO Class doc
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public class HttpSchemeHandler extends SchemeHandler
{
	@Override
	public InputStream getStreamForPath(Context context, String path, Object optionForDownloader, int connectTimeout, int readTimeout)
	{
		try
		{
			return getStreamFromNetwork(path, connectTimeout, readTimeout, optionForDownloader);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
