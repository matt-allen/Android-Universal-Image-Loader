package com.nostra13.universalimageloader.core.helper;

/**
 * Simply a place to store API options for the Google services used
 * by the image loader scheme handlers.
 *
 * @author Matt Allen
 */
public class GoogleServiceHelper
{
	private static String streetViewApiKey;

	public static String getStreetViewApiKey()
	{
		return streetViewApiKey;
	}

	public static void setStreetViewApiKey(String streetViewApiKey)
	{
		GoogleServiceHelper.streetViewApiKey = streetViewApiKey;
	}
}
