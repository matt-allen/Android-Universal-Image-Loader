package com.nostra13.universalimageloader.core.helper;

/**
 * // TODO Class doc
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public class GoogleMapsServiceHelper
{
	public static String getUriForLocation(double latitude, double longitude)
	{
		return "maps://" + String.valueOf(latitude) + "/" + String.valueOf(longitude);
	}
}
