package com.nostra13.universalimageloader.image;

/**
 * Image options for the Flickr service. When this is passed into
 * the loader, it triggers the custom scheme handler for this scheme.
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public class FlickrImage implements ImageServiceOptions
{
	private double latitude, longitude;

	public FlickrImage(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	@Override
	public String createUrl()
	{
		return "flickr://" + String.valueOf(latitude) + "/" + String.valueOf(longitude);
	}

	@Override
	public void fromUrl(String url)
	{
		String[] splitString = url.split("/");
		latitude = Double.parseDouble(splitString[splitString.length - 2]);
		longitude = Double.parseDouble(splitString[splitString.length - 1]);
	}
}
