package com.nostra13.universalimageloader.core.model;

import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * // TODO Add class description
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public class StaticMapImage implements ImageServiceOptions
{
	private static final String URL = "https://maps.googleapis.com/maps/api/staticmap?center=%s,%s&zoom=%d&size=%dx%d";
	public static final String BLACK = "black";
	public static final String BROWN = "brown";
	public static final String GREEN = "green";
	public static final String PURPLE = "purple";
	public static final String YELLOW = "yellow";
	public static final String BLUE = "blue";
	public static final String GRAY = "gray";
	public static final String ORANGE = "orange";
	public static final String RED = "red";
	public static final String WHITE = "white";

	private double latitude, longitude;
	private int width, height, zoom;
	private List<Marker> markers;

	public StaticMapImage(double latitude, double longitude, int width, int height, int zoom, List<Marker> markers)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.width = width;
		this.height = height;
		this.zoom = zoom;
		this.markers = markers;
	}

	public StaticMapImage(double latitude, double longitude)
	{
		this(latitude, longitude, 800, 400, 11, null);
	}

	public void addMarker(Marker marker)
	{
		if (markers == null)
		{
			markers = new ArrayList<>();
		}
		markers.add(marker);
	}

	public void setMarkers(List<Marker> markers)
	{
		this.markers = markers;
	}

	@Override
	public String createUrl()
	{
		String url = String.format(URL, String.valueOf(latitude), String.valueOf(longitude), zoom, width, height);
		if (markers != null && markers.size() > 0)
		{
			for (Marker marker : markers)
			{
				url += marker.createUrl();
			}
		}
		return url;
	}

	@Override
	public void fromUrl(String url)
	{
		// Not needed
	}

	public static class Marker implements ImageServiceOptions
	{
		private double latitude, longitude;
		private String colour, label;

		public Marker(String label, double latitude, double longitude, String colour)
		{
			this.label = label;
			this.latitude = latitude;
			this.longitude = longitude;
			this.colour = colour;
		}

		public Marker(double latitude, double longitude, String colour)
		{
			this(null, latitude, longitude, colour);
		}

		@Override
		public String createUrl()
		{
			String base = "&markers=color:" + colour;
			if (!TextUtils.isEmpty(label))
			{
				base += "%7Clabel:" + label.substring(0, 1);
			}
			base += "%7C" + String.valueOf(latitude) + "," + String.valueOf(longitude);
			return base;
		}

		@Override
		public void fromUrl(String url)
		{
			// Not needed
		}
	}
}
