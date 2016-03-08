package com.nostra13.universalimageloader.image;

import android.text.TextUtils;

import com.nostra13.universalimageloader.core.helper.GoogleServiceHelper;

import java.util.Locale;

/**
 * Create a URL for accessing images from the Google Street View Image API
 *
 * @author Matt Allen
 */
public class StreetViewImage implements ImageServiceOptions
{
	private static final String URL_START = "https://maps.googleapis.com/maps/api/streetview?";

	private double latitude, longitude;
	private int width, height, fov, pitch, heading;
	private String location;

	public StreetViewImage(double latitude, double longitude)
	{
		this(latitude, longitude, 640, 480);
	}

	public StreetViewImage(double latitude, double longitude, int width, int height)
	{
		this(latitude, longitude, width, height, 90, 10, -1);
	}

	public StreetViewImage(double latitude, double longitude, int width, int height, int fieldOfView, int pitch, int heading)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.width = width;
		this.height = height;
		this.fov = fieldOfView;
		this.pitch = pitch;
		this.heading = heading;
	}

	public StreetViewImage(String locationName)
	{
		this(locationName, 640, 480);
	}

	public StreetViewImage(String locationName, int width, int height)
	{
		this(locationName, width, height, 90, 10, -1);
	}

	public StreetViewImage(String locationName, int width, int height, int fieldOfView, int pitch, int heading)
	{
		this.location = locationName;
		this.width = width;
		this.height = height;
		this.fov = fieldOfView;
		this.pitch = pitch;
		this.heading = heading;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getFieldOfView()
	{
		return fov;
	}

	public void setFieldOfView(int fov)
	{
		this.fov = fov;
	}

	public int getPitch()
	{
		return pitch;
	}

	public void setPitch(int pitch)
	{
		this.pitch = pitch;
	}

	public int getHeading()
	{
		return heading;
	}

	public void setHeading(int heading)
	{
		this.heading = heading;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	@Override
	public String createUrl()
	{
		StringBuilder builder = new StringBuilder(URL_START);
		if (!TextUtils.isEmpty(location))
		{
			builder.append(String.format("location=%s", location)); // URL escape this string
		}
		else
		{
			builder.append(String.format("location=%s,%s", String.valueOf(latitude), String.valueOf(longitude)));
		}

		if (TextUtils.isEmpty(GoogleServiceHelper.getStreetViewApiKey()))
		{
			throw new NullPointerException("API key for Street View API not set. Set now by using GoogleServiceHelper#setStreetViewApiKey(String)");
		}

		builder.append(String.format(Locale.US, "&size=%dx%s", width, height));
		builder.append(String.format("&key=%s", GoogleServiceHelper.getStreetViewApiKey()));
		builder.append(String.format(Locale.US, "&pitch=%d", pitch));
		builder.append(String.format(Locale.US, "&fov=%d", fov));

		if (heading >= 0)
		{
			builder.append(String.format(Locale.US, "&heading=%d", heading));
		}

		return builder.toString();
	}

	@Override
	public void fromUrl(String url)
	{
		// Not needed
	}
}
