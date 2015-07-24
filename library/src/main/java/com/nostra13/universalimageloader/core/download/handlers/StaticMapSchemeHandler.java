package com.nostra13.universalimageloader.core.download.handlers;

import android.content.Context;

import java.io.InputStream;

/**
 * // TODO Class doc
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public class StaticMapSchemeHandler extends SchemeHandler
{
	@Override
	public InputStream getStreamForPath(Context context, String path, Object optionForDownloader, int connectTimeout, int readTimeout)
	{
		return null;
	}
}
