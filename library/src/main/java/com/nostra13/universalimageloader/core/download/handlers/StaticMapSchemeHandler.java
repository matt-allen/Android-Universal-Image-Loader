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
public class StaticMapSchemeHandler extends SchemeHandler
{
	@Override
	public InputStream getStreamForPath(Context context, String path, Object optionForDownloader, int connectTimeout, int readTimeout)
	{
		try
		{
			String[] splitString = path.split("/");
			String getUrl = createUrl(Double.parseDouble(splitString[splitString.length - 2]), Double.parseDouble(splitString[splitString.length - 1]));
			return getStreamFromNetwork(getUrl, connectTimeout, readTimeout, optionForDownloader);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	protected String createUrl(double lat, double lng)
	{
		return "https://maps.googleapis.com/maps/api/staticmap?center=" + lat +"," + lng + "&zoom=11&size=800x300&markers=color:red";
	}
}
