package com.nostra13.universalimageloader.core.model;

/**
 * // TODO Add class description
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public class StaticMapImage implements ImageServiceOptions
{
	private static final String URL = "https://maps.googleapis.com/maps/api/staticmap?center=%s,%s&zoom=%d&size=%dx%d&markers=color:%s";
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
	private String colour;

	public StaticMapImage(double latitude, double longitude, int width, int height, int zoom, String colour)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.width = width;
		this.height = height;
		this.zoom = zoom;
		this.colour = colour;
	}

	public StaticMapImage(double latitude, double longitude)
	{
		this(latitude, longitude, 800, 400, 11, BLUE);
	}

	@Override
	public String createUrl()
	{
		return String.format(URL, String.valueOf(latitude), String.valueOf(longitude), zoom, width, height, colour);
	}

	@Override
	public void fromUrl(String url)
	{
		// Not needed
	}
}
