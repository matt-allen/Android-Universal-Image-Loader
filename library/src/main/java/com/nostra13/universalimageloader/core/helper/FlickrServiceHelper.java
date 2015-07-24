package com.nostra13.universalimageloader.core.helper;

/**
 * Created by Matt Allen
 * 24/07/15
 * mattallen092@gmail.com
 */
public class FlickrServiceHelper
{
	private static String flickrApiKey;

	public static void setApiKey(String key)
	{
		flickrApiKey = key;
	}

	public static String getApiKey()
	{
		return flickrApiKey;
	}

	public static String getUriForLocation(double latitude, double longitude)
	{
		return "flickr://" + String.valueOf(latitude) + "/" + String.valueOf(longitude);
	}
}
